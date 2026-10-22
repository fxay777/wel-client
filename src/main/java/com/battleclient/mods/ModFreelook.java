package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

/**
 * ModFreelook — Perspective / 360° camera with HUD indicator.
 *
 * Modes:
 *  • 360 Livre       — free camera rotation while holding/toggling the key
 *  • Terceira Frente — forces thirdPersonView = 2 (front camera)
 *  • Terceira Costas — forces thirdPersonView = 1 (back camera)
 *
 * Settings:
 *  • keyMode       Hold | Toggle
 *  • invertPitch   flip vertical camera axis
 *  • perspective   360 Livre | Terceira Frente | Terceira Costas
 *  • sensitivity   mouse speed multiplier (0.1 – 3.0)
 *  • showIndicator show "[Perspective]" text at screen top-centre when active
 */
public class ModFreelook extends BaseMod {
    public static ModFreelook instance;

    // ─── Settings ─────────────────────────────────────────────────────────────
    private final ModeSetting    keyMode;
    private final BooleanSetting invertPitch;
    private final ModeSetting    perspectiveMode;
    private final NumberSetting  sensitivity;
    private final BooleanSetting showIndicator;

    // ─── State ────────────────────────────────────────────────────────────────
    private KeyBinding freelookKey;
    private boolean    active              = false;
    private float      cameraYaw           = 0.0F;
    private float      cameraPitch         = 0.0F;
    private int        previousPerspective = 0;
    private boolean    keyPressedLastTick  = false;

    private static final String INDICATOR_TEXT = "[Perspective]";

    public ModFreelook() {
        super("freelook", "Perspective (Freelook)",
              "Permite olhar ao redor em 360 graus livremente sem virar o personagem", "Gameplay");
        instance = this;

        keyMode         = new ModeSetting  ("keyMode",      "Key Mode",          "Modo da tecla", 0,
                                            "Segurar (Hold)", "Alternar (Toggle)");
        invertPitch     = new BooleanSetting("invertPitch", "Invert Vertical",   "Inverter eixo vertical da camera", false);
        perspectiveMode = new ModeSetting  ("perspective",  "Perspective Mode",  "Tipo de perspectiva ao ativar", 0,
                                            "360 Livre", "Terceira Frente", "Terceira Costas");
        sensitivity     = new NumberSetting("sensitivity",  "Mouse Sensitivity", "Multiplicador de sensibilidade do mouse no freelook", 1.0, 0.1, 3.0, 0.1);
        showIndicator   = new BooleanSetting("indicator",   "Show Indicator",    "Mostrar texto '[Perspective]' no HUD enquanto ativo", true);

        addSetting(keyMode);
        addSetting(invertPitch);
        addSetting(perspectiveMode);
        addSetting(sensitivity);
        addSetting(showIndicator);

        freelookKey = new KeyBinding("Perspective 360", org.lwjgl.input.Keyboard.KEY_LMENU, "Battle Client");
        ClientRegistry.registerKeyBinding(freelookKey);
    }

    // ─── Tick ─────────────────────────────────────────────────────────────────
    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!enabled || mc.thePlayer == null || mc.gameSettings == null) {
            if (active) disableFreelook();
            return;
        }

        boolean isKeyDown = freelookKey.isKeyDown();

        if (keyMode.is("Alternar (Toggle)")) {
            if (isKeyDown && !keyPressedLastTick) {
                if (!active) enableFreelook();
                else         disableFreelook();
            }
        } else {
            // Hold mode
            if (isKeyDown && !active) enableFreelook();
            else if (!isKeyDown && active) disableFreelook();
        }

        keyPressedLastTick = isKeyDown;

        // Update free camera rotation (only in 360 Livre mode)
        if (active && perspectiveMode.is("360 Livre") && mc.currentScreen == null) {
            float baseSens  = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float factor    = baseSens * baseSens * baseSens * 8.0F * sensitivity.getFloatValue();

            float dx =  Mouse.getDX() * factor;
            float dy =  Mouse.getDY() * factor;

            if (invertPitch.isEnabled()) dy = -dy;

            cameraYaw   += dx * 0.15F;
            cameraPitch -= dy * 0.15F;
            cameraPitch  = MathHelper.clamp_float(cameraPitch, -90.0F, 90.0F);
        }
    }

    // ─── Activate / Deactivate ────────────────────────────────────────────────
    private void enableFreelook() {
        active              = true;
        cameraYaw           = mc.thePlayer.rotationYaw;
        cameraPitch         = mc.thePlayer.rotationPitch;
        previousPerspective = mc.gameSettings.thirdPersonView;

        // Apply the chosen perspective view
        if (perspectiveMode.is("Terceira Frente")) {
            mc.gameSettings.thirdPersonView = 2;
        } else if (perspectiveMode.is("Terceira Costas")) {
            mc.gameSettings.thirdPersonView = 1;
        } else {
            // 360 Livre — use third-person back so the player model is visible
            mc.gameSettings.thirdPersonView = 1;
        }
    }

    private void disableFreelook() {
        active = false;
        mc.gameSettings.thirdPersonView = previousPerspective;
    }

    // ─── Camera override (360 Livre only) ────────────────────────────────────
    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (enabled && active && perspectiveMode.is("360 Livre")) {
            event.yaw   = cameraYaw;
            event.pitch = cameraPitch;
        }
    }

    // ─── HUD Indicator ────────────────────────────────────────────────────────
    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || !active || !showIndicator.isEnabled()) return;
        if (mc.thePlayer == null) return;

        // Render a small "[Perspective]" label centred at the top of the screen
        int screenW  = mc.displayWidth  / 2; // scaled width  (mc renders at half resolution)
        int screenH  = mc.displayHeight / 2;

        // Use the actual scaled resolution from the event's ScaledResolution when possible
        // Since we don't have it here, approximate from mc fields
        int sw = event.resolution != null ? event.resolution.getScaledWidth()  : screenW;

        int strW   = mc.fontRendererObj.getStringWidth(INDICATOR_TEXT);
        int textX  = (sw - strW) / 2;
        int textY  = 4;

        // Dark pill background
        Gui.drawRect(textX - 4, textY - 2, textX + strW + 4, textY + 10, 0x88000000);

        // Text with blue accent
        mc.fontRendererObj.drawStringWithShadow(INDICATOR_TEXT, textX, textY, 0xFF00AAFF);
    }

    // ─── Accessors ────────────────────────────────────────────────────────────
    public boolean isActive()      { return enabled && active; }
    public float   getCameraYaw()  { return cameraYaw; }
    public float   getCameraPitch(){ return cameraPitch; }
}
