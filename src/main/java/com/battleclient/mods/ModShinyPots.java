package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModShinyPots extends BaseMod {
    public static ModShinyPots instance;

    private final BooleanSetting splashOnly;

    public ModShinyPots() {
        super("shinypots", "Shiny Pots", "Adiciona brilho de encantamento reluzente em frascos de poção", "Visual");
        instance = this;

        splashOnly = new BooleanSetting("splashOnly", "Apenas Poções Arremessáveis", true);
        addSetting(splashOnly);
    }

    public boolean isSplashOnly() {
        return splashOnly.isEnabled();
    }
}
