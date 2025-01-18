package io.themade4.relictium.core.client.model.vertex.type;

import io.themade4.relictium.core.client.render.chunk.format.ChunkMeshAttribute;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;

public interface ChunkVertexType extends BlittableVertexType<ModelVertexSink>, CustomVertexType<ModelVertexSink, ChunkMeshAttribute> {
    /**
     * @return The scale to be applied to vertex coordinates
     */
    float getModelScale();

    /**
     * @return The scale to be applied to texture coordinates
     */
    float getTextureScale();
}
