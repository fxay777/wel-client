package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModNametags extends BaseMod {
    public static ModNametags instance;

    private final BooleanSetting showHealth;
    private final BooleanSetting showDistance;
    private final NumberSetting scale;

    public ModNametags() {
        super("nametags", "Nametags", "Personaliza as etiquetas dos jogadores (exibe vida, distância e escala)", "Visual");
        instance = this;

        showHealth = new BooleanSetting("showHealth", "Exibir Vida (HP)", true);
        showDistance = new BooleanSetting("showDistance", "Exibir Distância", false);
        scale = new NumberSetting("scale", "Escala da Etiqueta", 1.0, 0.5, 2.0, 0.1);

        addSetting(showHealth);
        addSetting(showDistance);
        addSetting(scale);
    }

    public boolean isShowHealth() {
        return enabled && showHealth.isEnabled();
    }

    public boolean isShowDistance() {
        return enabled && showDistance.isEnabled();
    }

    public float getScale() {
        return scale.getFloatValue();
    }
}
