package io.themade4.relictium.core.client.model.vertex.formats.line.writer;

import io.themade4.relictium.core.client.model.vertex.fallback.VertexWriterFallback;
import io.themade4.relictium.core.client.model.vertex.formats.line.LineVertexSink;
import io.themade4.relictium.core.client.util.color.ColorABGR;
import net.minecraft.client.renderer.BufferBuilder;

public class LineVertexWriterFallback extends VertexWriterFallback implements LineVertexSink {
    public LineVertexWriterFallback(BufferBuilder consumer) {
        super(consumer);
    }

    @Override
    public void vertexLine(float x, float y, float z, int color) {
        BufferBuilder consumer = this.consumer;
        consumer.pos(x, y, z);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        consumer.endVertex();
    }
}
