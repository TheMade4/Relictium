package io.themade4.relictium.core.client.gui.options;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public enum OptionImpact {
    LOW(TextFormatting.GREEN, new TextComponentTranslation("relictium.option_impact.low").getFormattedText()),
    MEDIUM(TextFormatting.YELLOW, new TextComponentTranslation("relictium.option_impact.medium").getFormattedText()),
    HIGH(TextFormatting.GOLD, new TextComponentTranslation("relictium.option_impact.high").getFormattedText()),
    EXTREME(TextFormatting.RED, new TextComponentTranslation("relictium.option_impact.extreme").getFormattedText()),
    VARIES(TextFormatting.WHITE, new TextComponentTranslation("relictium.option_impact.varies").getFormattedText());

    private final TextFormatting color;
    private final String text;

    OptionImpact(TextFormatting color, String text) {
        this.color = color;
        this.text = text;
    }

    public String toDisplayString() {
        return this.color + this.text;
    }
}
