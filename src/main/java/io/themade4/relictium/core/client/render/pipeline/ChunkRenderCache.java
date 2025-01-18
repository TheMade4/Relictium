package io.themade4.relictium.core.client.render.pipeline;

import io.themade4.relictium.core.client.model.quad.blender.BiomeColorBlender;
import net.minecraft.client.Minecraft;

public class ChunkRenderCache {
    protected BiomeColorBlender createBiomeColorBlender() {
    	 return BiomeColorBlender.create(Minecraft.getMinecraft());
    }
}
