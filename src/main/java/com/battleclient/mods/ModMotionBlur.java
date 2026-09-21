package com.battleclient.mods;

import com.battleclient.core.settings.NumberSetting;

public class ModMotionBlur extends BaseMod {
    public static ModMotionBlur instance;

    private final NumberSetting blurAmount;

    public ModMotionBlur() {
        super("motionblur", "Motion Blur", "Aplica desfoque de movimento suave e cinematográfico na câmera", "Visual");
        instance = this;

        blurAmount = new NumberSetting("blurAmount", "Intensidade do Blur", 4.0, 1.0, 10.0, 1.0);
        addSetting(blurAmount);
    }

    public float getBlurFactor() {
        return (float) (blurAmount.getValue() / 10.0F);
    }
}
