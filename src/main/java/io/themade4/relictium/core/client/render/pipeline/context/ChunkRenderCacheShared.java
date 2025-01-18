package io.themade4.relictium.core.client.render.pipeline.context;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import io.themade4.relictium.core.client.model.light.LightPipelineProvider;
import io.themade4.relictium.core.client.model.light.cache.HashLightDataCache;
import io.themade4.relictium.core.client.model.quad.blender.BiomeColorBlender;
import io.themade4.relictium.core.client.render.pipeline.BlockRenderer;
import io.themade4.relictium.core.client.render.pipeline.ChunkRenderCache;
import io.themade4.relictium.core.client.world.WorldSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

import java.util.Map;

public class ChunkRenderCacheShared extends ChunkRenderCache {
    private static final Map<World, ChunkRenderCacheShared> INSTANCES = new Reference2ObjectOpenHashMap<>();

    private final BlockRenderer blockRenderer;
    private final HashLightDataCache lightCache;

    private ChunkRenderCacheShared(WorldSlice world) {
        Minecraft client = Minecraft.getMinecraft();

        this.lightCache = new HashLightDataCache(world);

        BiomeColorBlender biomeColorBlender = this.createBiomeColorBlender();
        LightPipelineProvider lightPipelineProvider = new LightPipelineProvider(this.lightCache);

        this.blockRenderer = new BlockRenderer(client, lightPipelineProvider, biomeColorBlender);
    }

    public BlockRenderer getBlockRenderer() {
        return this.blockRenderer;
    }

    private void resetCache() {
        this.lightCache.clearCache();
    }

    public static ChunkRenderCacheShared getInstance(WorldClient world) {
        ChunkRenderCacheShared instance = INSTANCES.get(world);

        if (instance == null) {
            throw new IllegalStateException("No global renderer exists");
        }

        return instance;
    }

    public static void destroyRenderContext(WorldClient world) {
        if (INSTANCES.remove(world) == null) {
            throw new IllegalStateException("No render context exists for world: " + world);
        }
    }

    public static void createRenderContext(WorldClient world) {
        if (INSTANCES.containsKey(world)) {
            throw new IllegalStateException("Render context already exists for world: " + world);
        }

        INSTANCES.put(world, new ChunkRenderCacheShared(new WorldSlice(world)));
    }

    public static void resetCaches() {
        for (ChunkRenderCacheShared context : INSTANCES.values()) {
            context.resetCache();
        }
    }
}
