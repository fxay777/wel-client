package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModPackOrganizer extends BaseMod {
    public static ModPackOrganizer instance;

    private final BooleanSetting fastLoad;

    public ModPackOrganizer() {
        super("packorganizer", "Pack Organizer", "Otimiza a organização e troca rápida de Texture Packs", "Utility");
        instance = this;

        fastLoad = new BooleanSetting("fastLoad", "Carregamento Rápido de Texturas", true);
        addSetting(fastLoad);
    }

    public boolean isFastLoad() {
        return enabled && fastLoad.isEnabled();
    }
}
