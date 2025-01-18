package io.themade4.relictium.core.client.gui.options.control;

import io.themade4.relictium.core.client.gui.options.Option;
import io.themade4.relictium.core.client.util.Dim2i;

public interface Control<T> {
    Option<T> getOption();

    ControlElement<T> createElement(Dim2i dim);

    int getMaxWidth();
}
