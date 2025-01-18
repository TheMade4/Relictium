package me.jellysquid.mods.sodium.mixin.features.chunk_rendering;

import com.google.common.collect.Queues;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ArrayBlockingQueue;

@Mixin(ChunkRenderDispatcher.class)
public class MixinChunkBuilder {

    @Shadow
    @Final
    @Mutable
    private int countRenderBuilders;

    @Redirect(method = "<init>(I)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Queues;newArrayBlockingQueue(I)Ljava/util/concurrent/ArrayBlockingQueue;", remap = false))
    public ArrayBlockingQueue<?> modifyThreadPoolSize(int capacity) {
        // Do not allow any resources to be allocated
        this.countRenderBuilders = 0;
        return Queues.newArrayBlockingQueue(1);
    }
}
