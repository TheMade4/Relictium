package io.themade4.relictium.core.client.render.chunk.shader;

import io.themade4.relictium.core.client.gl.shader.ShaderBindingPoint;

public class ChunkShaderBindingPoints {
    public static final ShaderBindingPoint POSITION = new ShaderBindingPoint(0);
    public static final ShaderBindingPoint COLOR = new ShaderBindingPoint(1);
    public static final ShaderBindingPoint TEX_COORD = new ShaderBindingPoint(2);
    public static final ShaderBindingPoint LIGHT_COORD = new ShaderBindingPoint(3);

    public static final ShaderBindingPoint MODEL_OFFSET = new ShaderBindingPoint(4);
}
