package io.themade4.relictium.core.client.gui.options.storage;

import io.themade4.relictium.RelictiumClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class MinecraftOptionsStorage implements OptionStorage<GameSettings> {
    private final Minecraft client;

    public MinecraftOptionsStorage() {
        this.client = Minecraft.getMinecraft();
    }

    @Override
    public GameSettings getData() {
        return this.client.gameSettings;
    }

    @Override
    public void save() {
        this.getData().saveOptions();

        RelictiumClientMod.logger().info("Flushed changes to Minecraft configuration");
    }
}
