package io.themade4.relictium.core.client.render.chunk.compile.buffers;

import io.themade4.relictium.core.client.model.vertex.transformers.AbstractVertexTransformer;
import io.themade4.relictium.core.client.render.chunk.format.ChunkModelOffset;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;

public class ChunkModelVertexTransformer extends AbstractVertexTransformer<ModelVertexSink> implements ModelVertexSink {
    /**
     * The translation to be applied to all quads written into this mesh builder.
     */
    private final ChunkModelOffset offset;

    public ChunkModelVertexTransformer(ModelVertexSink delegate, ChunkModelOffset offset) {
        super(delegate);

        this.offset = offset;
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light) {
        this.delegate.writeQuad(x + this.offset.x, y + this.offset.y, z + this.offset.z, color, u, v, light);
    }
}
