package me.jellysquid.mods.sodium.client.render;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import io.themade4.relictium.Relictium;
import me.jellysquid.mods.sodium.client.gl.compat.FogHelper;
import me.jellysquid.mods.sodium.client.gl.device.RenderDevice;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.model.vertex.type.ChunkVertexType;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderManager;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.oneshot.ChunkRenderBackendOneshot;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.format.DefaultModelVertexFormats;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPassManager;
import me.jellysquid.mods.sodium.client.render.pipeline.context.ChunkRenderCacheShared;
import me.jellysquid.mods.sodium.client.util.math.FrustumExtended;
import me.jellysquid.mods.sodium.client.world.ChunkStatusListener;
import me.jellysquid.mods.sodium.client.world.ChunkStatusListenerManager;
import me.jellysquid.mods.sodium.common.util.ListUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.Map;
import java.util.Set;

/**
 * Provides an extension to vanilla's {@link net.minecraft.client.renderer.RenderGlobal}.
 */
public class SodiumWorldRenderer implements ChunkStatusListener {
    private static SodiumWorldRenderer instance;

    private final Minecraft client;

    private WorldClient world;
    private int renderDistance;

    private double lastCameraX, lastCameraY, lastCameraZ;
    private double lastCameraPitch, lastCameraYaw;
    private float lastFogDistance;

    private boolean useEntityCulling;

    private final LongSet loadedChunkPositions = new LongOpenHashSet();
    private final Set<TileEntity> globalBlockEntities = new ObjectOpenHashSet<>();

    private Frustum frustum;
    private ChunkRenderManager<?> chunkRenderManager;
    private BlockRenderPassManager renderPassManager;
    private ChunkRenderBackend<?> chunkRenderBackend;

    /**
     * Instantiates Sodium's world renderer. This should be called at the time of the world renderer initialization.
     */
    public static SodiumWorldRenderer create() {
        if (instance == null) {
            instance = new SodiumWorldRenderer(Minecraft.getMinecraft());
        }

        return instance;
    }

