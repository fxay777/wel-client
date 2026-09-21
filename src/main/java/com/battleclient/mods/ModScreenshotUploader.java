package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModScreenshotUploader extends BaseMod {
    public static ModScreenshotUploader instance;

    private final BooleanSetting copyToClipboard;

    public ModScreenshotUploader() {
        super("screenshotuploader", "Screenshot Uploader", "Facilita o compartilhamento e cópia de prints tiradas no jogo", "Utility");
        instance = this;

        copyToClipboard = new BooleanSetting("clipboard", "Notificação Sonora ao Tirar Print", true);
        addSetting(copyToClipboard);
    }

    public boolean isSoundEnabled() {
        return enabled && copyToClipboard.isEnabled();
    }
}
