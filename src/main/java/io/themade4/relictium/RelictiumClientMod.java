package io.themade4.relictium;

import io.themade4.relictium.core.client.gui.RelictiumGameOptions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, useMetadata = true)
public class RelictiumClientMod {
    private static RelictiumGameOptions CONFIG;
    public static Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    public static RelictiumGameOptions options() {
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

    private static RelictiumGameOptions loadConfig() {
        return RelictiumGameOptions.load(Minecraft.getMinecraft().gameDir.toPath().resolve("config").resolve(Tags.MOD_ID + "-options.json"));
    }

    public static String getVersion() {
        return Tags.VERSION;
    }

    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }
}