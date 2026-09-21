package com.battleclient.mods;

import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModHitColor extends BaseMod {
    public static ModHitColor instance;

    private final ColorSetting hitColor;
    private final NumberSetting alpha;

    public ModHitColor() {
        super("hitcolor", "Hit Color", "Muda a cor e intensidade do flash de dano das entidades atingidas", "Visual");
        instance = this;

        hitColor = new ColorSetting("color", "Cor do Dano", 0xFF0055FF);
        alpha = new NumberSetting("alpha", "Opacidade", 0.5, 0.1, 1.0, 0.05);

        addSetting(hitColor);
        addSetting(alpha);
    }

    public int getHitColor() {
        return hitColor.getValue();
    }

    public float getAlpha() {
        return alpha.getFloatValue();
    }
}
