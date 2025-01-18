package io.themade4.relictium.core.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;

/**
 * Contains extensions to the vanilla {@link IBlockAccess}.
 */
public interface SodiumBlockAccess extends IBlockAccess {
    int getBlockTint(BlockPos pos, BiomeColorHelper.ColorResolver resolver);
}
