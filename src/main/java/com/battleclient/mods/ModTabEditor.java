package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModTabEditor extends BaseMod {
    public static ModTabEditor instance;

    private final BooleanSetting showNumericPing;
    private final BooleanSetting hideHeaderFooter;

    public ModTabEditor() {
        super("tabeditor", "Tab Editor", "Personaliza a lista de jogadores (TAB), ping numérico e cabeçalho", "HUD");
        instance = this;
        this.width = 120;
        this.height = 30;

        showNumericPing = new BooleanSetting("numPing", "Mostrar Ping Numérico (ms)", true);
        hideHeaderFooter = new BooleanSetting("hideHeader", "Ocultar Cabeçalho/Rodapé", false);

        addSetting(showNumericPing);
        addSetting(hideHeaderFooter);
    }

    public boolean isShowNumericPing() {
        return enabled && showNumericPing.isEnabled();
    }

    public boolean isHideHeaderFooter() {
        return enabled && hideHeaderFooter.isEnabled();
    }
}
