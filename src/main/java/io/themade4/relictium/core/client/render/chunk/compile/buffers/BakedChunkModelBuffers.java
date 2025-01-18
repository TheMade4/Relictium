package io.themade4.relictium.core.client.render.chunk.compile.buffers;

import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFacing;
import io.themade4.relictium.core.client.render.chunk.data.ChunkRenderData;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;

public class BakedChunkModelBuffers implements ChunkModelBuffers {
    private final ModelVertexSink[] builders;
    private final ChunkRenderData.Builder renderData;

    public BakedChunkModelBuffers(ModelVertexSink[] builders, ChunkRenderData.Builder renderData) {
        this.builders = builders;
        this.renderData = renderData;
    }

    @Override
    public ModelVertexSink getSink(ModelQuadFacing facing) {
        return this.builders[facing.ordinal()];
    }

    @Override
    public ChunkRenderData.Builder getRenderData() {
        return this.renderData;
    }
}
