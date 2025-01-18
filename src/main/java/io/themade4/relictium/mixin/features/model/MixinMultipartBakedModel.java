package io.themade4.relictium.mixin.features.model;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;

import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.concurrent.locks.StampedLock;

@Mixin(MultipartBakedModel.class)
public class MixinMultipartBakedModel {
	private final Map<IBlockState, IBakedModel[]> stateCacheFast = new Reference2ReferenceOpenHashMap<>();
    private final StampedLock lock = new StampedLock();

    @Shadow
    @Final
    private Map<Predicate<IBlockState>, IBakedModel> selectors;

    /**
     * @author JellySquid
     * @reason Avoid expensive allocations and replace bitfield indirection
     */
    @Overwrite
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing face, long random) {
        if (state == null) {
            return Collections.emptyList();
        }

        IBakedModel[] models;

        long readStamp = this.lock.readLock();
        try {
            models = this.stateCacheFast.get(state);
        } finally {
            this.lock.unlockRead(readStamp);
        }
        
        if (models == null) {
            long writeStamp = this.lock.writeLock();
            try {
                List<IBakedModel> modelList = new ArrayList<>(this.selectors.size());

                for (Map.Entry<Predicate<IBlockState>, IBakedModel> pair : this.selectors.entrySet()) {
                    if (pair.getKey().test(state)) {
                        modelList.add(pair.getValue());
                    }
                }

                models = modelList.toArray(new IBakedModel[modelList.size()]);
                this.stateCacheFast.put(state, models);
            } finally {
                this.lock.unlockWrite(writeStamp);
            }
        }

        List<BakedQuad> quads = new ArrayList<>();

        for (IBakedModel model : models) {
            quads.addAll(model.getQuads(state, face, random));
        }

        return quads;
    }

}
