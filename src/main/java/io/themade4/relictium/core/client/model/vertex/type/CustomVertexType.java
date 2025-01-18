package io.themade4.relictium.core.client.model.vertex.type;

import io.themade4.relictium.core.client.gl.attribute.BufferVertexFormat;
import io.themade4.relictium.core.client.gl.attribute.GlVertexFormat;
import io.themade4.relictium.core.client.model.vertex.VertexSink;

public interface CustomVertexType<T extends VertexSink, A extends Enum<A>> extends BufferVertexType<T> {
    /**
     * @return The {@link GlVertexFormat} required for blitting (direct writing into buffers)
     */
    GlVertexFormat<A> getCustomVertexFormat();

    @Override
    default BufferVertexFormat getBufferVertexFormat() {
        return this.getCustomVertexFormat();
    }
}
