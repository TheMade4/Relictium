package io.themade4.relictium.core.client.model.quad.blender;

import io.themade4.relictium.core.client.model.quad.ModelQuadView;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface BiomeColorBlender {
    /**
     * Computes the blended biome colors and returns an an array containing the integer-encoded colors for each vertex.
     * The array returned by this method may be re-used in subsequent calls in order to reduce memory allocations, and
     * as such, the contents of an array returned by this method is undefined after a subsequent call.
     *
     * @param colorizer The color sampling source
     * @param world The world to sample biomes (and as a result, colors) from
     * @param state The block state being rendered
     * @param origin The position of the block being rendered
     * @param quad The quad which will be colorized
     * @return An array of integer colors in ABGR format
     */
    int[] getColors(IBlockColor colorizer, IBlockAccess world, IBlockState state, BlockPos origin,
                    ModelQuadView quad);
    
    static BiomeColorBlender create(Minecraft client) {
        return new ConfigurableColorBlender(client);
    }
}
