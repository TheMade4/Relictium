package io.themade4.relictium.core.client.render.chunk.compile.buffers;

import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFacing;
import io.themade4.relictium.core.client.render.chunk.data.ChunkRenderData;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;

public interface ChunkModelBuffers {
    ModelVertexSink getSink(ModelQuadFacing facing);

    @Deprecated
    ChunkRenderData.Builder getRenderData();
}
