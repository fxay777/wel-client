package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModBossBar extends BaseMod {
    public static ModBossBar instance;

    private final BooleanSetting hideBossBar;
    private final BooleanSetting hideBossText;
    private final NumberSetting scale;

    public ModBossBar() {
        super("bossbar", "Boss Bar", "Personaliza a Boss Bar (Wither, Dragon, Timers de Servidores)", "HUD");
        instance = this;
        this.width = 180;
        this.height = 20;

        hideBossBar = new BooleanSetting("hideBar", "Ocultar Barra", false);
        hideBossText = new BooleanSetting("hideText", "Ocultar Texto da Barra", false);
        scale = new NumberSetting("scale", "Escala", 1.0, 0.4, 1.5, 0.1);

        addSetting(hideBossBar);
        addSetting(hideBossText);
        addSetting(scale);
    }

    public boolean isHideBossBar() {
        return enabled && hideBossBar.isEnabled();
    }

    public boolean isHideBossText() {
        return enabled && hideBossText.isEnabled();
    }

    public float getScale() {
        return scale.getFloatValue();
    }
}
