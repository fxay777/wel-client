package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.ModSetting;
import com.battleclient.core.settings.NumberSetting;
import com.battleclient.mods.BaseMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class ModSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final BaseMod mod;

    private NumberSetting draggingSlider = null;

    public ModSettingsScreen(GuiScreen parent, BaseMod mod) {
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(999, width / 2 - 80, height - 30, 160, 20, "§fVoltar ao Menu"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 999) {
            Config.salvar();
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Cabeçalho
        Gui.drawRect(0, 0, width, 40, 0xDD000000);
        Gui.drawRect(0, 39, width, 40, 0xFF0055FF);

        drawCenteredString(fontRendererObj, "§9§lBATTLE CLIENT §8- §fCONFIGURAÇÕES", width / 2, 10, 0xFFFFFFFF);
        drawCenteredString(fontRendererObj, "§7Mod: §e" + mod.getDisplayName() + " §8(§b" + mod.getCategory() + "§8)", width / 2, 24, 0xFFFFFFFF);

        // Painel central de configurações
        int panelWidth = 320;
        int panelX = width / 2 - panelWidth / 2;
        int panelY = 50;

        if (mod.getSettings().isEmpty()) {
            drawCenteredString(fontRendererObj, "§7Este mod não possui configurações adicionais.", width / 2, height / 2 - 10, 0xFFAAAAAA);
        } else {
            int currentY = panelY;

            for (ModSetting<?> setting : mod.getSettings()) {
                // Fundo do card da setting
                Gui.drawRect(panelX, currentY, panelX + panelWidth, currentY + 32, 0xA0101010);
                Gui.drawRect(panelX, currentY, panelX + 3, currentY + 32, 0xFF0055FF);

                // Nome da setting
                fontRendererObj.drawStringWithShadow(setting.getDisplayName(), panelX + 10, currentY + 7, 0xFFFFFFFF);
                if (setting.getDescription() != null && !setting.getDescription().isEmpty()) {
                    fontRendererObj.drawStringWithShadow("§8" + setting.getDescription(), panelX + 10, currentY + 18, 0xFF888888);
                }

                // Renderiza controle interativo à direita
                int controlX = panelX + panelWidth - 90;
                int controlY = currentY + 6;

                if (setting instanceof BooleanSetting) {
                    BooleanSetting bs = (BooleanSetting) setting;
                    boolean enabled = bs.isEnabled();
                    int btnColor = enabled ? 0xFF0055FF : 0xFF333333;
                    Gui.drawRect(controlX, controlY, controlX + 80, controlY + 20, btnColor);
                    Gui.drawRect(controlX, controlY, controlX + 80, controlY + 1, 0xFF555555);
                    drawCenteredString(fontRendererObj, enabled ? "§a§lON" : "§cOFF", controlX + 40, controlY + 6, 0xFFFFFFFF);

                } else if (setting instanceof ModeSetting) {
                    ModeSetting ms = (ModeSetting) setting;
                    Gui.drawRect(controlX - 20, controlY, controlX + 80, controlY + 20, 0xFF222222);
                    Gui.drawRect(controlX - 20, controlY, controlX + 80, controlY + 1, 0xFF0055FF);
                    drawCenteredString(fontRendererObj, "§b" + ms.getValue(), controlX + 30, controlY + 6, 0xFFFFFFFF);

                } else if (setting instanceof NumberSetting) {
                    NumberSetting ns = (NumberSetting) setting;
                    int sliderWidth = 80;
                    double percent = (ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin());
                    int fillWidth = (int) (sliderWidth * percent);

                    Gui.drawRect(controlX, controlY, controlX + sliderWidth, controlY + 20, 0xFF222222);
                    Gui.drawRect(controlX, controlY, controlX + fillWidth, controlY + 20, 0xFF0055FF);
                    Gui.drawRect(controlX, controlY, controlX + sliderWidth, controlY + 1, 0xFF444444);

                    String valStr = String.format("%.1f", ns.getValue());
                    if (ns.getIncrement() == 1.0) {
                        valStr = String.valueOf(ns.getIntValue());
                    }
                    drawCenteredString(fontRendererObj, valStr, controlX + sliderWidth / 2, controlY + 6, 0xFFFFFFFF);

                } else if (setting instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting) setting;
                    Gui.drawRect(controlX + 40, controlY + 2, controlX + 76, controlY + 18, cs.getValue());
                    Gui.drawRect(controlX + 40, controlY + 2, controlX + 76, controlY + 3, 0xFFFFFFFF);
                    fontRendererObj.drawStringWithShadow("Cor", controlX + 15, controlY + 6, 0xFFDDDDDD);
                }

                currentY += 36;
            }
        }

        // Lógica de slider dragging
        if (draggingSlider != null) {
            int currentY = panelY;
            for (ModSetting<?> setting : mod.getSettings()) {
                if (setting == draggingSlider) {
                    int controlX = panelX + panelWidth - 90;
                    int sliderWidth = 80;
                    double diff = Math.max(0, Math.min(sliderWidth, mouseX - controlX));
                    double percent = diff / sliderWidth;
                    double newVal = draggingSlider.getMin() + (percent * (draggingSlider.getMax() - draggingSlider.getMin()));
                    draggingSlider.setValue(newVal);
                    break;
                }
                currentY += 36;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int panelWidth = 320;
        int panelX = width / 2 - panelWidth / 2;
        int panelY = 50;
        int currentY = panelY;

        for (ModSetting<?> setting : mod.getSettings()) {
            int controlX = panelX + panelWidth - 90;
            int controlY = currentY + 6;

            if (setting instanceof BooleanSetting) {
                if (mouseX >= controlX && mouseX <= controlX + 80 && mouseY >= controlY && mouseY <= controlY + 20) {
                    ((BooleanSetting) setting).toggle();
                    Config.salvar();
                    return;
                }
            } else if (setting instanceof ModeSetting) {
                if (mouseX >= controlX - 20 && mouseX <= controlX + 80 && mouseY >= controlY && mouseY <= controlY + 20) {
                    if (mouseButton == 0) {
                        ((ModeSetting) setting).cycle();
                    } else if (mouseButton == 1) {
                        ((ModeSetting) setting).cycleBack();
                    }
                    Config.salvar();
                    return;
                }
            } else if (setting instanceof NumberSetting) {
                if (mouseX >= controlX && mouseX <= controlX + 80 && mouseY >= controlY && mouseY <= controlY + 20) {
                    draggingSlider = (NumberSetting) setting;
                    return;
                }
            } else if (setting instanceof ColorSetting) {
                if (mouseX >= controlX + 40 && mouseX <= controlX + 76 && mouseY >= controlY + 2 && mouseY <= controlY + 18) {
                    ColorSetting cs = (ColorSetting) setting;
                    // Alterna paletas predefinidas de cores PVP populares
                    int[] palette = {
                            0xFF0055FF, // Azul Forte Battle Client
                            0xFF00FFFF, // Ciano
                            0xFF00FF00, // Verde Neon
                            0xFFFF0055, // Rosa/Vermelho
                            0xFFFFAA00, // Laranja
                            0xFFFFFF00, // Amarelo
                            0xFFFFFFFF, // Branco
                            0xFFAA00FF  // Roxo
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

            currentY += 36;
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
}
