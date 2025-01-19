package io.themade4.relictium;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = "vintagium", name = Relictium.MOD_NAME, version = Relictium.MOD_VERSION, useMetadata = true)
public class Relictium {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;

    private static SodiumGameOptions CONFIG;
    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger(MOD_NAME);
        }

        return LOGGER;
    }

    private static SodiumGameOptions loadConfig() {
        return SodiumGameOptions.load(Minecraft.getMinecraft().gameDir.toPath().resolve("config").resolve(MOD_ID + "-options.json"));
    }

    public static String getVersion() {
        return MOD_VERSION;
    }

    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }
}