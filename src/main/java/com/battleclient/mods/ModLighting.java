package com.battleclient.mods;

import com.battleclient.core.settings.NumberSetting;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModLighting extends BaseMod {
    public static ModLighting instance;

    private final NumberSetting brightness;
    private float previousGamma = 1.0F;

    public ModLighting() {
        super("lighting", "Fullbright", "Permite enxergar perfeitamente no escuro sem precisar de tochas", "Visual");
        instance = this;

        brightness = new NumberSetting("gamma", "Nível de Brilho", 10.0, 1.0, 15.0, 1.0);
        addSetting(brightness);
    }

    @Override
    public void onEnable() {
        if (mc.gameSettings != null) {
            previousGamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = brightness.getFloatValue();
        }
    }

    @Override
    public void onDisable() {
        if (mc.gameSettings != null) {
            mc.gameSettings.gammaSetting = previousGamma;
        }
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.gameSettings != null && mc.gameSettings.gammaSetting < brightness.getFloatValue()) {
            mc.gameSettings.gammaSetting = brightness.getFloatValue();
        }
    }
}
