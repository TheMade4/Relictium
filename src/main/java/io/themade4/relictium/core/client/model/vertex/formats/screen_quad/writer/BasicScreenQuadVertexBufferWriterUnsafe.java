package io.themade4.relictium.core.client.model.vertex.formats.screen_quad.writer;

import io.themade4.relictium.core.client.model.vertex.VanillaVertexTypes;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferWriterUnsafe;
import io.themade4.relictium.core.client.model.vertex.formats.screen_quad.BasicScreenQuadVertexSink;
import io.themade4.relictium.core.client.util.CompatMemoryUtil;

public class BasicScreenQuadVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements BasicScreenQuadVertexSink {
    public BasicScreenQuadVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.BASIC_SCREEN_QUADS);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color) {
        long i = this.writePointer;

        CompatMemoryUtil.memPutFloat(i, x);
        CompatMemoryUtil.memPutFloat(i + 4, y);
        CompatMemoryUtil.memPutFloat(i + 8, z);
        CompatMemoryUtil.memPutInt(i + 12, color);

        this.advance();
    }
}
