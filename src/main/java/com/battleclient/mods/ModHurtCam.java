package com.battleclient.mods;

import com.battleclient.core.settings.NumberSetting;

public class ModHurtCam extends BaseMod {
    public static ModHurtCam instance;

    private final NumberSetting intensity;

    public ModHurtCam() {
        super("hurtcam", "HurtCam", "Ajusta ou desativa a oscilação da câmera ao receber dano em combate", "Visual");
        instance = this;

        intensity = new NumberSetting("intensity", "Intensidade da Trepidação", 0.0, 0.0, 1.0, 0.1);
        addSetting(intensity);
    }

    public float getMultiplier() {
        return enabled ? intensity.getFloatValue() : 1.0F;
    }
}
