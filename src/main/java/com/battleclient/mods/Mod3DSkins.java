package com.battleclient.mods;

import com.battleclient.core.settings.NumberSetting;

public class Mod3DSkins extends BaseMod {
    public static Mod3DSkins instance;

    private final NumberSetting depth;

    public Mod3DSkins() {
        super("3dskins", "3D Skins", "Renderiza a segunda camada das skins com relevo tridimensional", "Visual");
        instance = this;

        depth = new NumberSetting("depth", "Profundidade 3D", 1.0, 0.5, 2.5, 0.25);
        addSetting(depth);
    }

    public float getDepth() {
        return depth.getFloatValue();
    }
}
