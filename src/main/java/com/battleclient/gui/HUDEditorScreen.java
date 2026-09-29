package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.ModManager;
import com.battleclient.mods.BaseMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class HUDEditorScreen extends GuiScreen {
    private BaseMod draggingMod = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    @Override
    public void initGui() {
        this.buttonList.clear();
        int btnW = 120;
        int btnH = 20;
        this.buttonList.add(new GuiButton(0, width / 2 - btnW - 5, height - 32, btnW, btnH, "§aSalvar & Sair"));
        this.buttonList.add(new GuiButton(1, width / 2 + 5, height - 32, btnW, btnH, "§cResetar Posições"));
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
                    yOffset += mod.getHeight() + 6;
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
        // Fundo escurecido semi-transparente
        drawDefaultBackground();

        // Barra de topo com branding
        Gui.drawRect(0, 0, width, 28, 0xDD0a0a0a);
        Gui.drawRect(0, 27, width, 28, 0xFF0055FF);

        drawCenteredString(fontRendererObj, "§9§lBATTLE CLIENT §8— §fHUD EDITOR", width / 2, 6, 0xFFFFFFFF);
        drawCenteredString(fontRendererObj, "§7Clique e arraste qualquer elemento do HUD para escolher onde ele fica na tela", width / 2, 17, 0xFFAAAAAA);

        // Se estiver arrastando, atualiza a posição e faz snap suave às bordas
        if (draggingMod != null) {
            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;

            // Snap magnético às bordas (distância <= 10px)
            if (Math.abs(newX) < 10) newX = 4;
            if (Math.abs(newX + draggingMod.getWidth() - width) < 10) newX = width - draggingMod.getWidth() - 4;
            if (Math.abs(newY - 30) < 10) newY = 32;
            if (Math.abs(newY + draggingMod.getHeight() - height) < 10) newY = height - draggingMod.getHeight() - 4;

            draggingMod.setX(Math.max(2, Math.min(width - draggingMod.getWidth() - 2, newX)));
            draggingMod.setY(Math.max(30, Math.min(height - draggingMod.getHeight() - 2, newY)));
        }

        // Renderiza as caixas e previews de todos os mods HUD ativos
        for (BaseMod mod : ModManager.getInstance().getMods()) {
            if (mod.isHUD() && mod.isEnabled()) {
                int mx = mod.getX();
                int my = mod.getY();
                int mw = Math.max(20, mod.getWidth());
                int mh = Math.max(12, mod.getHeight());

                boolean isDragging = (mod == draggingMod);
                boolean hovered = mouseX >= mx && mouseX <= mx + mw && mouseY >= my && mouseY <= my + mh;

                // Fundo do elemento no editor
                int bgColor = isDragging ? 0x700055FF : (hovered ? 0x500055FF : 0x40000000);
                Gui.drawRect(mx, my, mx + mw, my + mh, bgColor);

                // Contorno azul estilo CheatBreaker / Lunar
                int borderColor = isDragging ? 0xFF55FFFF : (hovered ? 0xFF0088FF : 0xFF0055FF);
                Gui.drawRect(mx, my, mx + mw, my + 1, borderColor);
                Gui.drawRect(mx, my, mx + 1, my + mh, borderColor);
                Gui.drawRect(mx + mw - 1, my, mx + mw, my + mh, borderColor);
                Gui.drawRect(mx, my + mh - 1, mx + mw, my + mh, borderColor);

                // Nome e coordenadas do mod
                String label = "§f" + mod.getDisplayName() + " §8[" + mx + ", " + my + "]";
                fontRendererObj.drawStringWithShadow(label, mx + 3, my + (mh - 8) / 2, 0xFFFFFFFF);
            }
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
                    int mw = Math.max(20, mod.getWidth());
                    int mh = Math.max(12, mod.getHeight());

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
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_H) {
            Config.salvar();
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
