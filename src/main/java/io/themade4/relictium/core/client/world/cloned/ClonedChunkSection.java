package io.themade4.relictium.core.client.world.cloned;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import io.themade4.relictium.core.client.util.math.ChunkSectionPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClonedChunkSection {
    private static final ExtendedBlockStorage EMPTY_SECTION = new ExtendedBlockStorage(0, false);

    private final AtomicInteger referenceCount = new AtomicInteger(0);
    private final ClonedChunkSectionCache backingCache;

    private final Short2ObjectMap<TileEntity> blockEntities;
    private final World world;

    private ChunkSectionPos pos;

    private ExtendedBlockStorage data;

    private Biome[] biomeData;

    private byte[][] lightData;

    private long lastUsedTimestamp = Long.MAX_VALUE;

    ClonedChunkSection(ClonedChunkSectionCache backingCache, World world) {
        this.backingCache = backingCache;
        this.world = world;
        this.blockEntities = new Short2ObjectOpenHashMap<>();
    }

    public void init(ChunkSectionPos pos) {
        Chunk chunk = world.getChunk(pos.getX(), pos.getZ());

        if (chunk == null) {
            throw new RuntimeException("Couldn't retrieve chunk at " + pos.toChunkPos());
        }

        ExtendedBlockStorage section = getChunkSection(chunk, pos);

        if (section == Chunk.NULL_BLOCK_STORAGE/*ChunkSection.isEmpty(section)*/) {
            section = EMPTY_SECTION;
        }

        this.pos = pos;
        this.data = section;

        this.biomeData = new Biome[chunk.getBiomeArray().length];

        StructureBoundingBox box = new StructureBoundingBox(pos.getMinX(), pos.getMinY(), pos.getMinZ(), pos.getMaxX(), pos.getMaxY(), pos.getMaxZ());

        this.blockEntities.clear();

        for (Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
            BlockPos entityPos = entry.getKey();

            if (box.isVecInside(entityPos)) {
                //this.blockEntities.put(BlockPos.asLong(entityPos.getX() & 15, entityPos.getY() & 15, entityPos.getZ() & 15), entry.getValue());
            	this.blockEntities.put(ChunkSectionPos.packLocal(entityPos), entry.getValue());
            }
        }

        BlockPos.MutableBlockPos biomePos = new BlockPos.MutableBlockPos();
        // Fill biome data
        for(int z = pos.getMinZ(); z <= pos.getMaxZ(); z++) {
            for(int x = pos.getMinX(); x <= pos.getMaxX(); x++) {
                biomePos.setPos(x, 100, z);
                this.biomeData[((z & 15) << 4) | (x & 15)] = world.getBiome(biomePos);
            }
        }
    }

    public IBlockState getBlockState(int x, int y, int z) {
        return data.get(x, y, z);
    }

    public Biome getBiomeForNoiseGen(int x, int z) {
        return this.biomeData[x | z << 4];
    }

    public Biome[] getBiomeData() {
        return this.biomeData;
    }

    public TileEntity getBlockEntity(int x, int y, int z) {
        return this.blockEntities.get(packLocal(x, y, z));
    }

    public ChunkSectionPos getPosition() {
        return this.pos;
    }

    public int getLightLevel(int x, int y, int z, EnumSkyBlock type) {
        NibbleArray lightArray = type == EnumSkyBlock.BLOCK ? this.data.getBlockLight() : this.data.getSkyLight();
        return lightArray != null ? lightArray.get(x, y, z) : type.defaultLightValue;
    }

    private static ExtendedBlockStorage getChunkSection(Chunk chunk, ChunkSectionPos pos) {
        ExtendedBlockStorage section = null;

        if (!isOutsideBuildHeight(ChunkSectionPos.getBlockCoord(pos.getY()))) {
            section = chunk.getBlockStorageArray()[pos.getY()];
        }

        return section;
    }

    private static boolean isOutsideBuildHeight(int y) {
        return y < 0 || y >= 256;
    }

    public void acquireReference() {
        this.referenceCount.incrementAndGet();
    }

    public boolean releaseReference() {
        return this.referenceCount.decrementAndGet() <= 0;
    }

    public long getLastUsedTimestamp() {
        return this.lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long timestamp) {
        this.lastUsedTimestamp = timestamp;
    }

    public ClonedChunkSectionCache getBackingCache() {
        return this.backingCache;
    }
    
    /**
     * @param x The local x-coordinate
     * @param y The local y-coordinate
     * @param z The local z-coordinate
     * @return An index which can be used to key entities or blocks within a chunk
     */
    private static short packLocal(int x, int y, int z) {
        return (short) (x << 8 | z << 4 | y);
    }
}
