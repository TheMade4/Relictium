package io.themade4.relictium.core.client.gl.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;

import io.themade4.relictium.core.client.render.chunk.shader.ChunkFogMode;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class FogHelper {
    private static final float FAR_PLANE_THRESHOLD_EXP = (float) Math.log(1.0f / 0.0019f);
    private static final float FAR_PLANE_THRESHOLD_EXP2 = MathHelper.sqrt(FAR_PLANE_THRESHOLD_EXP);

    public static float getFogEnd() {
    	return GlStateManager.fogState.end;
    }

    public static float getFogStart() {
    	return GlStateManager.fogState.start;
    }

    public static float getFogDensity() {
    	return GlStateManager.fogState.density;
    }

    /**
     * Retrieves the current fog mode from the fixed-function pipeline.
     */
    public static ChunkFogMode getFogMode() {
        int mode = GlStateManager.fogState.mode;
        
        if(mode == 0 || !GlStateManager.fogState.fog.currentState)
        	return ChunkFogMode.NONE;

        switch (mode) {
            case GL11.GL_EXP2:
            case GL11.GL_EXP:
                return ChunkFogMode.EXP2;
            case GL11.GL_LINEAR:
                return ChunkFogMode.LINEAR;
            default:
                throw new UnsupportedOperationException("Unknown fog mode: " + mode);
        }
    }

    public static float getFogCutoff() {
    	int mode = GlStateManager.fogState.mode;

        switch (mode) {
            case GL11.GL_LINEAR:
                return getFogEnd();
            case GL11.GL_EXP:
                return FAR_PLANE_THRESHOLD_EXP / getFogDensity();
            case GL11.GL_EXP2:
                return FAR_PLANE_THRESHOLD_EXP2 / getFogDensity();
            default:
                return 0.0f;
        }
    }
    
    public static float[] getFogColor() {
        EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
    	return new float[]{entityRenderer.fogColorRed, entityRenderer.fogColorGreen, entityRenderer.fogColorBlue, 1.0F};
    }
}
