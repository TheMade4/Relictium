package io.themade4.relictium.core.client.gui.widgets;

import io.themade4.relictium.core.client.gui.utils.Drawable;
import io.themade4.relictium.core.client.gui.utils.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public abstract class AbstractWidget implements Drawable, Element {
    protected final FontRenderer font;

    protected AbstractWidget() {
        this.font = Minecraft.getMinecraft().fontRenderer;
    }

    protected void drawString(String str, int x, int y, int color) {
        this.font.drawString(str, x, y, color);
    }

    protected void drawRect(double x1, double y1, double x2, double y2, int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        this.drawQuads(vertices -> addQuad(vertices, x1, y1, x2, y2, a, r, g, b));
    }

    protected void drawQuads(Consumer<BufferBuilder> consumer) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        consumer.accept(bufferBuilder);

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    protected static void addQuad(BufferBuilder consumer, double x1, double y1, double x2, double y2, float a, float r, float g, float b) {
        consumer.pos(x2, y1, 0.0D).color(r, g, b, a).endVertex();
        consumer.pos(x1, y1, 0.0D).color(r, g, b, a).endVertex();
        consumer.pos(x1, y2, 0.0D).color(r, g, b, a).endVertex();
        consumer.pos(x2, y2, 0.0D).color(r, g, b, a).endVertex();
    }

    protected void playClickSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    protected int getStringWidth(String text) {
        return this.font.getStringWidth(text);
    }

    protected int getTextWidth(ITextComponent text) {
        return this.font.getStringWidth(text.getFormattedText());
    }
}
