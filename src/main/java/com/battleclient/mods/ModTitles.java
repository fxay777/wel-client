package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModTitles extends BaseMod {
    public static ModTitles instance;

    private final BooleanSetting disableTitles;
    private final NumberSetting titleScale;

    public ModTitles() {
        super("titles", "Titles", "Personaliza escala e exibição de títulos e subtítulos de servidores", "HUD");
        instance = this;
        this.width = 120;
        this.height = 30;

        disableTitles = new BooleanSetting("disable", "Desativar Títulos", false);
        titleScale = new NumberSetting("scale", "Escala do Título", 1.0, 0.2, 2.0, 0.1);

        addSetting(disableTitles);
        addSetting(titleScale);
    }

    public boolean isDisableTitles() {
        return enabled && disableTitles.isEnabled();
    }

    public float getTitleScale() {
        return titleScale.getFloatValue();
    }
}
