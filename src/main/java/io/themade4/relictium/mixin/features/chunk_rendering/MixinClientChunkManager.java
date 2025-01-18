package io.themade4.relictium.mixin.features.chunk_rendering;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import io.themade4.relictium.core.client.util.ExtChunkProviderClient;
import io.themade4.relictium.core.client.util.math.MathChunkPos;
import io.themade4.relictium.core.client.world.ChunkStatusListener;
import io.themade4.relictium.core.client.world.ChunkStatusListenerManager;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderClient.class)
public abstract class MixinClientChunkManager implements ChunkStatusListenerManager, ExtChunkProviderClient {
    @Shadow
    public abstract Chunk provideChunk(int x, int z);

    private final LongOpenHashSet loadedChunks = new LongOpenHashSet();
    private boolean needsTrackingUpdate = false;

    private ChunkStatusListener listener;

    @Inject(method = "loadChunk", at = @At("RETURN"))
    private void afterLoadChunkFromPacket(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (this.listener != null) {
            this.listener.onChunkAdded(x, z);
            this.loadedChunks.add(ChunkPos.asLong(x, z));
        }
    }

    @Inject(method = "unloadChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;onUnload()V", shift = At.Shift.AFTER))
    private void afterUnloadChunk(int x, int z, CallbackInfo ci) {
        if (this.listener != null) {
            this.listener.onChunkRemoved(x, z);
            this.loadedChunks.remove(ChunkPos.asLong(x, z));
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void afterTick(CallbackInfoReturnable<Boolean> cir) {
        if (!this.needsTrackingUpdate) {
            return;
        }

        LongIterator it = this.loadedChunks.iterator();

        while (it.hasNext()) {
            long pos = it.nextLong();

            int x = MathChunkPos.getX(pos);
            int z = MathChunkPos.getZ(pos);

            if (this.provideChunk(x, z) == null) {
                it.remove();

                if (this.listener != null) {
                    this.listener.onChunkRemoved(x, z);
                }
            }
        }

        this.needsTrackingUpdate = false;
    }

    /*@Inject(method = "setChunkMapCenter(II)V", at = @At("RETURN"))
    private void afterChunkMapCenterChanged(int x, int z, CallbackInfo ci) {
        this.needsTrackingUpdate = true;
    }

    @Inject(method = "updateLoadDistance",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientChunkManager$ClientChunkMap;set(ILnet/minecraft/world/chunk/WorldChunk;)V",
                    shift = At.Shift.AFTER))
    private void afterLoadDistanceChanged(int loadDistance, CallbackInfo ci) {
        this.needsTrackingUpdate = true;
    }*/
    @Override
    public void setNeedsTrackingUpdate(boolean needsTrackingUpdate) {
        this.needsTrackingUpdate = needsTrackingUpdate;
    }

    @Override
    public boolean needsTrackingUpdate() {
        return this.needsTrackingUpdate;
    }

    @Override
    public void setListener(ChunkStatusListener listener) {
        this.listener = listener;
    }
}
