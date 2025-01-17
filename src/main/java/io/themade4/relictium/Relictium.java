package io.themade4.relictium;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Relictium {

    private static SodiumGameOptions CONFIG;
    public static Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger(Tags.MOD_NAME);
        }

        return LOGGER;
    }

    private static SodiumGameOptions loadConfig() {
        return SodiumGameOptions.load(Minecraft.getMinecraft().gameDir.toPath().resolve("config").resolve(Tags.MOD_ID + "-options.json"));
    }

    public static String getVersion() {
        return Tags.VERSION;
    }

    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }

}
