package io.themade4.relictium.core.client.model.vertex.formats.particle.writer;

import io.themade4.relictium.core.client.model.vertex.fallback.VertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.formats.particle.ParticleVertexSink;
import io.themade4.relictium.core.client.util.color.ColorABGR;
import net.minecraft.client.renderer.BufferBuilder;

public class ParticleVertexWriterFallback extends VertexWriterFallback implements ParticleVertexSink {
    public ParticleVertexWriterFallback(BufferBuilder consumer) {
        super(consumer);
    }

    @Override
    public void writeParticle(float x, float y, float z, float u, float v, int color, int light) {
        BufferBuilder consumer = this.consumer;
        consumer.pos(x, y, z);
        consumer.tex(u, v);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        // TODO
        consumer.lightmap(light, light);
        consumer.endVertex();
    }
}
