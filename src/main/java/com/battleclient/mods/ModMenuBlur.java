package com.battleclient.mods;

import com.battleclient.core.settings.NumberSetting;

public class ModMenuBlur extends BaseMod {
    public static ModMenuBlur instance;

    private final NumberSetting blurRadius;

    public ModMenuBlur() {
        super("menublur", "Menu Blur", "Aplica desfoque cinematográfico no fundo ao abrir inventário e menus", "Visual");
        instance = this;

        blurRadius = new NumberSetting("radius", "Raio de Desfoque", 7.0, 1.0, 15.0, 1.0);
        addSetting(blurRadius);
    }

    public int getBlurRadius() {
        return blurRadius.getIntValue();
    }
}
