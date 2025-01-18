package io.themade4.relictium.core.client.model.vertex.formats.glyph;

import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.formats.glyph.writer.GlyphVertexBufferWriterNio;
import io.themade4.relictium.core.client.model.vertex.formats.glyph.writer.GlyphVertexBufferWriterUnsafe;
import io.themade4.relictium.core.client.model.vertex.formats.glyph.writer.GlyphVertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.type.BlittableVertexType;
import io.themade4.relictium.core.client.model.vertex.type.VanillaVertexType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class GlyphVertexType implements VanillaVertexType<GlyphVertexSink>, BlittableVertexType<GlyphVertexSink> {
    @Override
    public GlyphVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new GlyphVertexBufferWriterUnsafe(buffer) : new GlyphVertexBufferWriterNio(buffer);
    }

    @Override
    public GlyphVertexSink createFallbackWriter(BufferBuilder consumer) {
        return new GlyphVertexWriterFallback(consumer);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return GlyphVertexSink.VERTEX_FORMAT;
    }

    @Override
    public BlittableVertexType<GlyphVertexSink> asBlittable() {
        return this;
    }
}
