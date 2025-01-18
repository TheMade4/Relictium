package io.themade4.relictium.mixin.features.gui;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public abstract class MixinDebugHud {
    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    @Final
    private FontRenderer fontRenderer;

    private List<String> capturedList = null;

    @Redirect(method = { "renderDebugInfoLeft", "renderDebugInfoRight" }, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int preRenderText(List<String> list) {
        // Capture the list to be rendered later
        this.capturedList = list;

        return 0; // Prevent the rendering of any text
    }

    @Inject(method = "renderDebugInfoLeft", at = @At("RETURN"))
    public void renderLeftText(CallbackInfo ci) {
        this.renderCapturedText(new ScaledResolution(this.mc), false);
    }

    @Inject(method = "renderDebugInfoRight", at = @At("RETURN"))
    public void renderRightText(ScaledResolution resolution, CallbackInfo ci) {
        this.renderCapturedText(resolution, true);
    }

    private void renderCapturedText(ScaledResolution resolution, boolean right) {
        Validate.notNull(this.capturedList, "Failed to capture string list");

        this.renderBackdrop(resolution, this.capturedList, right);
        this.renderStrings(resolution, this.capturedList, right);

        this.capturedList = null;
    }

    private void renderStrings(ScaledResolution resolution, List<String> list, boolean right) {
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);

            if (!Strings.isNullOrEmpty(string)) {
                int height = 9;
                int width = this.fontRenderer.getStringWidth(string);

                float x1 = right ? resolution.getScaledWidth() - 2 - width : 2;
                float y1 = 2 + (height * i);

                this.fontRenderer.drawString(string, (int) x1, (int) y1, 0xe0e0e0);
            }
        }
    }

    private void renderBackdrop(ScaledResolution resolution, List<String> list, boolean right) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        int color = 0x90505050;

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);

            if (Strings.isNullOrEmpty(string)) {
                continue;
            }

            int height = 9;
            int width = this.fontRenderer.getStringWidth(string);

            int x = right ? resolution.getScaledWidth() - 2 - width : 2;
            int y = 2 + height * i;

            float x1 = x - 1;
            float y1 = y - 1;
            float x2 = x + width + 1;
            float y2 = y + height - 1;

            bufferBuilder.pos(x1, y2, 0.0F).color(g, h, k, f).endVertex();
            bufferBuilder.pos(x2, y2, 0.0F).color(g, h, k, f).endVertex();
            bufferBuilder.pos(x2, y1, 0.0F).color(g, h, k, f).endVertex();
            bufferBuilder.pos(x1, y1, 0.0F).color(g, h, k, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
