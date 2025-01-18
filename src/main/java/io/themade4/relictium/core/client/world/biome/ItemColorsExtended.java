package io.themade4.relictium.core.client.world.biome;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public interface ItemColorsExtended {
    IItemColor getColorProvider(ItemStack stack);
}
