package io.themade4.relictium.core.client.render.chunk.compile;

import io.themade4.relictium.core.client.render.chunk.ChunkGraphicsState;
import io.themade4.relictium.core.client.render.chunk.ChunkRenderContainer;
import io.themade4.relictium.core.client.render.chunk.data.ChunkRenderData;
import io.themade4.relictium.core.client.render.chunk.passes.BlockRenderPass;

/**
 * The result of a chunk rebuild task which contains any and all data that needs to be processed or uploaded on
 * the main thread. If a task is cancelled after finishing its work and not before the result is processed, the result
 * will instead be discarded.
 */
public class ChunkBuildResult<T extends ChunkGraphicsState> {
    public final ChunkRenderContainer<T> render;
    public final ChunkRenderData data;
    public BlockRenderPass[] passesToUpload;

    public ChunkBuildResult(ChunkRenderContainer<T> render, ChunkRenderData data) {
        this.render = render;
        this.data = data;
        this.passesToUpload = BlockRenderPass.VALUES;
    }
}
