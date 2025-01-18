package io.themade4.relictium.core.client.render.chunk.compile.buffers;

import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFacing;
import io.themade4.relictium.core.client.render.chunk.data.ChunkRenderData;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;

public class FallbackChunkModelBuffers implements ChunkModelBuffers {
    public FallbackChunkModelBuffers() {

    }

    @Override
    public ModelVertexSink getSink(ModelQuadFacing facing) {
        return null;
    }

    @Override
    public ChunkRenderData.Builder getRenderData() {
        return null;
    }
}
