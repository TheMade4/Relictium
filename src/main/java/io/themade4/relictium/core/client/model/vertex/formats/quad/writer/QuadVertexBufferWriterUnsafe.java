package io.themade4.relictium.core.client.model.vertex.formats.quad.writer;

import io.themade4.relictium.core.client.model.vertex.VanillaVertexTypes;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferWriterUnsafe;
import io.themade4.relictium.core.client.model.vertex.formats.quad.QuadVertexSink;
import io.themade4.relictium.core.client.util.CompatMemoryUtil;

public class QuadVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements QuadVertexSink {
    public QuadVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.QUADS);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        long i = this.writePointer;

        CompatMemoryUtil.memPutFloat(i, x);
        CompatMemoryUtil.memPutFloat(i + 4, y);
        CompatMemoryUtil.memPutFloat(i + 8, z);
        CompatMemoryUtil.memPutInt(i + 12, color);
        CompatMemoryUtil.memPutFloat(i + 16, u);
        CompatMemoryUtil.memPutFloat(i + 20, v);
        CompatMemoryUtil.memPutInt(i + 24, overlay);
        CompatMemoryUtil.memPutInt(i + 28, light);
        CompatMemoryUtil.memPutInt(i + 32, normal);

        this.advance();
    }
}
