package io.themade4.relictium.core.client.render;

import io.themade4.relictium.mixin.features.chunk_rendering.AccessorActiveRenderInfo;
import org.lwjgl.BufferUtils;
import repack.joml.Matrix4f;

import java.nio.FloatBuffer;

public class GameRendererContext {
    private static final FloatBuffer bufModelViewProjection = BufferUtils.createFloatBuffer(16);

    /**
     * Obtains a model-view-projection matrix by multiplying the projection matrix with the model-view matrix
     * from {@param matrices}.
     *
     * The returned buffer is only valid for the lifetime of {@param stack}.
     *
     * @return A float-buffer on the stack containing the model-view-projection matrix in a format suitable for
     * uploading as uniform state
     */
    public static FloatBuffer getModelViewProjectionMatrix() {
        Matrix4f matrix = new Matrix4f(AccessorActiveRenderInfo.getProjectionMatrix());
        Matrix4f model = new Matrix4f(AccessorActiveRenderInfo.getModelViewMatrix());
        matrix.mul(model);
        matrix.get(bufModelViewProjection);

        return bufModelViewProjection;
    }
}
