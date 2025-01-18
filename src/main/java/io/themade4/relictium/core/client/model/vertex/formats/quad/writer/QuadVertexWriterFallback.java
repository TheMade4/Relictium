package io.themade4.relictium.core.client.model.vertex.formats.quad.writer;

import io.themade4.relictium.core.client.model.vertex.fallback.VertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.formats.quad.QuadVertexSink;
import io.themade4.relictium.core.client.util.Norm3b;
import io.themade4.relictium.core.client.util.color.ColorABGR;
import net.minecraft.client.renderer.BufferBuilder;

public class QuadVertexWriterFallback extends VertexWriterFallback implements QuadVertexSink {
    public QuadVertexWriterFallback(BufferBuilder consumer) {
        super(consumer);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        BufferBuilder consumer = this.consumer;
        consumer.pos(x, y, z);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        consumer.tex(u, v);
        // TODO
        //consumer.overlay(overlay);
        consumer.lightmap(light, light);
        consumer.normal(Norm3b.unpackX(normal), Norm3b.unpackY(normal), Norm3b.unpackZ(normal));
        consumer.endVertex();
    }
}
