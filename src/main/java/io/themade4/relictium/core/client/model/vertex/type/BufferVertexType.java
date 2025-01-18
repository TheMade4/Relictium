package io.themade4.relictium.core.client.model.vertex.type;

import io.themade4.relictium.core.client.gl.attribute.BufferVertexFormat;
import io.themade4.relictium.core.client.model.vertex.VertexSink;

/**
 * A blittable {@link VertexType} which supports direct copying into a {@link net.minecraft.client.renderer.BufferBuilder}
 * provided the buffer's vertex format matches that required by the {@link VertexSink}.
 *
 * @param <T> The {@link VertexSink} type this factory produces
 */
public interface BufferVertexType<T extends VertexSink> extends VertexType<T> {
    BufferVertexFormat getBufferVertexFormat();
}
