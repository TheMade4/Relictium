package io.themade4.relictium.core.client.model.vertex.formats.screen_quad.writer;

import io.themade4.relictium.core.client.model.vertex.fallback.VertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.formats.screen_quad.BasicScreenQuadVertexSink;
import io.themade4.relictium.core.client.util.color.ColorABGR;
import net.minecraft.client.renderer.BufferBuilder;

public class BasicScreenQuadVertexWriterFallback extends VertexWriterFallback implements BasicScreenQuadVertexSink {
    public BasicScreenQuadVertexWriterFallback(BufferBuilder consumer) {
        super(consumer);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color) {
        BufferBuilder consumer = this.consumer;
        consumer.pos(x, y, z);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        consumer.endVertex();
    }
}
