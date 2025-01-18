package me.jellysquid.mods.sodium.mixin.features.options;

import io.themade4.relictium.Relictium;
import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * note that both {@link net.minecraft.client.gui.GuiIngame} and {@link net.minecraftforge.client.GuiIngameForge}
 * implements client GUI rendering, and mixins for GuiIngame can be totally useless since GuiIngame might be
 * intentionally ignored and barely invoked
 */
@Mixin(GuiIngameForge.class)
public class MixinInGameHud {
    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isFancyGraphicsEnabled()Z"))
    private boolean vintagium$redirectVignette() {
        //mixin target is `if (renderVignette && Minecraft.isFancyGraphicsEnabled())`
        //we assume that `renderVignette` is always true, because vanilla/forge will not change its value, and mods that
        //will change it explicitly must have a reason.
        return Relictium.options().quality.enableVignette;
    }
}
