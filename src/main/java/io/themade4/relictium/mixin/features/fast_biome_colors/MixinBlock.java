package io.themade4.relictium.mixin.features.fast_biome_colors;

import io.themade4.relictium.core.client.model.quad.blender.BlockColorSettings;
import io.themade4.relictium.core.client.model.quad.blender.DefaultBlockColorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class MixinBlock implements BlockColorSettings<IBlockState> {

    @Override
    public boolean useSmoothColorBlending(IBlockAccess view, IBlockState state, BlockPos pos) {
        return DefaultBlockColorSettings.isSmoothBlendingAvailable(state.getBlock());
    }
}