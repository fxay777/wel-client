package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModCoordinates extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModCoordinates() {
        super("coords", "Coordinates", "Exibe as coordenadas X, Y, Z com formatação limpa", "HUD");
        this.x = 10;
        this.y = 64;
        this.width = 90;
        this.height = 14;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) return;

        int xPos = (int) mc.thePlayer.posX;
        int yPos = (int) mc.thePlayer.posY;
        int zPos = (int) mc.thePlayer.posZ;

        String text = String.format("XYZ: %d, %d, %d", xPos, yPos, zPos);
        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
