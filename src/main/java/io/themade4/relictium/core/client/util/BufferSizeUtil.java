package io.themade4.relictium.core.client.util;

import net.minecraft.util.BlockRenderLayer;

import java.util.HashMap;
import java.util.Map;

// Values was taken from RegionRenderCacheBuilder
public class BufferSizeUtil {

    public static final Map<BlockRenderLayer, Integer> BUFFER_SIZES = new HashMap<>();

    static {
        BUFFER_SIZES.put(BlockRenderLayer.SOLID, 2097152);
        BUFFER_SIZES.put(BlockRenderLayer.CUTOUT, 131072);
        BUFFER_SIZES.put(BlockRenderLayer.CUTOUT_MIPPED, 131072);
        BUFFER_SIZES.put(BlockRenderLayer.TRANSLUCENT, 262144);
    }

}
