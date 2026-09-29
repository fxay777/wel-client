package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModPackOrganizer extends BaseMod {
    public static ModPackOrganizer instance;

    private final BooleanSetting fastLoad;

    public ModPackOrganizer() {
        super("packorganizer", "Pack Organizer", "Otimiza a organizacao e troca rapida de Texture Packs", "Utility");
        instance = this;

        fastLoad = new BooleanSetting("fastLoad", "Carregamento Rapido de Texturas", true);
        addSetting(fastLoad);
    }

    public boolean isFastLoad() {
        return enabled && fastLoad.isEnabled();
    }
}
