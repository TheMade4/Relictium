package io.themade4.relictium.mixin.features.entity.smooth_lighting;

import io.themade4.relictium.core.client.gui.RelictiumGameOptions;
import io.themade4.relictium.core.client.model.light.EntityLighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Unique
    private float tickDelta;

    @Inject(method = "renderEntityStatic", at = @At("HEAD"))
    public void catchTickDelta(Entity entity, float tickDelta, boolean bl, CallbackInfo ci) {
        this.tickDelta = tickDelta;
    }

    @Redirect(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBrightnessForRender()I"))
    private int sodium$getBrightnessForRender(Entity self) {
        if (Minecraft.getMinecraft().gameSettings.ambientOcclusion == RelictiumGameOptions.LightingQuality.HIGH.ordinal()) {
            return EntityLighter.getBlendedLight(self, tickDelta);
        }

        return self.getBrightnessForRender();
    }
}