package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ModZoom extends BaseMod {
    public static ModZoom instance;

    private final NumberSetting zoomFactor;
    private final BooleanSetting smoothCamera;

    private KeyBinding zoomKey;
    private float defaultFOV = 70.0F;
    private boolean isZooming = false;

    public ModZoom() {
        super("zoom", "Zoom", "Aproximação suave da visão estilo Optifine segurando a tecla C", "Gameplay");
        instance = this;

        zoomFactor = new NumberSetting("factor", "Fator de Aproximação", 3.0, 1.5, 6.0, 0.5);
        smoothCamera = new BooleanSetting("smooth", "Câmera Cinemática ao Zoomar", true);

        addSetting(zoomFactor);
        addSetting(smoothCamera);

        zoomKey = new KeyBinding("Zoom Battle Client", Keyboard.KEY_C, "Battle Client");
        ClientRegistry.registerKeyBinding(zoomKey);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.gameSettings == null) return;

        if (zoomKey.isKeyDown()) {
            if (!isZooming) {
                defaultFOV = mc.gameSettings.fovSetting;
                isZooming = true;
                if (smoothCamera.isEnabled()) {
                    mc.gameSettings.smoothCamera = true;
                }
            }
            mc.gameSettings.fovSetting = defaultFOV / zoomFactor.getFloatValue();
        } else if (isZooming) {
            mc.gameSettings.fovSetting = defaultFOV;
            isZooming = false;
            if (smoothCamera.isEnabled()) {
                mc.gameSettings.smoothCamera = false;
            }
        }
    }
}
