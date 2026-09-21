package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.NumberSetting;

public class ModChat extends BaseMod {
    public static ModChat instance;

    private final BooleanSetting showTimestamps;
    private final BooleanSetting transparentBackground;
    private final NumberSetting chatWidth;

    public ModChat() {
        super("chat", "Chat Mod", "Personaliza o chat (timestamps, transparência e largura)", "HUD");
        instance = this;
        this.width = 120;
        this.height = 30;

        showTimestamps = new BooleanSetting("timestamps", "Mostrar Horário [HH:mm]", false);
        transparentBackground = new BooleanSetting("transBg", "Fundo Totalmente Transparente", false);
        chatWidth = new NumberSetting("chatWidth", "Largura do Chat", 320, 200, 500, 20);

        addSetting(showTimestamps);
        addSetting(transparentBackground);
        addSetting(chatWidth);
    }

    public boolean isShowTimestamps() {
        return enabled && showTimestamps.isEnabled();
    }

    public boolean isTransparentBackground() {
        return enabled && transparentBackground.isEnabled();
    }

    public float getChatWidth() {
        return chatWidth.getFloatValue();
    }
}
