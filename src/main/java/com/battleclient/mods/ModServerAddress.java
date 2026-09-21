package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModServerAddress extends BaseMod {
    private final BooleanSetting hideIP;
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModServerAddress() {
        super("serveraddress", "Server Address", "Exibe o endereço do servidor conectado no HUD", "HUD");
        this.x = 10;
        this.y = 326;
        this.width = 70;
        this.height = 14;

        hideIP = new BooleanSetting("hideIP", "Ocultar IP (Streamer)", false);
        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(hideIP);
        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        String address = "Singleplayer";
        if (!mc.isSingleplayer()) {
            ServerData data = mc.getCurrentServerData();
            if (data != null) {
                address = hideIP.isEnabled() ? "Servidor Protegido" : data.serverIP;
            }
        }

        String text = "IP: " + address;
        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
