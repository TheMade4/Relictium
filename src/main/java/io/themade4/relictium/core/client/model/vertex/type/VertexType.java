package io.themade4.relictium.core.client.model.vertex.type;

import io.themade4.relictium.core.client.model.vertex.VertexSink;
import net.minecraft.client.renderer.BufferBuilder;

/**
 * Provides factories which create a {@link VertexSink} for the given vertex format.
 *
 * @param <T> The {@link VertexSink} type this factory produces
 */
public interface VertexType<T extends VertexSink> {
    /**
     * Creates a {@link VertexSink} which can write into any {@link BufferBuilder}. This is generally used when
     * a special implementation of {@link BufferBuilder} is used that cannot be optimized for, or when
     * complex/unsupported transformations need to be performed using vanilla code paths.
     * @param consumer The {@link BufferBuilder} to write into
     */
    T createFallbackWriter(BufferBuilder consumer);

    /**
     * If this vertex type supports {@link BufferVertexType}, then this method returns this vertex type as a
     * blittable type, performing a safe cast.
     */
    default BlittableVertexType<T> asBlittable() {
        return null;
    }
}
