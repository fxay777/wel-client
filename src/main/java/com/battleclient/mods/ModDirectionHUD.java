package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModDirectionHUD extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModDirectionHUD() {
        super("directionhud", "Direction HUD", "Exibe a direção da bússola em tempo real (N, S, L, O e Graus)", "HUD");
        this.x = 10;
        this.y = 308;
        this.width = 65;
        this.height = 14;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) return;

        float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        int facing = MathHelper.floor_double((double) (mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        String dir = "Norte";
        if (facing == 1) dir = "Leste";
        else if (facing == 2) dir = "Sul";
        else if (facing == 3) dir = "Oeste";

        int deg = (int) (yaw < 0 ? yaw + 360 : yaw);
        String text = dir + " (" + deg + "°)";

        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
