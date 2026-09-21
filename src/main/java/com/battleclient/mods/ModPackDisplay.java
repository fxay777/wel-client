package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.List;

public class ModPackDisplay extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModPackDisplay() {
        super("packdisplay", "Pack Display", "Exibe o nome da textura (Resource Pack) ativa no HUD", "HUD");
        this.x = 10;
        this.y = 344;
        this.width = 75;
        this.height = 14;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        String packName = "Default";
        List<ResourcePackRepository.Entry> repo = mc.getResourcePackRepository().getRepositoryEntries();
        if (repo != null && !repo.isEmpty()) {
            packName = repo.get(repo.size() - 1).getResourcePackName();
            if (packName.endsWith(".zip")) {
                packName = packName.substring(0, packName.length() - 4);
            }
        }

        String text = "Pack: " + packName;
        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
