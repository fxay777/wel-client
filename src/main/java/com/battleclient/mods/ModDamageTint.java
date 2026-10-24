package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModDamageTint extends BaseMod {
    public static ModDamageTint instance;

    private final BooleanSetting disableTint;
    private final ColorSetting tintColor;
    private final NumberSetting intensity;

    public ModDamageTint() {
        super("damagetint", "Damage Tint", "Personaliza ou desativa o flash vermelho nas bordas da tela ao tomar dano", "Visual");
        instance = this;

        disableTint = new BooleanSetting("disable", "Desativar Flash de Dano", false);
        tintColor = new ColorSetting("color", "Cor do Flash", 0xFFFF0000);
        intensity = new NumberSetting("intensity", "Intensidade", 1.0, 0.2, 2.0, 0.1);

        addSetting(disableTint);
        addSetting(tintColor);
        addSetting(intensity);
    }

    public boolean isDisableTint() {
        return enabled && disableTint.isEnabled();
    }

    public int getTintColor() {
        return tintColor.getValue();
    }

    public float getIntensity() {
        return intensity.getFloatValue();
    }
}
