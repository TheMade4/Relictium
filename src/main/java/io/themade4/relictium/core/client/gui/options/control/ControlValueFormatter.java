package io.themade4.relictium.core.client.gui.options.control;

import net.minecraft.util.text.TextComponentTranslation;

public interface ControlValueFormatter {
    static ControlValueFormatter guiScale() {
        return (v) -> (v == 0) ? new TextComponentTranslation("options.guiScale.auto").getFormattedText() : new TextComponentTranslation(v + "x").getFormattedText();
    }

    static ControlValueFormatter fpsLimit() {
        return (v) -> (v == 260) ? new TextComponentTranslation("options.framerateLimit.max").getFormattedText() : new TextComponentTranslation("options.framerate", v).getFormattedText();
    }

    static ControlValueFormatter brightness() {
        return (v) -> {
            if (v == 0) {
                return new TextComponentTranslation("options.gamma.min").getFormattedText();
            } else if (v == 100) {
                return new TextComponentTranslation("options.gamma.max").getFormattedText();
            } else {
                return v + "%";
            }
        };
    }

    String format(int value);

    static ControlValueFormatter percentage() {
        return (v) -> v + "%";
    }

    static ControlValueFormatter multiplier() {
        return (v) -> new TextComponentTranslation(v + "x").getFormattedText();
    }

    static ControlValueFormatter quantity(String name) {
        return (v) -> new TextComponentTranslation(name, v).getFormattedText();
    }

    static ControlValueFormatter quantityOrDisabled(String name, String disableText) {
        return (v) -> new TextComponentTranslation(v == 0 ? disableText : name, v).getFormattedText();
    }

    static ControlValueFormatter number() {
        return String::valueOf;
    }
}
