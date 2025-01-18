package io.themade4.relictium.core.client.gl.shader;

public class ShaderBindingPoint {
    private final int genericAttributeIndex;

    public ShaderBindingPoint(int genericAttributeIndex) {
        this.genericAttributeIndex = genericAttributeIndex;
    }

    public int getGenericAttributeIndex() {
        return genericAttributeIndex;
    }
}
