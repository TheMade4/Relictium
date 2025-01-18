package io.themade4.relictium.core.client.model.vertex.formats.line.writer;

import io.themade4.relictium.core.client.util.CompatMemoryUtil;

import io.themade4.relictium.core.client.model.vertex.VanillaVertexTypes;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferWriterUnsafe;
import io.themade4.relictium.core.client.model.vertex.formats.line.LineVertexSink;

public class LineVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements LineVertexSink {
    public LineVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.LINES);
    }

    @Override
    public void vertexLine(float x, float y, float z, int color) {
        long i = this.writePointer;

        CompatMemoryUtil.memPutFloat(i, x);
        CompatMemoryUtil.memPutFloat(i + 4, y);
        CompatMemoryUtil.memPutFloat(i + 8, z);
        CompatMemoryUtil.memPutInt(i + 12, color);

        this.advance();
    }
}
