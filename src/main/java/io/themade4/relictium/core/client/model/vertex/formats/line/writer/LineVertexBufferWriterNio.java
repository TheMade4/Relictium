package io.themade4.relictium.core.client.model.vertex.formats.line.writer;

import io.themade4.relictium.core.client.model.vertex.VanillaVertexTypes;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferView;
import io.themade4.relictium.core.client.model.vertex.buffer.VertexBufferWriterNio;
import io.themade4.relictium.core.client.model.vertex.formats.line.LineVertexSink;

import java.nio.ByteBuffer;

public class LineVertexBufferWriterNio extends VertexBufferWriterNio implements LineVertexSink {
    public LineVertexBufferWriterNio(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.LINES);
    }

    @Override
    public void vertexLine(float x, float y, float z, int color) {
        int i = this.writeOffset;

        ByteBuffer buffer = this.byteBuffer;
        buffer.putFloat(i, x);
        buffer.putFloat(i + 4, y);
        buffer.putFloat(i + 8, z);
        buffer.putInt(i + 12, color);

        this.advance();
    }
}
