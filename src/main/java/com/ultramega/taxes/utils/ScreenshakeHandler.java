package com.ultramega.taxes.utils;

import net.minecraft.client.Camera;
import net.minecraft.util.RandomSource;

public class ScreenshakeHandler {
    public static float intensity;
    public static float yawOffset;
    public static float pitchOffset;

    public static void cameraTick(Camera camera, RandomSource random) {
        if (intensity >= 0) {
            yawOffset = ModMaths.randomOffset(random, intensity);
            pitchOffset = ModMaths.randomOffset(random, intensity);
            camera.setRotation(camera.getYRot() + yawOffset, camera.getXRot() + pitchOffset, 0.0F);
            intensity -= 0.05f;
        }
    }

    public static void addScreenshake(float intensity) {
        ScreenshakeHandler.intensity = intensity;
    }
}
