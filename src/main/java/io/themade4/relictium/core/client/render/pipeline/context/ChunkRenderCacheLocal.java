package io.themade4.relictium.core.client.render.pipeline.context;

import io.themade4.relictium.core.client.model.light.LightPipelineProvider;
import io.themade4.relictium.core.client.model.light.cache.ArrayLightDataCache;
import io.themade4.relictium.core.client.model.quad.blender.BiomeColorBlender;
import io.themade4.relictium.core.client.render.pipeline.BlockRenderer;
import io.themade4.relictium.core.client.render.pipeline.ChunkRenderCache;
import io.themade4.relictium.core.client.render.pipeline.FluidRenderer;
import io.themade4.relictium.core.client.world.WorldSlice;
import io.themade4.relictium.core.client.world.WorldSliceLocal;
import io.themade4.relictium.core.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.world.World;

public class ChunkRenderCacheLocal extends ChunkRenderCache {
    private final ArrayLightDataCache lightDataCache;

    private final BlockRenderer blockRenderer;
    private final FluidRenderer fluidRenderer;

    private final BlockModelShapes blockModels;
    private final WorldSlice worldSlice;
    private WorldSliceLocal localSlice;

    public ChunkRenderCacheLocal(Minecraft client, World world) {
        this.worldSlice = new WorldSlice(world);
        this.lightDataCache = new ArrayLightDataCache(this.worldSlice);

        LightPipelineProvider lightPipelineProvider = new LightPipelineProvider(this.lightDataCache);
        BiomeColorBlender biomeColorBlender = this.createBiomeColorBlender();

        this.blockRenderer = new BlockRenderer(client, lightPipelineProvider, biomeColorBlender);
        this.fluidRenderer = new FluidRenderer(client, lightPipelineProvider, biomeColorBlender);

        this.blockModels = client.getBlockRendererDispatcher().getBlockModelShapes();
    }

    public BlockModelShapes getBlockModels() {
        return this.blockModels;
    }

    public BlockRenderer getBlockRenderer() {
        return this.blockRenderer;
    }

    public FluidRenderer getFluidRenderer() {
        return this.fluidRenderer;
    }

    public void init(ChunkRenderContext context) {
        this.lightDataCache.reset(context.getOrigin());
        this.worldSlice.copyData(context);
        // create the new local slice here so that it's unique whenever we copy new data
        // this is passed into mod code, since some depend on the provided BlockRenderView object being unique each time
        this.localSlice = new WorldSliceLocal(this.worldSlice);
    }

    public WorldSlice getWorldSlice() {
        return this.worldSlice;
    }

    public WorldSliceLocal getLocalSlice() {
        return this.localSlice;
    }
}
