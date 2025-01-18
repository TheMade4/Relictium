package io.themade4.relictium.core.client.gl.tessellation;

import io.themade4.relictium.core.client.gl.device.CommandList;

public interface GlTessellation {
    void delete(CommandList commandList);

    void bind(CommandList commandList);

    void unbind(CommandList commandList);

    GlPrimitiveType getPrimitiveType();
}
