package io.themade4.relictium.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

public class CameraUtil {
    public static Vec3d getCameraPosition(float partialTicks) {
        return Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(partialTicks);
    }
}
