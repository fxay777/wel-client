package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModScrollableTooltips extends BaseMod {
    public static ModScrollableTooltips instance;

    private final BooleanSetting wrapText;

    public ModScrollableTooltips() {
        super("scrollabletooltips", "Scrollable Tooltips", "Permite rolar descrições longas de itens que passam da tela", "Utility");
        instance = this;

        wrapText = new BooleanSetting("wrap", "Quebrar Linhas Automaticamente", true);
        addSetting(wrapText);
    }

    public boolean isWrapText() {
        return enabled && wrapText.isEnabled();
    }
}
