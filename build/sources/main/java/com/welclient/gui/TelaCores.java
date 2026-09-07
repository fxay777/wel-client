package com.seuclient.gui;

import com.seuclient.core.Config;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class TelaCores extends GuiScreen {
    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 30, 200, 20, "Texto: " + corHex(Config.corTexto)));
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2, 200, 20, "Destaque: " + corHex(Config.corDestaque)));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 30, 200, 20, "Voltar"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Config.corTexto = Config.corTexto == 0xFFFFFFFF ? 0xFFFFFF55 : 0xFFFFFFFF;
        } else if (button.id == 1) {
            Config.corDestaque = Config.corDestaque == 0xFF00FF00 ? 0xFF55FFFF : 0xFF00FF00;
        } else if (button.id == 2) {
            mc.displayGuiScreen(new TelaConfiguracao());
            return;
        }
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Cores do Seu Client", width / 2, 20, Config.corTexto);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String corHex(int cor) {
        return String.format("#%06X", cor & 0xFFFFFF);
    }
}