    /**
     * @throws IllegalStateException If the renderer has not yet been created
     * @return The current instance of this type
     */
    @Deprecated
    public static SodiumWorldRenderer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Renderer not initialized");
        }

        return instance;
    }

    /**
     * @return The current instance of the Sodium terrain renderer, or null if the renderer is not active
     */
    public static SodiumWorldRenderer getInstanceNullable() {
        return instance;
    }

    private SodiumWorldRenderer(Minecraft client) {
        this.client = client;
    }

    public void setWorld(WorldClient world) {
        // Check that the world is actually changing
        if (this.world == world) {
            return;
        }

        // If we have a world is already loaded, unload the renderer
        if (this.world != null) {
            this.unloadWorld();
        }

        // If we're loading a new world, load the renderer
        if (world != null) {
            this.loadWorld(world);
        }
    }

    private void loadWorld(WorldClient world) {
        this.world = world;

        ChunkRenderCacheShared.createRenderContext(this.world);

        this.initRenderer();

        ((ChunkStatusListenerManager) world.getChunkProvider()).setListener(this);
    }

    private void unloadWorld() {
        ChunkRenderCacheShared.destroyRenderContext(this.world);

        if (this.chunkRenderManager != null) {
            this.chunkRenderManager.destroy();
            this.chunkRenderManager = null;
        }

        if (this.chunkRenderBackend != null) {
            this.chunkRenderBackend.delete();
            this.chunkRenderBackend = null;
        }

        this.loadedChunkPositions.clear();
        this.globalBlockEntities.clear();

        this.world = null;
    }

    /**
     * @return The number of chunk renders which are visible in the current camera's frustum
     */
    public int getVisibleChunkCount() {
        return this.chunkRenderManager.getVisibleChunkCount();
    }

    /**
     * Notifies the chunk renderer that the graph scene has changed and should be re-computed.
     */
    public void scheduleTerrainUpdate() {
        // BUG: seems to be called before init
        if (this.chunkRenderManager != null) {
            this.chunkRenderManager.markDirty();
        }
    }

    /**
     * @return True if no chunks are pending rebuilds
     */
    public boolean isTerrainRenderComplete() {
        return this.chunkRenderManager.isBuildComplete();
    }

    // We'll keep it to have compatibility with Oculus' older versions
    public static boolean hasChanges = false;
    
    /**
     * Called prior to any chunk rendering in order to update necessary state.
     */
    public void updateChunks(Frustum frustum, float ticks, boolean hasForcedFrustum, int frame, boolean spectator) {
        this.frustum = frustum;

        this.useEntityCulling = Relictium.options().advanced.useEntityCulling;

        if (this.client.gameSettings.renderDistanceChunks != this.renderDistance) {
            this.reload();
        }

        Profiler profiler = this.client.profiler;
        profiler.startSection("camera_setup");

        Entity viewEntity = this.client.getRenderViewEntity();

        if (viewEntity == null) {
            throw new IllegalStateException("Client instance has no active render entity");
        }

        double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * ticks;
        double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * ticks + (double) viewEntity.getEyeHeight();
        double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * ticks;

        this.chunkRenderManager.setCameraPosition(x, y, z);

        float fogDistance = FogHelper.getFogCutoff();

        boolean dirty = x != this.lastCameraX || y != this.lastCameraY ||
                z != this.lastCameraZ || (double) viewEntity.rotationPitch != this.lastCameraPitch |
                (double) viewEntity.rotationYaw != this.lastCameraYaw;

        if (dirty) {
            this.chunkRenderManager.markDirty();
        }

        this.lastCameraX = x;
        this.lastCameraY = y;
        this.lastCameraZ = z;
        this.lastCameraPitch = viewEntity.rotationPitch;
        this.lastCameraYaw = viewEntity.rotationYaw;
        this.lastFogDistance = fogDistance;

        profiler.endStartSection("chunk_update");

        this.chunkRenderManager.updateChunks();

        if (!hasForcedFrustum && this.chunkRenderManager.isDirty()) {
            profiler.endStartSection("chunk_graph_rebuild");

            this.chunkRenderManager.update(ticks, (FrustumExtended) frustum, frame, spectator);
        }

        profiler.endStartSection("visible_chunk_tick");

        this.chunkRenderManager.tickVisibleRenders();

        profiler.endSection();

        // TODO distance checking option
        Entity.setRenderDistanceWeight(MathHelper.clamp((double) this.client.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D) * 2000);
    }

    /**
     * Performs a render pass for the given {@link BlockRenderLayer} and draws all visible chunks for it.
     */
    public void drawChunkLayer(BlockRenderLayer renderLayer, double x, double y, double z) {
        BlockRenderPass pass = this.renderPassManager.getRenderPassForLayer(renderLayer);
        // TODO startDrawing/endDrawing are handled by 1.12 already
        //pass.startDrawing();

        this.chunkRenderManager.renderLayer(pass, x, y, z);

        //pass.endDrawing();

        GlStateManager.resetColor();
    }

    public void reload() {
        if (this.world == null) {
            return;
        }

        this.initRenderer();
    }

    private void initRenderer() {
        if (this.chunkRenderManager != null) {
            this.chunkRenderManager.destroy();
            this.chunkRenderManager = null;
        }

        if (this.chunkRenderBackend != null) {
            this.chunkRenderBackend.delete();
            this.chunkRenderBackend = null;
        }

        this.globalBlockEntities.clear();

        RenderDevice device = RenderDevice.INSTANCE;

        this.renderDistance = this.client.gameSettings.renderDistanceChunks;

        SodiumGameOptions opts = Relictium.options();

        this.renderPassManager = BlockRenderPassManager.createDefaultMappings();

        final ChunkVertexType vertexFormat;

        if (opts.advanced.useCompactVertexFormat) {
            vertexFormat = DefaultModelVertexFormats.MODEL_VERTEX_HFP;
        } else {
            vertexFormat = DefaultModelVertexFormats.MODEL_VERTEX_SFP;
        }

        this.chunkRenderBackend = createChunkRenderBackend(device, opts, vertexFormat);
        this.chunkRenderBackend.createShaders(device);

        this.chunkRenderManager = new ChunkRenderManager<>(this, this.chunkRenderBackend, this.renderPassManager, this.world, this.renderDistance);
        this.chunkRenderManager.restoreChunks(this.loadedChunkPositions);
    }

    private static ChunkRenderBackend<?> createChunkRenderBackend(RenderDevice device,
                                                                  SodiumGameOptions options,
                                                                  ChunkVertexType vertexFormat) {
        boolean disableBlacklist = Relictium.options().advanced.ignoreDriverBlacklist;

        if (options.advanced.useChunkMultidraw && MultidrawChunkRenderBackend.isSupported(disableBlacklist)) {
            return new MultidrawChunkRenderBackend(device, vertexFormat);
        } else {
            return new ChunkRenderBackendOneshot(vertexFormat);
        }
    }

    private boolean checkBEVisibility(TileEntity entity) {
        return frustum.isBoundingBoxInFrustum(entity.getRenderBoundingBox());
    }

    private void renderTE(TileEntity tileEntity, int pass, float partialTicks, int damageProgress) {
        if((damageProgress < 0 && !tileEntity.shouldRenderInPass(pass)) || !checkBEVisibility(tileEntity))
            return;

        try {
            TileEntityRendererDispatcher.instance.render(tileEntity, partialTicks, damageProgress);
        } catch(RuntimeException e) {
            if(tileEntity.isInvalid()) {
                Relictium.logger().error("Suppressing crash from invalid tile entity", e);
            } else {
                throw e;
            }
        }
    }

    private void preRenderDamagedBlocks() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.doPolygonOffset(-1.0F, -10.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
    }

    private void postRenderDamagedBlocks() {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public void renderTileEntities(float partialTicks, Map<Integer, DestroyBlockProgress> damagedBlocks) {
        int pass = MinecraftForgeClient.getRenderPass();
        TileEntityRendererDispatcher.instance.preDrawBatch();
        for (TileEntity tileEntity : this.chunkRenderManager.getVisibleBlockEntities()) {
            renderTE(tileEntity, pass, partialTicks, -1);
        }

        for (TileEntity tileEntity : this.globalBlockEntities) {
            renderTE(tileEntity, pass, partialTicks, -1);
        }
        TileEntityRendererDispatcher.instance.drawBatch(pass);

        this.preRenderDamagedBlocks();
        for (DestroyBlockProgress destroyProgress : damagedBlocks.values()) {
            BlockPos pos = destroyProgress.getPosition();

            if (this.world.getBlockState(pos).getBlock().hasTileEntity()) {
                TileEntity tileEntity = this.world.getTileEntity(pos);

                if (tileEntity instanceof TileEntityChest) {
                    TileEntityChest chest = (TileEntityChest) tileEntity;

                    if (chest.adjacentChestXNeg != null) {
                        pos = pos.offset(EnumFacing.WEST);
                        tileEntity = this.world.getTileEntity(pos);
                    } else if (chest.adjacentChestZNeg != null) {
                        pos = pos.offset(EnumFacing.NORTH);
                        tileEntity = this.world.getTileEntity(pos);
                    }
                }

                IBlockState state = this.world.getBlockState(pos);
                if (tileEntity != null && state.hasCustomBreakingProgress()) {
                    renderTE(tileEntity, pass, partialTicks, destroyProgress.getPartialBlockDamage());
                }
            }
        }
        this.postRenderDamagedBlocks();
    }

    @Override
    public void onChunkAdded(int x, int z) {
        this.loadedChunkPositions.add(ChunkPos.asLong(x, z));
        this.chunkRenderManager.onChunkAdded(x, z);
    }

    @Override
    public void onChunkRemoved(int x, int z) {
        this.loadedChunkPositions.remove(ChunkPos.asLong(x, z));
        this.chunkRenderManager.onChunkRemoved(x, z);
    }

    public void onChunkRenderUpdated(int x, int y, int z, ChunkRenderData meshBefore, ChunkRenderData meshAfter) {
        ListUtil.updateList(this.globalBlockEntities, meshBefore.getGlobalBlockEntities(), meshAfter.getGlobalBlockEntities());
        
        this.chunkRenderManager.onChunkRenderUpdates(x, y, z, meshAfter);
    }

    private static boolean isInfiniteExtentsBox(AxisAlignedBB box) {
        return Double.isInfinite(box.minX) || Double.isInfinite(box.minY) || Double.isInfinite(box.minZ)
            || Double.isInfinite(box.maxX) || Double.isInfinite(box.maxY) || Double.isInfinite(box.maxZ);
    }

    /**
     * Returns whether or not the entity intersects with any visible chunks in the graph.
     * @return True if the entity is visible, otherwise false
     */
    public boolean isEntityVisible(Entity entity) {
        if (!this.useEntityCulling) {
            return true;
        }

        AxisAlignedBB box = entity.getRenderBoundingBox();

        // Entities outside the valid world height will never map to a rendered chunk
        // Always render these entities or they'll be culled incorrectly!
        if (box.maxY < 0.5D || box.minY > 255.5D) {
            return true;
        }

        if (isInfiniteExtentsBox(box)) {
            return true;
        }

        // Ensure entities with outlines or nametags are always visible
        if (entity.isGlowing() || entity.getAlwaysRenderNameTagForRender()) {
            return true;
        }

        int minX = MathHelper.floor(box.minX - 0.5D) >> 4;
        int minY = MathHelper.floor(box.minY - 0.5D) >> 4;
        int minZ = MathHelper.floor(box.minZ - 0.5D) >> 4;

        int maxX = MathHelper.floor(box.maxX + 0.5D) >> 4;
        int maxY = MathHelper.floor(box.maxY + 0.5D) >> 4;
        int maxZ = MathHelper.floor(box.maxZ + 0.5D) >> 4;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    if (this.chunkRenderManager.isChunkVisible(x, y, z)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @return The frustum of the current player's camera used to cull chunks
     */
    public Frustum getFrustum() {
        return this.frustum;
    }

    public String getChunksDebugString() {
        // C: visible/total
        return String.format("C: %s/%s Q: %s+%si ", this.chunkRenderManager.getVisibleChunkCount(), this.chunkRenderManager.getTotalSections(), this.chunkRenderManager.getRebuildQueueSize(), this.chunkRenderManager.getImportantRebuildQueueSize());
    }

    /**
     * Schedules chunk rebuilds for all chunks in the specified block region.
     */
    public void scheduleRebuildForBlockArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean important) {
        this.scheduleRebuildForChunks(minX >> 4, minY >> 4, minZ >> 4, maxX >> 4, maxY >> 4, maxZ >> 4, important);
    }

    /**
     * Schedules chunk rebuilds for all chunks in the specified chunk region.
     */
    public void scheduleRebuildForChunks(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean important) {
        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkY = minY; chunkY <= maxY; chunkY++) {
                for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                    this.scheduleRebuildForChunk(chunkX, chunkY, chunkZ, important);
                }
            }
        }
    }

    /**
     * Schedules a chunk rebuild for the render belonging to the given chunk section position.
     */
    public void scheduleRebuildForChunk(int x, int y, int z, boolean important) {
        this.chunkRenderManager.scheduleRebuild(x, y, z, important);
    }

    public ChunkRenderBackend<?> getChunkRenderer() {
        return this.chunkRenderBackend;
    }
}
