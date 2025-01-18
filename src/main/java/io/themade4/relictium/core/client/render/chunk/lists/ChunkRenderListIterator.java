package io.themade4.relictium.core.client.render.chunk.lists;

public interface ChunkRenderListIterator<T> {
    T getGraphicsState();
    int getVisibleFaces();

    boolean hasNext();
    void advance();
}
