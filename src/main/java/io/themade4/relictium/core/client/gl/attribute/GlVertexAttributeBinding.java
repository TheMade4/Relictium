package io.themade4.relictium.core.client.gl.attribute;

import io.themade4.relictium.core.client.gl.shader.ShaderBindingPoint;

public class GlVertexAttributeBinding extends GlVertexAttribute {
    private final int index;

    public GlVertexAttributeBinding(ShaderBindingPoint bindingPoint, GlVertexAttribute attribute) {
        super(attribute.getFormat(), attribute.getSize(), attribute.getCount(), attribute.isNormalized(), attribute.getPointer(), attribute.getStride());

        this.index = bindingPoint.getGenericAttributeIndex();
    }

    public int getIndex() {
        return this.index;
    }
}
