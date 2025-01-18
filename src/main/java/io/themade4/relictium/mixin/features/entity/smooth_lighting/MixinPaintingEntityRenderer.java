package io.themade4.relictium.mixin.features.entity.smooth_lighting;

import io.themade4.relictium.RelictiumClientMod;
import io.themade4.relictium.core.client.gui.RelictiumGameOptions;
import io.themade4.relictium.core.client.model.light.EntityLighter;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPainting.class)
public abstract class MixinPaintingEntityRenderer extends Render<EntityPainting> {

    @Unique
    private EntityPainting entity;

    @Unique
    private float tickDelta;

    protected MixinPaintingEntityRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityPainting;DDDFF)V", at = @At(value = "HEAD"))
    public void preRender(EntityPainting paintingEntity, double x, double y, double z, float p_76986_8_, float partialTicks, CallbackInfo ci) {
        this.entity = paintingEntity;
        this.tickDelta = partialTicks;
    }

    @Redirect(method = "setLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCombinedLight(Lnet/minecraft/util/math/BlockPos;I)I"))
    public int redirectLightmapCoord(World world, BlockPos pos, int type) {
        if (RelictiumClientMod.options().quality.smoothLighting == RelictiumGameOptions.LightingQuality.HIGH && this.entity != null) {
            return EntityLighter.getBlendedLight(this.entity, tickDelta);
        } else {
            return world.getCombinedLight(pos, type);
        }
    }

}