package io.themade4.relictium.core.client.gui.options.storage;

import io.themade4.relictium.RelictiumClientMod;
import io.themade4.relictium.Tags;
import io.themade4.relictium.core.client.gui.RelictiumGameOptions;

import java.io.IOException;

public class SodiumOptionsStorage implements OptionStorage<RelictiumGameOptions> {
    private final RelictiumGameOptions options;

    public SodiumOptionsStorage() {
        this.options = RelictiumClientMod.options();
    }

    @Override
    public RelictiumGameOptions getData() {
        return this.options;
    }

    @Override
    public void save() {
        try {
            this.options.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save configuration changes", e);
        }

        RelictiumClientMod.logger().info("Flushed changes to " + Tags.MOD_NAME + " configuration");
    }
}
