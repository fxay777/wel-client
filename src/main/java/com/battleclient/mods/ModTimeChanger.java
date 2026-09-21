package com.battleclient.mods;

import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModTimeChanger extends BaseMod {
    public static ModTimeChanger instance;

    private final ModeSetting preset;
    private final NumberSetting customTime;

    public ModTimeChanger() {
        super("timechanger", "Time Changer", "Altera visualmente a hora do dia no client (Dia, Pôr do Sol, Noite)", "Visual");
        instance = this;

        preset = new ModeSetting("preset", "Predefinição", 0, "Dia (6000)", "Pôr do Sol (12000)", "Noite (18000)", "Personalizado");
        customTime = new NumberSetting("time", "Hora Personalizada", 6000.0, 0.0, 24000.0, 500.0);

        addSetting(preset);
        addSetting(customTime);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld != null) {
            long targetTime = 6000L;
            if (preset.is("Dia (6000)")) targetTime = 6000L;
            else if (preset.is("Pôr do Sol (12000)")) targetTime = 12000L;
            else if (preset.is("Noite (18000)")) targetTime = 18000L;
            else if (preset.is("Personalizado")) targetTime = (long) customTime.getValue();

            mc.theWorld.setWorldTime(targetTime);
        }
    }
}
