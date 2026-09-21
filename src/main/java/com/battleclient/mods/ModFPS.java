package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModFPS extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;
    private final ModeSetting style;

    public ModFPS() {
        super("fps", "FPS Display", "Exibe a taxa de quadros por segundo em tempo real", "HUD");
        this.x = 10;
        this.y = 10;
        this.width = 54;
        this.height = 14;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);
        style = new ModeSetting("style", "Estilo", 0, "Padrão", "Apenas Número", "Colorido");

        addSetting(showBackground);
        addSetting(textColor);
        addSetting(style);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        int fps = Minecraft.getDebugFPS();
        String text;

        if (style.is("Apenas Número")) {
            text = String.valueOf(fps);
        } else {
            text = "FPS: " + fps;
        }

        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        int renderColor = textColor.getValue();
        if (style.is("Colorido")) {
            if (fps >= 60) renderColor = 0xFF00FF00;
            else if (fps >= 30) renderColor = 0xFFFFFF00;
            else renderColor = 0xFFFF0000;
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, renderColor);
    }
}
