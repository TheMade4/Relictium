package io.themade4.relictium.core.client.model.quad.blender;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface BlockColorSettings<T> {
    /**
     * Configures whether biome colors from a color provider will be interpolated for this block. You should only
     * enable this functionality if your color provider returns values based upon a pair of coordinates in the world,
     * and not if it needs access to the block state itself.
     *
     * @return True if interpolation should be used, otherwise false.
     */
    boolean useSmoothColorBlending(IBlockAccess view, T state, BlockPos pos);

    @SuppressWarnings("unchecked")
    static boolean isSmoothBlendingEnabled(IBlockAccess world, IBlockState state, BlockPos pos) {
        if (state.getBlock() instanceof BlockColorSettings) {
        	BlockColorSettings<IBlockState> settings = (BlockColorSettings<IBlockState>) state.getBlock();
            return settings.useSmoothColorBlending(world, state, pos);
        }

        return false;
    }
}