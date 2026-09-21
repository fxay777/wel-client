package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModFOVChanger extends BaseMod {
    public static ModFOVChanger instance;

    private final BooleanSetting disableSpeedModifier;
    private final NumberSetting staticFOV;

    public ModFOVChanger() {
        super("fovchanger", "FOV Changer", "Personaliza o campo de visão (FOV) e desativa alterações bruscas de velocidade", "Gameplay");
        instance = this;

        disableSpeedModifier = new BooleanSetting("noSpeedFOV", "Travar FOV na Corrida/Velocidade", true);
        staticFOV = new NumberSetting("staticFov", "FOV Base Customizado", 90.0, 60.0, 130.0, 1.0);

        addSetting(disableSpeedModifier);
        addSetting(staticFOV);
    }

    public boolean isDisableSpeedModifier() {
        return enabled && disableSpeedModifier.isEnabled();
    }

    public float getStaticFOV() {
        return staticFOV.getFloatValue();
    }
}
