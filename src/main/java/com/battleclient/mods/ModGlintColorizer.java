package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;

import java.awt.Color;

public class ModGlintColorizer extends BaseMod {
    public static ModGlintColorizer instance;

    private final ColorSetting glintColor;
    private final BooleanSetting chroma;

    public ModGlintColorizer() {
        super("glintcolorizer", "Glint Colorizer", "Personaliza a cor do brilho de itens e armaduras encantadas", "Visual");
        instance = this;

        glintColor = new ColorSetting("color", "Cor do Brilho", 0xFF0055FF);
        chroma = new BooleanSetting("chroma", "Efeito Arco-íris (Chroma)", false);

        addSetting(glintColor);
        addSetting(chroma);
    }

    public int getColor() {
        if (chroma.isEnabled()) {
            float hue = (System.currentTimeMillis() % 3000L) / 3000.0F;
            return Color.HSBtoRGB(hue, 0.8F, 1.0F);
        }
        return glintColor.getValue();
    }
}
