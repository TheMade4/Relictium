package io.themade4.relictium.mixin.features.chunk_rendering;

import io.themade4.relictium.core.client.util.ExtChunkProviderClient;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinClientWorld {

    @Shadow
    public ChunkProviderClient getChunkProvider() {
        return null;
    }

    @Inject(method = "refreshVisibleChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", shift = At.Shift.AFTER, ordinal = 1))
    private void updateViewCenter(CallbackInfo ci) {
        ExtChunkProviderClient ext = (ExtChunkProviderClient) getChunkProvider();
        ext.setNeedsTrackingUpdate(true);
    }

}
