package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ModFreelook extends BaseMod {
    public static ModFreelook instance;

    private final BooleanSetting invertPitch;
    private KeyBinding freelookKey;

    private boolean active = false;
    private float cameraYaw = 0.0F;
    private float cameraPitch = 0.0F;
    private int previousPerspective = 0;

    public ModFreelook() {
        super("freelook", "Freelook (Perspective)", "Permite olhar ao redor em 360° sem alterar a direção de movimento", "Gameplay");
        instance = this;

        invertPitch = new BooleanSetting("invertPitch", "Inverter Vertical", false);
        addSetting(invertPitch);

        freelookKey = new KeyBinding("Freelook Battle Client", Keyboard.KEY_LALT, "Battle Client");
        ClientRegistry.registerKeyBinding(freelookKey);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.gameSettings == null) return;

        if (freelookKey.isKeyDown()) {
            if (!active) {
                active = true;
                cameraYaw = mc.thePlayer.rotationYaw;
                cameraPitch = mc.thePlayer.rotationPitch;
                previousPerspective = mc.gameSettings.thirdPersonView;
                mc.gameSettings.thirdPersonView = 1; // 3ª pessoa
            }
        } else if (active) {
            active = false;
            mc.gameSettings.thirdPersonView = previousPerspective;
        }
    }

    public boolean isActive() {
        return enabled && active;
    }

    public float getCameraYaw() {
        return cameraYaw;
    }

    public float getCameraPitch() {
        return cameraPitch;
    }
}
