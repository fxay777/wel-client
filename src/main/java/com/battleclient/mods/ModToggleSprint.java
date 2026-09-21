package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModToggleSprint extends BaseMod {
    public static ModToggleSprint instance;

    private final BooleanSetting showHUD;
    private final BooleanSetting toggleSneak;
    private final ColorSetting textColor;

    private boolean sprintToggled = true;

    public ModToggleSprint() {
        super("togglesprint", "Toggle Sprint", "Mantém a corrida (Sprint) sempre ativada automaticamente", "Gameplay");
        instance = this;
        this.x = 10;
        this.y = 362;
        this.width = 80;
        this.height = 14;

        showHUD = new BooleanSetting("hud", "Mostrar Indicador no HUD", true);
        toggleSneak = new BooleanSetting("sneak", "Toggle Sneak (Agachar)", false);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showHUD);
        addSetting(toggleSneak);
        addSetting(textColor);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer != null && sprintToggled) {
            // Se o player está andando para frente e tem comida, mantém sprint ativo
            if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!showHUD.isEnabled() || mc.thePlayer == null) return;

        String text = "[Sprinting (Toggled)]";
        if (mc.thePlayer.isSprinting()) {
            text = "§9[Sprinting (Ativo)]";
        } else {
            text = "§7[Sprinting (Pronto)]";
        }

        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        drawHUDBox(x, y, width, height);
        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
