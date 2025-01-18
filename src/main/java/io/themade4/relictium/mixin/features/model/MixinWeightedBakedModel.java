package io.themade4.relictium.mixin.features.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(WeightedBakedModel.class)
public class MixinWeightedBakedModel {
    @Shadow
    @Final
    private List<WeightedBakedModel.WeightedModel> models;

    @Shadow
    @Final
    private int totalWeight;

    /**
     * @author JellySquid
     * @reason Avoid excessive object allocations
     */
    @Overwrite
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing face, long random) {
    	WeightedBakedModel.WeightedModel entry = getAt(this.models, Math.abs((int) random) % this.totalWeight);

        if (entry != null) {
            return entry.model.getQuads(state, face, random);
        }

        return Collections.emptyList();
    }

    private static <T extends WeightedBakedModel.WeightedModel> T getAt(List<T> pool, int totalWeight) {
        int i = 0;
        int len = pool.size();

        T weighted;

        do {
            if (i >= len) {
                return null;
            }

            weighted = pool.get(i++);
            totalWeight -= weighted.itemWeight;
        } while (totalWeight >= 0);

        return weighted;
    }
}