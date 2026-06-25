package com.scrp.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GUIConfig {
    private static float transparency = 1.0f;
    private static int backgroundColor = 0xFF1A1A1A;
    private static int slotColor = 0xFF3A3A3A;
    private static int borderColor = 0xFF8B8B8B;

    public static float getTransparency() {
        return transparency;
    }

    public static void setTransparency(float value) {
        transparency = Math.max(0.0f, Math.min(1.0f, value));
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public static int getSlotColor() {
        return slotColor;
    }

    public static void setSlotColor(int color) {
        slotColor = color;
    }

    public static int getBorderColor() {
        return borderColor;
    }

    public static void setBorderColor(int color) {
        borderColor = color;
    }

    public static void reset() {
        transparency = 1.0f;
        backgroundColor = 0xFF1A1A1A;
        slotColor = 0xFF3A3A3A;
        borderColor = 0xFF8B8B8B;
    }
}
