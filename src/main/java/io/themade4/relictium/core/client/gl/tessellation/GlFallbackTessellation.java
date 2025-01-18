package io.themade4.relictium.core.client.gl.tessellation;

import io.themade4.relictium.core.client.gl.attribute.GlVertexAttributeBinding;
import io.themade4.relictium.core.client.gl.device.CommandList;
import org.lwjgl.opengl.GL20;

public class GlFallbackTessellation extends GlAbstractTessellation {
    public GlFallbackTessellation(GlPrimitiveType primitiveType, TessellationBinding[] bindings) {
        super(primitiveType, bindings);
    }

    @Override
    public void delete(CommandList commandList) {

    }

    @Override
    public void bind(CommandList commandList) {
        this.bindAttributes(commandList);
    }

    @Override
    public void unbind(CommandList commandList) {
        for (TessellationBinding binding : this.bindings) {
            for (GlVertexAttributeBinding attrib : binding.getAttributeBindings()) {
                GL20.glDisableVertexAttribArray(attrib.getIndex());
            }
        }
    }
}
