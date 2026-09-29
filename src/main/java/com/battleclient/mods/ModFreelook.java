package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ModeSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ModFreelook extends BaseMod {
    public static ModFreelook instance;

    private final ModeSetting keyMode; // Hold ou Toggle
    private final BooleanSetting invertPitch;

    private KeyBinding freelookKey;
    private boolean active = false;
    private float cameraYaw = 0.0F;
    private float cameraPitch = 0.0F;
    private int previousPerspective = 0;
    private boolean keyPressedLastTick = false;

    public ModFreelook() {
        super("freelook", "Perspective (Freelook)", "Permite olhar ao redor em 360 graus livremente sem virar o personagem", "Gameplay");
        instance = this;

        keyMode = new ModeSetting("keyMode", "Key Mode", "Modo da tecla", 0, "Segurar (Hold)", "Alternar (Toggle)");
        invertPitch = new BooleanSetting("invertPitch", "Invert Vertical", "Inverter eixo vertical da câmera", false);

        addSetting(keyMode);
        addSetting(invertPitch);

        freelookKey = new KeyBinding("Perspective 360", Keyboard.KEY_LMENU, "Battle Client"); // Left Alt
        ClientRegistry.registerKeyBinding(freelookKey);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!enabled || mc.thePlayer == null || mc.gameSettings == null) {
            if (active) disableFreelook();
            return;
        }

        boolean isKeyDown = freelookKey.isKeyDown();

        if (keyMode.is("Alternar (Toggle)")) {
            if (isKeyDown && !keyPressedLastTick) {
                if (!active) {
                    enableFreelook();
                } else {
                    disableFreelook();
                }
            }
        } else {
            // Hold mode
            if (isKeyDown) {
                if (!active) {
                    enableFreelook();
                }
            } else if (active) {
                disableFreelook();
            }
        }

        keyPressedLastTick = isKeyDown;

        // Se ativo e em tela de jogo, atualiza a rotação da câmera com o mouse
        if (active && mc.currentScreen == null) {
            float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float factor = sens * sens * sens * 8.0F;

            float dx = Mouse.getDX() * factor;
            float dy = Mouse.getDY() * factor;

            if (invertPitch.isEnabled()) {
                dy = -dy;
            }

            cameraYaw += dx * 0.15F;
            cameraPitch -= dy * 0.15F;
            cameraPitch = MathHelper.clamp_float(cameraPitch, -90.0F, 90.0F);
        }
    }

    private void enableFreelook() {
        active = true;
        cameraYaw = mc.thePlayer.rotationYaw;
        cameraPitch = mc.thePlayer.rotationPitch;
        previousPerspective = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 1; // Terceira pessoa costas
    }

    private void disableFreelook() {
        active = false;
        mc.gameSettings.thirdPersonView = previousPerspective;
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (enabled && active) {
            event.yaw = cameraYaw;
            event.pitch = cameraPitch;
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
