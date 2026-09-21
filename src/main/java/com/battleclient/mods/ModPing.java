package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModPing extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModPing() {
        super("ping", "Ping Display", "Mostra a latência com o servidor multiplayer em ms", "HUD");
        this.x = 10;
        this.y = 46;
        this.width = 54;
        this.height = 14;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        int ping = 0;
        if (mc.thePlayer != null && mc.getNetHandler() != null) {
            NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            if (info != null) {
                ping = Math.max(0, info.getResponseTime());
            }
        }

        String text = ping + " ms";
        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
