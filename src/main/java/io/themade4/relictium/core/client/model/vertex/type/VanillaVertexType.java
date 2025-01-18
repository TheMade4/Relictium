package io.themade4.relictium.core.client.model.vertex.type;

import io.themade4.relictium.core.client.gl.attribute.BufferVertexFormat;
import io.themade4.relictium.core.client.model.vertex.VertexSink;
import net.minecraft.client.renderer.vertex.VertexFormat;

public interface VanillaVertexType<T extends VertexSink> extends BufferVertexType<T> {
    default BufferVertexFormat getBufferVertexFormat() {
        return BufferVertexFormat.from(this.getVertexFormat());
    }

    VertexFormat getVertexFormat();
}
