package io.themade4.relictium.core.client.model.quad.blender;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Set;

public class DefaultBlockColorSettings {
    private static final Set<Block> BLENDED_BLOCKS = new ReferenceOpenHashSet<>(Sets.newHashSet(
            Blocks.GRASS, Blocks.TALLGRASS,
            Blocks.DOUBLE_PLANT, Blocks.LEAVES, Blocks.LEAVES2,
            Blocks.VINE, Blocks.WATER, Blocks.CAULDRON, Blocks.REEDS));

    public static boolean isSmoothBlendingAvailable(Block block) {
        return BLENDED_BLOCKS.contains(block);
    }
}