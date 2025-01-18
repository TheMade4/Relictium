package io.themade4.relictium.core.client.gui.options.control;

import io.themade4.relictium.core.client.gui.options.FormattedTextProvider;
import io.themade4.relictium.core.client.gui.options.Option;
import io.themade4.relictium.core.client.gui.options.TextProvider;
import io.themade4.relictium.core.client.gui.options.named.NamedState;
import io.themade4.relictium.core.client.util.Dim2i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;

public class CyclingControl<T extends Enum<T>> implements Control<T> {
    private final Option<T> option;
    private final T[] allowedValues;
    private final ITextComponent[] names;

    public CyclingControl(Option<T> option, Class<T> enumType) {
        this(option, enumType, enumType.getEnumConstants());
    }

    public CyclingControl(Option<T> option, Class<T> enumType, ITextComponent[] names) {
        T[] universe = enumType.getEnumConstants();

        Validate.isTrue(universe.length == names.length, "Mismatch between universe length and names array length");
        Validate.notEmpty(universe, "The enum universe must contain at least one item");

        this.option = option;
        this.allowedValues = universe;
        this.names = names;
    }

    @Deprecated
    public CyclingControl(Option<T> option, Class<T> enumType, String[] names) {
        this(option, enumType, Arrays.stream(names).map(TextComponentString::new).toArray(ITextComponent[]::new));
    }

    public CyclingControl(Option<T> option, Class<T> enumType, T[] allowedValues) {
        T[] universe = enumType.getEnumConstants();

        this.option = option;
        this.allowedValues = allowedValues;
        this.names = new ITextComponent[universe.length];

        for (int i = 0; i < this.names.length; i++) {
            ITextComponent name;
            T value = universe[i];

            if (value instanceof TextProvider) {
                name = new TextComponentString(((TextProvider)value).getLocalizedName());
            } else if(value instanceof FormattedTextProvider) {
                name = ((FormattedTextProvider)value).getLocalizedName();
            } else {
                name = value instanceof NamedState ? new TextComponentTranslation(((NamedState)value).getKey()) : new TextComponentString(value.name());
            }

            this.names[i] = name;
        }
    }

    @Override
    public Option<T> getOption() {
        return this.option;
    }

    @Override
    public ControlElement<T> createElement(Dim2i dim) {
        return new CyclingControlElement<>(this.option, dim, this.allowedValues, this.names);
    }

    @Override
    public int getMaxWidth() {
        return 70;
    }

    private static class CyclingControlElement<T extends Enum<T>> extends ControlElement<T> {
        private final T[] allowedValues;
        private final ITextComponent[] names;
        private int currentIndex;

        public CyclingControlElement(Option<T> option, Dim2i dim, T[] allowedValues, ITextComponent[] names) {
            super(option, dim);

            this.allowedValues = allowedValues;
            this.names = names;
            this.currentIndex = 0;

            for (int i = 0; i < allowedValues.length; i++) {
                if (allowedValues[i] == option.getValue()) {
                    this.currentIndex = i;
                    break;
                }
            }
        }

        @Override
        public void render(int mouseX, int mouseY, float delta) {
            super.render(mouseX, mouseY, delta);

            Enum<T> value = this.option.getValue();
            ITextComponent name = this.names[value.ordinal()];

            int strWidth = this.getTextWidth(name);
            this.drawString(name.getFormattedText(), this.dim.getLimitX() - strWidth - 6, this.dim.getCenterY() - 4, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.option.isAvailable() && button == 0 && this.dim.containsCursor(mouseX, mouseY)) {
                this.currentIndex = (this.option.getValue().ordinal() + 1) % this.allowedValues.length;
                this.option.setValue(this.allowedValues[this.currentIndex]);
                this.playClickSound();

                return true;
            }

            return false;
        }
    }
}
