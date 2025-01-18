package io.themade4.relictium.mixin.features.particle.cull;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.themade4.relictium.RelictiumClientMod;
import io.themade4.relictium.core.client.render.RelictiumWorldRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    private Frustum cullingFrustum;

    @Inject(method = {"renderParticles", "renderLitParticles"}, at = @At("HEAD"))
    private void preRenderParticles(Entity entity, float partialTicks, CallbackInfo ci) {
        Frustum frustum = RelictiumWorldRenderer.getInstance().getFrustum();
        boolean useCulling = RelictiumClientMod.options().advanced.useParticleCulling;

        // Setup the frustum state before rendering particles
        if (useCulling && frustum != null) {
            this.cullingFrustum = frustum;
        } else {
            this.cullingFrustum = null;
        }
    }

    @WrapWithCondition(method = {"renderParticles", "renderLitParticles"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;renderParticle(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private boolean filterParticleList(Particle particle, BufferBuilder f8, Entity f9, float f10, float f11, float f12, float vec3d, float v, float buffer) {
        if(this.cullingFrustum == null) {
            return true;
        }

        AxisAlignedBB box = particle.getBoundingBox();

        // Hack: Grow the particle's bounding box in order to work around mis-behaved particles
        return this.cullingFrustum.isBoxInFrustum(box.minX - 1.0D, box.minY - 1.0D, box.minZ - 1.0D, box.maxX + 1.0D, box.maxY + 1.0D, box.maxZ + 1.0D);
    }

}