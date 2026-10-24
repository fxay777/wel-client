package com.battleclient.core.settings;

public class ColorSetting extends ModSetting<Integer> {

    public ColorSetting(String id, String displayName, String description, int defaultColor) {
        super(id, displayName, description, defaultColor);
    }

    public ColorSetting(String id, String displayName, int defaultColor) {
        this(id, displayName, "", defaultColor);
    }

    public int getRed() {
        return (value >> 16) & 0xFF;
    }

    public int getGreen() {
        return (value >> 8) & 0xFF;
    }

    public int getBlue() {
        return value & 0xFF;
    }

    public int getAlpha() {
        return (value >> 24) & 0xFF;
    }

    public void setColor(int r, int g, int b, int a) {
        this.value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
