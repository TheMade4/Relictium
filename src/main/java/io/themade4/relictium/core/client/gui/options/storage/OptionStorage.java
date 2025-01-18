package io.themade4.relictium.core.client.gui.options.storage;

public interface OptionStorage<T> {
    T getData();

    void save();
}
