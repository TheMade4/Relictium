package io.themade4.relictium.render.fluid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import io.themade4.relictium.mixin.features.chunk_rendering.AccessorBlockFluidRenderer;
import io.themade4.relictium.mixin.features.chunk_rendering.AccessorBlockRenderDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidSpriteCache {
    // Cache the sprites array to avoid reallocating it on every call
    private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[3];
    private final Object2ObjectOpenHashMap<ResourceLocation, TextureAtlasSprite> spriteCache = new Object2ObjectOpenHashMap<>();
    private final TextureAtlasSprite[] waterOverride, lavaOverride;

    public FluidSpriteCache() {
        AccessorBlockFluidRenderer fluidRenderer = (AccessorBlockFluidRenderer)((AccessorBlockRenderDispatcher)Minecraft.getMinecraft().getBlockRendererDispatcher()).getFluidRenderer();
        waterOverride = new TextureAtlasSprite[3];
        TextureAtlasSprite[] water = fluidRenderer.getAtlasSpritesWater();
        waterOverride[0] = water[0];
        waterOverride[1] = water[1];
        waterOverride[2] = fluidRenderer.getAtlasSpriteWaterOverlay();
        // Construct array with 3 elements, vanilla only has 2
        lavaOverride = new TextureAtlasSprite[3];
        TextureAtlasSprite[] lava = fluidRenderer.getAtlasSpritesLava();
        lavaOverride[0] = lava[0];
        lavaOverride[1] = lava[1];
    }

    private TextureAtlasSprite getTexture(ResourceLocation identifier) {
        TextureAtlasSprite sprite = spriteCache.get(identifier);

        if (sprite == null) {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(identifier.toString());;
            spriteCache.put(identifier, sprite);
        }

        return sprite;
    }

    public TextureAtlasSprite[] getSprites(Fluid fluid) {
        if (fluid == FluidRegistry.WATER) {
            return waterOverride;
        } else if (fluid == FluidRegistry.LAVA) {
            return lavaOverride;
        }
        sprites[0] = getTexture(fluid.getStill());
        sprites[1] = getTexture(fluid.getFlowing());
        ResourceLocation overlay = fluid.getOverlay();
        sprites[2] = overlay != null ? getTexture(overlay) : null;
        return sprites;
    }
}
