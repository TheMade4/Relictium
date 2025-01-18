package io.themade4.relictium.core.client.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;

public interface BlockColorsExtended {
    IBlockColor getColorProvider(IBlockState state);
    boolean hasColorProvider(IBlockState state);
}
