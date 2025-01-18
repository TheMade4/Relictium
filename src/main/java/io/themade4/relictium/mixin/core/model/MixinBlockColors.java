package io.themade4.relictium.mixin.core.model;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import io.themade4.relictium.core.client.world.biome.BlockColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockColors.class)
public class MixinBlockColors implements BlockColorsExtended {
    private Reference2ReferenceMap<Block, IBlockColor> blocksToColor;

    private static final IBlockColor DEFAULT_PROVIDER = (state, view, pos, tint) -> -1;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.blocksToColor = new Reference2ReferenceOpenHashMap<>();
        this.blocksToColor.defaultReturnValue(DEFAULT_PROVIDER);
    }

    @Inject(method = "registerBlockColorHandler", at = @At("HEAD"))
    private void preRegisterColor(IBlockColor provider, Block[] blocks, CallbackInfo ci) {
        // Synchronize because Forge mods register this without enqueuing the call on the main thread
        // and then blame Embeddium for the crash because of the mixin, despite vanilla using a non-concurrent
        // HashMap too
        synchronized (this.blocksToColor) {
            for (Block block : blocks) {
                if(provider != null)
                    this.blocksToColor.put(block, provider);
            }
        }
    }

    @Override
    public IBlockColor getColorProvider(IBlockState state) {
        return this.blocksToColor.get(state.getBlock());
    }

    @Override
    public boolean hasColorProvider(IBlockState state) {
        return this.blocksToColor.get(state.getBlock()) != DEFAULT_PROVIDER;
    }
}
