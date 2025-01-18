package io.themade4.relictium.mixin.core.model;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import io.themade4.relictium.core.client.world.biome.ItemColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IRegistryDelegate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemColors.class)
public class MixinItemColors implements ItemColorsExtended {
    private Reference2ReferenceMap<IRegistryDelegate<Item>, IItemColor> itemsToColor;

    private static final IItemColor DEFAULT_PROVIDER = (stack, tintIdx) -> -1;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.itemsToColor = new Reference2ReferenceOpenHashMap<>();
        this.itemsToColor.defaultReturnValue(DEFAULT_PROVIDER);
    }

    @Inject(method = "registerItemColorHandler(Lnet/minecraft/client/renderer/color/IItemColor;[Lnet/minecraft/item/Item;)V", at = @At("HEAD"))
    private void preRegisterColor(IItemColor mapper, Item[] convertibles, CallbackInfo ci) {
        // Synchronize because Forge mods register this without enqueuing the call on the main thread
        // and then blame Embeddium for the crash because of the mixin, despite vanilla using a non-concurrent
        // HashMap too
        synchronized (this.itemsToColor) {
            for (Item convertible : convertibles) {
                this.itemsToColor.put(convertible.delegate, mapper);
            }
        }
    }

    @Inject(method = "registerItemColorHandler(Lnet/minecraft/client/renderer/color/IItemColor;[Lnet/minecraft/block/Block;)V", at = @At("HEAD"))
    private void preRegisterColor(IItemColor mapper, Block[] convertibles, CallbackInfo ci) {
        // Synchronize because Forge mods register this without enqueuing the call on the main thread
        // and then blame Embeddium for the crash because of the mixin, despite vanilla using a non-concurrent
        // HashMap too
        synchronized (this.itemsToColor) {
            for (Block convertible : convertibles) {
                this.itemsToColor.put(Item.getItemFromBlock(convertible).delegate, mapper);
            }
        }
    }

    @Override
    public IItemColor getColorProvider(ItemStack stack) {
        return this.itemsToColor.get(stack.getItem().delegate);
    }
}
