package io.themade4.relictium.core.client.model.vertex.formats.particle;

import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.formats.particle.writer.ParticleVertexBufferWriterNio;
import io.themade4.relictium.core.client.model.vertex.formats.particle.writer.ParticleVertexBufferWriterUnsafe;
import io.themade4.relictium.core.client.model.vertex.formats.particle.writer.ParticleVertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.type.BlittableVertexType;
import io.themade4.relictium.core.client.model.vertex.type.VanillaVertexType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class ParticleVertexType implements VanillaVertexType<ParticleVertexSink>, BlittableVertexType<ParticleVertexSink> {
    @Override
    public ParticleVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new ParticleVertexBufferWriterUnsafe(buffer) : new ParticleVertexBufferWriterNio(buffer);
    }

    @Override
    public ParticleVertexSink createFallbackWriter(BufferBuilder consumer) {
        return new ParticleVertexWriterFallback(consumer);
    }

    @Override
    public BlittableVertexType<ParticleVertexSink> asBlittable() {
        return this;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return ParticleVertexSink.VERTEX_FORMAT;
    }
}
