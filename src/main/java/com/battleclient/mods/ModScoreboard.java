package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModScoreboard extends BaseMod {
    public static ModScoreboard instance;

    private final BooleanSetting hideRedNumbers;
    private final BooleanSetting hideScoreboard;
    private final NumberSetting backgroundAlpha;

    public ModScoreboard() {
        super("scoreboard", "Scoreboard", "Personaliza a sidebar/scoreboard (remove números vermelhos e ajusta transparência)", "HUD");
        instance = this;
        this.x = 0;
        this.y = 0;
        this.width = 100;
        this.height = 100;

        hideRedNumbers = new BooleanSetting("hideNumbers", "Ocultar Números Vermelhos", true);
        hideScoreboard = new BooleanSetting("hideAll", "Ocultar Scoreboard Inteiro", false);
        backgroundAlpha = new NumberSetting("alpha", "Opacidade do Fundo", 0.35, 0.0, 1.0, 0.05);

        addSetting(hideRedNumbers);
        addSetting(hideScoreboard);
        addSetting(backgroundAlpha);
    }

    public boolean isHideRedNumbers() {
        return enabled && hideRedNumbers.isEnabled();
    }

    public boolean isHideScoreboard() {
        return enabled && hideScoreboard.isEnabled();
    }

    public float getBackgroundAlpha() {
        return backgroundAlpha.getFloatValue();
    }
}
