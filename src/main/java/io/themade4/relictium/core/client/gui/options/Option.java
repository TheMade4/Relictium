package io.themade4.relictium.core.client.gui.options;

import io.themade4.relictium.core.client.gui.options.control.Control;
import io.themade4.relictium.core.client.gui.options.storage.OptionStorage;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

public interface Option<T> {
    ITextComponent getNewName();

    String getName();

    ITextComponent getTooltip();

    OptionImpact getImpact();

    Control<T> getControl();

    T getValue();

    void setValue(T value);

    void reset();

    OptionStorage<?> getStorage();

    boolean isAvailable();

    boolean hasChanged();

    void applyChanges();

    Collection<OptionFlag> getFlags();
}
