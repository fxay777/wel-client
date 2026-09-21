package com.battleclient.mods;

import com.battleclient.core.settings.NumberSetting;

public class ModItemPhysics extends BaseMod {
    public static ModItemPhysics instance;

    private final NumberSetting rotationSpeed;

    public ModItemPhysics() {
        super("itemphysics", "Item Physics", "Aplica física realista e rotação aos itens dropados no chão", "Visual");
        instance = this;

        rotationSpeed = new NumberSetting("rotSpeed", "Velocidade de Giro", 1.0, 0.2, 3.0, 0.2);
        addSetting(rotationSpeed);
    }

    public float getRotationSpeed() {
        return rotationSpeed.getFloatValue();
    }
}
