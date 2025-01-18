package io.themade4.relictium.mixin.features.chunk_rendering;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.FloatBuffer;

@Mixin(ActiveRenderInfo.class)
public interface AccessorActiveRenderInfo {
    @Accessor("PROJECTION")
    static FloatBuffer getProjectionMatrix() {
        throw new AssertionError();
    }

    @Accessor("MODELVIEW")
    static FloatBuffer getModelViewMatrix() {
        throw new AssertionError();
    }
}
