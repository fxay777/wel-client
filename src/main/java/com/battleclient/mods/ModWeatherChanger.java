package com.battleclient.mods;

import com.battleclient.core.settings.ModeSetting;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModWeatherChanger extends BaseMod {
    public static ModWeatherChanger instance;

    private final ModeSetting weatherMode;

    public ModWeatherChanger() {
        super("weatherchanger", "Weather Changer", "Força clima limpo client-side para máximo FPS e visibilidade", "Visual");
        instance = this;

        weatherMode = new ModeSetting("weather", "Clima Forçado", 0, "Ensolarado (Limpo)", "Chuva", "Neve");
        addSetting(weatherMode);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld != null) {
            if (weatherMode.is("Ensolarado (Limpo)")) {
                mc.theWorld.setRainStrength(0.0F);
                mc.theWorld.setThunderStrength(0.0F);
            } else if (weatherMode.is("Chuva") || weatherMode.is("Neve")) {
                mc.theWorld.setRainStrength(1.0F);
            }
        }
    }
}
