package io.themade4.relictium.core.client.model.vertex;

import io.themade4.relictium.core.client.model.vertex.type.VertexType;
import net.minecraft.client.renderer.BufferBuilder;

/**
 * A drain allows the instantiation of {@link VertexSink} and is implemented on outputs which take vertex data.
 */
public interface VertexDrain {
    /**
     * Returns a {@link VertexDrain} implementation on the provided {@link BufferBuilder}. Since the interface
     * is always implemented on a given VertexConsumer, this is simply implemented as a cast internally.
     * @param consumer The {@link BufferBuilder}
     * @return A {@link VertexDrain}
     */
    static VertexDrain of(BufferBuilder consumer) {
        return (VertexDrain) consumer;
    }

    /**
     * Returns a {@link VertexSink} of type {@link T}, created from {@param factory}, which transforms and writes
     * vertices through this vertex drain.
     *
     * @param factory The factory to create a vertex sink using
     * @param <T> The vertex sink's type
     * @return A new {@link VertexSink} of type {@link T}
     */
    <T extends VertexSink> T createSink(VertexType<T> factory);
}
