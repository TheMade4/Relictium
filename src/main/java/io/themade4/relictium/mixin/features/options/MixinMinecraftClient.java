package io.themade4.relictium.mixin.features.options;

import io.themade4.relictium.RelictiumClientMod;
import io.themade4.relictium.core.client.gui.RelictiumGameOptions;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {
    /**
     * @author JellySquid
     * @reason Make ambient occlusion user configurable
     */
    @Overwrite
    public static boolean isAmbientOcclusionEnabled() {
        return RelictiumClientMod.options().quality.smoothLighting != RelictiumGameOptions.LightingQuality.OFF;
    }
}
