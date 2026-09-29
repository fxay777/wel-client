package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.ModSetting;
import com.battleclient.core.settings.NumberSetting;
import com.battleclient.mods.BaseMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class ModSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final BaseMod mod;

    private int scrollOffset = 0;
    private NumberSetting draggingSlider = null;

    public ModSettingsScreen(GuiScreen parent, BaseMod mod) {
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            if (dWheel > 0) {
                scrollOffset = Math.max(0, scrollOffset - 20);
            } else {
                scrollOffset += 20;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int modalW = 340;
        int modalH = 260;
        int modalX = width / 2 - modalW / 2;
        int modalY = height / 2 - modalH / 2;

        // Fundo da Janela Modal (Estilo CheatBreaker / Lunar)
        Gui.drawRect(modalX, modalY, modalX + modalW, modalY + modalH, 0xF5141414);
        Gui.drawRect(modalX, modalY, modalX + modalW, modalY + 28, 0xFF1C1C1C);
        Gui.drawRect(modalX, modalY + 27, modalX + modalW, modalY + 28, 0xFF0055FF);

        // Borda sutil da janela
        Gui.drawRect(modalX, modalY, modalX + modalW, modalY + 1, 0x40FFFFFF);
        Gui.drawRect(modalX, modalY, modalX + 1, modalY + modalH, 0x30FFFFFF);
        Gui.drawRect(modalX + modalW - 1, modalY, modalX + modalW, modalY + modalH, 0x30FFFFFF);
        Gui.drawRect(modalX, modalY + modalH - 1, modalX + modalW, modalY + modalH, 0x30FFFFFF);

        // Título do Mod no cabeçalho
        fontRendererObj.drawStringWithShadow("§f§l" + mod.getDisplayName().toUpperCase() + " §8OPTIONS", modalX + 12, modalY + 10, 0xFFFFFFFF);

        // Botão de HUD Editor no topo à direita (se for mod HUD)
        if (mod.isHUD()) {
            int hudX = modalX + modalW - 95;
            int hudY = modalY + 6;
            boolean hudHover = mouseX >= hudX && mouseX <= hudX + 65 && mouseY >= hudY && mouseY <= hudY + 16;
            Gui.drawRect(hudX, hudY, hudX + 65, hudY + 16, hudHover ? 0xFF0055FF : 0xFF2A2A2A);
            drawCenteredString(fontRendererObj, "§b[✥ Mover]", hudX + 32, hudY + 4, 0xFFFFFFFF);
        }

        // Botão Fechar / Voltar (✕)
        int closeX = modalX + modalW - 22;
        int closeY = modalY + 6;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + 16 && mouseY >= closeY && mouseY <= closeY + 16;
        Gui.drawRect(closeX, closeY, closeX + 16, closeY + 16, closeHover ? 0xFFAA2222 : 0xFF2A2A2A);
        drawCenteredString(fontRendererObj, "§f✕", closeX + 8, closeY + 4, 0xFFFFFFFF);

        // Area de configuracoes com recorte de scroll
        int contentY = modalY + 34;
        int maxVisibleY = modalY + modalH - 10;

        if (mod.getSettings().isEmpty()) {
            drawCenteredString(fontRendererObj, "§7Este mod não possui configurações adicionais.", width / 2, height / 2, 0xFFAAAAAA);
        } else {
            int curY = contentY - scrollOffset;

            for (ModSetting<?> setting : mod.getSettings()) {
                if (curY + 24 > contentY && curY < maxVisibleY) {
                    // Linha da setting
                    int rowX = modalX + 12;
                    int rowW = modalW - 24;

                    // Fundo da linha suave
                    boolean rowHover = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + 22;
                    if (rowHover) {
                        Gui.drawRect(rowX, curY, rowX + rowW, curY + 22, 0x20FFFFFF);
                    }

                    // Nome da Setting
                    fontRendererObj.drawStringWithShadow("§7" + setting.getDisplayName(), rowX + 4, curY + 7, 0xFFE0E0E0);

                    int ctrlW = 100;
                    int ctrlX = rowX + rowW - ctrlW - 2;
                    int ctrlY = curY + 3;

                    if (setting instanceof BooleanSetting) {
                        BooleanSetting bs = (BooleanSetting) setting;
                        boolean on = bs.isEnabled();

                        // Seletor estilo CheatBreaker: < ON > ou < OFF >
                        drawCenteredString(fontRendererObj, on ? "§8< §a§lON §8>" : "§8< §cOFF §8>", ctrlX + ctrlW / 2, ctrlY + 4, 0xFFFFFFFF);

                    } else if (setting instanceof ModeSetting) {
                        ModeSetting ms = (ModeSetting) setting;
                        String val = ms.getValue();
                        if (val.length() > 14) val = val.substring(0, 12) + "..";

                        drawCenteredString(fontRendererObj, "§8< §b" + val + " §8>", ctrlX + ctrlW / 2, ctrlY + 4, 0xFFFFFFFF);

                    } else if (setting instanceof NumberSetting) {
                        NumberSetting ns = (NumberSetting) setting;
                        int sliderW = 65;
                        int sliderX = ctrlX + 5;
                        int sliderY = ctrlY + 6;

                        double pct = (ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin());
                        int handleX = sliderX + (int) (sliderW * pct);

                        // Linha do slider
                        Gui.drawRect(sliderX, sliderY, sliderX + sliderW, sliderY + 2, 0xFF444444);
                        Gui.drawRect(sliderX, sliderY, handleX, sliderY + 2, 0xFF0088FF);

                        // Bolinha / Handle do slider
                        Gui.drawRect(handleX - 3, sliderY - 3, handleX + 3, sliderY + 5, 0xFFFFFFFF);
                        Gui.drawRect(handleX - 2, sliderY - 2, handleX + 2, sliderY + 4, 0xFF0055FF);

                        // Valor numérico à direita
                        String valStr = String.format("%.1f", ns.getValue());
                        if (ns.getIncrement() == 1.0) valStr = String.valueOf(ns.getIntValue());
                        fontRendererObj.drawStringWithShadow(valStr, sliderX + sliderW + 5, ctrlY + 4, 0xFFFFFFFF);

                    } else if (setting instanceof ColorSetting) {
                        ColorSetting cs = (ColorSetting) setting;
                        int cVal = cs.getValue();

                        // Quadrado colorido
                        Gui.drawRect(ctrlX + 30, ctrlY + 2, ctrlX + 46, ctrlY + 14, cVal);
                        Gui.drawRect(ctrlX + 30, ctrlY + 2, ctrlX + 46, ctrlY + 3, 0xFFFFFFFF);

                        // Hex string e botão (+)
                        String hex = String.format("#%06X", cVal & 0xFFFFFF);
                        fontRendererObj.drawStringWithShadow("§7" + hex + " §a(+)", ctrlX + 50, ctrlY + 4, 0xFFFFFFFF);
                    }
                }

                curY += 24;
            }
        }

        // Se estiver arrastando slider
        if (draggingSlider != null) {
            int curY = contentY - scrollOffset;
            for (ModSetting<?> setting : mod.getSettings()) {
                if (setting == draggingSlider) {
                    int rowX = modalX + 12;
                    int rowW = modalW - 24;
                    int ctrlW = 100;
                    int ctrlX = rowX + rowW - ctrlW - 2;
                    int sliderW = 65;
                    int sliderX = ctrlX + 5;

                    double diff = Math.max(0, Math.min(sliderW, mouseX - sliderX));
                    double pct = diff / sliderW;
                    double newVal = draggingSlider.getMin() + (pct * (draggingSlider.getMax() - draggingSlider.getMin()));
                    draggingSlider.setValue(newVal);
                    break;
                }
                curY += 24;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int modalW = 340;
        int modalH = 260;
        int modalX = width / 2 - modalW / 2;
        int modalY = height / 2 - modalH / 2;

        // Botão Fechar (✕)
        int closeX = modalX + modalW - 22;
        int closeY = modalY + 6;
        if (mouseX >= closeX && mouseX <= closeX + 16 && mouseY >= closeY && mouseY <= closeY + 16) {
            Config.salvar();
            mc.displayGuiScreen(parent);
            return;
        }

        // Botão Mover no HUD Editor
        if (mod.isHUD()) {
            int hudX = modalX + modalW - 95;
            int hudY = modalY + 6;
            if (mouseX >= hudX && mouseX <= hudX + 65 && mouseY >= hudY && mouseY <= hudY + 16) {
                Config.salvar();
                mc.displayGuiScreen(new HUDEditorScreen());
                return;
            }
        }

        int contentY = modalY + 34;
        int curY = contentY - scrollOffset;

        for (ModSetting<?> setting : mod.getSettings()) {
            int rowX = modalX + 12;
            int rowW = modalW - 24;
            int ctrlW = 100;
            int ctrlX = rowX + rowW - ctrlW - 2;
            int ctrlY = curY + 3;

            if (mouseX >= ctrlX && mouseX <= ctrlX + ctrlW && mouseY >= curY && mouseY <= curY + 22) {
                if (setting instanceof BooleanSetting) {
                    ((BooleanSetting) setting).toggle();
                    Config.salvar();
                    return;
                } else if (setting instanceof ModeSetting) {
                    if (mouseButton == 0) {
                        ((ModeSetting) setting).cycle();
                    } else {
                        ((ModeSetting) setting).cycleBack();
                    }
                    Config.salvar();
                    return;
                } else if (setting instanceof NumberSetting) {
                    draggingSlider = (NumberSetting) setting;
                    return;
                } else if (setting instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting) setting;
                    int[] palette = {
                            0xFFFFFFFF, // Branco
                            0xFF0055FF, // Azul Forte Battle Client
                            0xFF00FFFF, // Ciano
                            0xFF55FF55, // Verde
                            0xFFFF5555, // Vermelho
                            0xFFFFAA00, // Laranja
                            0xFFFFFF55, // Amarelo
                            0xFFAA00AA  // Roxo
                    };
                    int cur = cs.getValue();
                    int next = palette[0];
                    for (int i = 0; i < palette.length; i++) {
                        if (palette[i] == cur) {
                            next = palette[(i + 1) % palette.length];
                            break;
                        }
                    }
                    cs.setValue(next);
                    Config.salvar();
                    return;
                }
            }

            curY += 24;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (draggingSlider != null) {
            draggingSlider = null;
            Config.salvar();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Config.salvar();
            mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
}
