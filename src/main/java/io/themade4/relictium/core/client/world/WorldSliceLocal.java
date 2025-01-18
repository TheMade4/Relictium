package io.themade4.relictium.core.client.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;

import javax.annotation.Nullable;

/**
 * Wrapper object used to defeat identity comparisons in mods. Since vanilla provides a unique object to them for each
 * subchunk, we do the same.
 */
public class WorldSliceLocal implements SodiumBlockAccess {
    private final SodiumBlockAccess view;

    public WorldSliceLocal(SodiumBlockAccess view) {
        this.view = view;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return view.getTileEntity(pos);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return view.getCombinedLight(pos, lightValue);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return view.getBlockState(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return view.isAirBlock(pos);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return view.getBiome(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return view.getStrongPower(pos, direction);
    }

    @Override
    public WorldType getWorldType() {
        return view.getWorldType();
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return view.isSideSolid(pos, side, _default);
    }

    @Override
    public int getBlockTint(BlockPos pos, BiomeColorHelper.ColorResolver resolver) {
        return view.getBlockTint(pos, resolver);
    }
}
