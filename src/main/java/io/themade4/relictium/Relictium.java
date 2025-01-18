package io.themade4.relictium;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Relictium.MODID, useMetadata = true)
public class Relictium {

    public static final String MODID = Tags.MOD_ID;
    public static final String MODNAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;

    private static SodiumGameOptions CONFIG;
    public static Logger LOGGER = LogManager.getLogger(MODNAME);

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger(MODNAME);
        }

        return LOGGER;
    }

    private static SodiumGameOptions loadConfig() {
        return SodiumGameOptions.load(Minecraft.getMinecraft().gameDir.toPath().resolve("config").resolve(MODID + "-options.json"));
    }

    public static String getVersion() {
        return MOD_VERSION;
    }

    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }
}