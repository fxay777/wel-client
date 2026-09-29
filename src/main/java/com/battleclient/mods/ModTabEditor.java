package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModTabEditor extends BaseMod {
    public static ModTabEditor instance;

    private final BooleanSetting showNumericPing;
    private final BooleanSetting hideHeaderFooter;

    public ModTabEditor() {
        super("tabeditor", "Tab Editor", "Personaliza a lista de jogadores (TAB), ping numerico e cabecalho", "HUD");
        instance = this;
        this.width = 120;
        this.height = 30;

        showNumericPing = new BooleanSetting("numPing", "Mostrar Ping Numerico (ms)", true);
        hideHeaderFooter = new BooleanSetting("hideHeader", "Ocultar Cabecalho/Rodape", false);

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
