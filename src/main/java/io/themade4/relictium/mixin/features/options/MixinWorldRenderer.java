package io.themade4.relictium.mixin.features.options;

import io.themade4.relictium.RelictiumClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinWorldRenderer {
    @Redirect(method = "renderRainSnow", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;fancyGraphics:Z", opcode = Opcodes.GETFIELD))
    private boolean redirectGetFancyWeather(GameSettings instance) {
        return RelictiumClientMod.options().quality.weatherQuality.isFancy(Minecraft.getMinecraft().gameSettings.fancyGraphics);
    }
}
