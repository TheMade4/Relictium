package io.themade4.relictium.core.client.render.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.common.Loader;
import zone.rong.loliasm.client.sprite.ondemand.IAnimatedSpriteActivator;

public class SpriteUtil {
    private static final boolean USING_CENSORED_ASM = Loader.isModLoaded("loliasm");
    private static final boolean USING_FERMIUM_ASM = Loader.isModLoaded("normalasm");
    public static void markSpriteActive(TextureAtlasSprite sprite) {
        if (USING_CENSORED_ASM && sprite instanceof IAnimatedSpriteActivator) {
            ((IAnimatedSpriteActivator) sprite).setActive(true);
        } else if (USING_FERMIUM_ASM && sprite instanceof mirror.normalasm.client.sprite.ondemand.IAnimatedSpriteActivator) {
            ((mirror.normalasm.client.sprite.ondemand.IAnimatedSpriteActivator) sprite).setActive(true);
        }
    }
}
