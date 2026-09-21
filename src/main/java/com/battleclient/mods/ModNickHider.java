package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModNickHider extends BaseMod {
    public static ModNickHider instance;

    private final BooleanSetting hideOthers;

    public ModNickHider() {
        super("nickhider", "Nick Hider", "Substitui seu nick real por 'Você' no chat e telas para streamers", "Utility");
        instance = this;

        hideOthers = new BooleanSetting("hideOthers", "Ocultar Nicks de Outros Jogadores", false);
        addSetting(hideOthers);
    }

    public String replaceNick(String original) {
        if (!enabled || mc.thePlayer == null) return original;
        String myName = mc.thePlayer.getName();
        if (original.contains(myName)) {
            return original.replace(myName, "§9Você§r");
        }
        return original;
    }
}
