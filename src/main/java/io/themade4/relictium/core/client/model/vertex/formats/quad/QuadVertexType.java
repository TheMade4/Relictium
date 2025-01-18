package io.themade4.relictium.core.client.model.vertex.formats.quad;

import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.formats.quad.writer.QuadVertexBufferWriterNio;
import io.themade4.relictium.core.client.model.vertex.formats.quad.writer.QuadVertexBufferWriterUnsafe;
import io.themade4.relictium.core.client.model.vertex.formats.quad.writer.QuadVertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.type.BlittableVertexType;
import io.themade4.relictium.core.client.model.vertex.type.VanillaVertexType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class QuadVertexType implements VanillaVertexType<QuadVertexSink>, BlittableVertexType<QuadVertexSink> {
    @Override
    public QuadVertexSink createFallbackWriter(BufferBuilder consumer) {
        return new QuadVertexWriterFallback(consumer);
    }

    @Override
    public QuadVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new QuadVertexBufferWriterUnsafe(buffer) : new QuadVertexBufferWriterNio(buffer);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return QuadVertexSink.VERTEX_FORMAT;
    }

    @Override
    public BlittableVertexType<QuadVertexSink> asBlittable() {
        return this;
    }
}
