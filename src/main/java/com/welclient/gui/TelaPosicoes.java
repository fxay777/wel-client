package com.seuclient.gui;

import com.seuclient.core.Config;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class TelaPosicoes extends GuiScreen {
    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 30, 200, 20, "Teclas: " + Config.posXTeclas + ", " + Config.posYTeclas));
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2, 200, 20, "FPS: " + Config.posXFPS + ", " + Config.posYFPS));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 30, 200, 20, "Voltar"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Config.posXTeclas = Config.posXTeclas == 10 ? 20 : 10;
        } else if (button.id == 1) {
            Config.posXFPS = Config.posXFPS == 10 ? 20 : 10;
        } else if (button.id == 2) {
            mc.displayGuiScreen(new TelaConfiguracao());
            return;
        }
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Posicoes do Seu Client", width / 2, 20, 0xFFFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
