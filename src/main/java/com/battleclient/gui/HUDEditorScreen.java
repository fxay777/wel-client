package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.ModManager;
import com.battleclient.mods.BaseMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class HUDEditorScreen extends GuiScreen {
    private BaseMod draggingMod = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 105, height - 32, 100, 20, "Salvar & Fechar"));
        this.buttonList.add(new GuiButton(1, width / 2 + 5, height - 32, 100, 20, "Resetar Padrões"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Config.salvar();
            mc.displayGuiScreen(null);
        } else if (button.id == 1) {
            int yOffset = 10;
            for (BaseMod mod : ModManager.getInstance().getMods()) {
                if (mod.isHUD()) {
                    mod.setX(10);
                    mod.setY(yOffset);
                    yOffset += mod.getHeight() + 4;
                    if (yOffset > height - 60) {
                        yOffset = 10;
                    }
                }
            }
            Config.salvar();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Fundo escurecido transparente
        drawDefaultBackground();

        // Título e instruções
        drawCenteredString(fontRendererObj, "§9§lBATTLE CLIENT §8- §fHUD EDITOR", width / 2, 12, 0xFFFFFFFF);
        drawCenteredString(fontRendererObj, "§7Arraste os elementos com o mouse para posicioná-los na tela", width / 2, 24, 0xFFAAAAAA);

        // Renderiza as caixas de cada mod HUD
        for (BaseMod mod : ModManager.getInstance().getMods()) {
            if (mod.isHUD() && mod.isEnabled()) {
                int mx = mod.getX();
                int my = mod.getY();
                int mw = mod.getWidth();
                int mh = mod.getHeight();

                boolean hovered = mouseX >= mx && mouseX <= mx + mw && mouseY >= my && mouseY <= my + mh;

                // Desenha a caixa do elemento
                if (mod == draggingMod) {
                    Gui.drawRect(mx, my, mx + mw, my + mh, 0x800055FF);
                } else if (hovered) {
                    Gui.drawRect(mx, my, mx + mw, my + mh, 0x600055FF);
                } else {
                    Gui.drawRect(mx, my, mx + mw, my + mh, 0x50000000);
                }

                // Borda azul forte
                Gui.drawRect(mx, my, mx + mw, my + 1, 0xFF0055FF);
                Gui.drawRect(mx, my, mx + 1, my + mh, 0xFF0055FF);
                Gui.drawRect(mx + mw - 1, my, mx + mw, my + mh, 0xFF0055FF);
                Gui.drawRect(mx, my + mh - 1, mx + mw, my + mh, 0xFF0055FF);

                // Nome do mod e coordenadas
                String info = mod.getDisplayName() + " §8[" + mx + "," + my + "]";
                fontRendererObj.drawStringWithShadow(info, mx + 3, my + (mh - 8) / 2, 0xFFFFFFFF);
            }
        }

        // Se estiver arrastando
        if (draggingMod != null) {
            draggingMod.setX(Math.max(0, Math.min(width - draggingMod.getWidth(), mouseX - dragOffsetX)));
            draggingMod.setY(Math.max(0, Math.min(height - draggingMod.getHeight(), mouseY - dragOffsetY)));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (BaseMod mod : ModManager.getInstance().getMods()) {
                if (mod.isHUD() && mod.isEnabled()) {
                    int mx = mod.getX();
                    int my = mod.getY();
                    int mw = mod.getWidth();
                    int mh = mod.getHeight();

                    if (mouseX >= mx && mouseX <= mx + mw && mouseY >= my && mouseY <= my + mh) {
                        draggingMod = mod;
                        dragOffsetX = mouseX - mx;
                        dragOffsetY = mouseY - my;
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (draggingMod != null) {
            draggingMod = null;
            Config.salvar();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
