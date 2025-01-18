package me.jellysquid.mods.sodium.mixin.features.options;

import io.themade4.relictium.Relictium;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
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
        return Relictium.options().quality.smoothLighting != SodiumGameOptions.LightingQuality.OFF;
    }
}
