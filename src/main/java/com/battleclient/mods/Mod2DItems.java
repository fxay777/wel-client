package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class Mod2DItems extends BaseMod {
    public static Mod2DItems instance;

    private final BooleanSetting inInventoryOnly;

    public Mod2DItems() {
        super("2ditems", "2D Items", "Renderiza itens no inventario em estilo 2D classico e plano", "Visual");
        instance = this;

        inInventoryOnly = new BooleanSetting("invOnly", "Apenas no Inventario", false);
        addSetting(inInventoryOnly);
    }

    public boolean isInInventoryOnly() {
        return inInventoryOnly.isEnabled();
    }
}
