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

/**
 * ModSettingsScreen - Estilo Badlion / Battle Client
 *
 * Layout:
 * ┌──────────────────────────────────────────┐
 * │  ◀ Back  MOD NAME OPTIONS          [HUD] │  <- Header 32px vermelho
 * ├──────────────────────────────────────────┤
 * │  Setting Name              < VALUE >     │  <- Linha 26px alternada
 * │  Setting Name              ████──── 3.0  │
 * │  Setting Name              ■ #FF0000 (+) │
 * └──────────────────────────────────────────┘
 */
public class ModSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final BaseMod mod;

    private int scrollOffset = 0;
    private NumberSetting draggingSlider = null;

    // Cores (mesmas do ModMenuScreen)
    private static final int C_BG      = 0xFF0D0D0D;
    private static final int C_HEADER  = 0xFF0D0D0D;
    private static final int C_ACCENT  = 0xFFCC1111;
    private static final int C_ACCENT2 = 0xFF880000;
    private static final int C_BORDER  = 0xFF282828;
    private static final int C_TEXT    = 0xFFEEEEEE;
    private static final int C_DIM     = 0xFF777777;
    private static final int C_ROW_ALT = 0xFF131313;

    private static final int MODAL_W = 380;
    private static final int MODAL_H = 300;
    private static final int HEADER_H = 34;

    public ModSettingsScreen(GuiScreen parent, BaseMod mod) {
        this.parent = parent;
        this.mod    = mod;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dw = Mouse.getEventDWheel();
        if (dw > 0) scrollOffset = Math.max(0, scrollOffset - 22);
        else if (dw < 0) scrollOffset += 22;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Overlay escuro
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);

        int mx = width  / 2 - MODAL_W / 2;
        int my = height / 2 - MODAL_H / 2;

        // ── Fundo principal ───────────────────────────────────────────────
        Gui.drawRect(mx, my, mx + MODAL_W, my + MODAL_H, C_BG);
        RenderUtil.drawBorder(mx, my, MODAL_W, MODAL_H, 1, C_BORDER);

        // ── Header ────────────────────────────────────────────────────────
        Gui.drawRect(mx, my, mx + MODAL_W, my + HEADER_H, C_HEADER);
        Gui.drawRect(mx, my + HEADER_H - 1, mx + MODAL_W, my + HEADER_H, C_ACCENT);

        // Botao Voltar
        int backX = mx + 8;
        int backY = my + 9;
        boolean backHov = isHov(mouseX, mouseY, backX, backY, 40, 14);
        Gui.drawRect(backX, backY, backX + 40, backY + 14, backHov ? C_ACCENT2 : 0xFF1A1A1A);
        RenderUtil.drawBorder(backX, backY, 40, 14, 1, backHov ? C_ACCENT : C_BORDER);
        drawCenteredString(fontRendererObj, "\u00A77\u25C4 Back", backX + 20, backY + 3, 0xFFFFFFFF);

        // Titulo
        String title = mod.getDisplayName().toUpperCase();
        fontRendererObj.drawStringWithShadow("\u00A7c\u00A7l" + title + "\u00A7r \u00A78OPTIONS", mx + 58, my + 12, C_TEXT);

        // Botao HUD Editor (apenas para mods HUD)
        if (mod.isHUD()) {
            int hudX = mx + MODAL_W - 75;
            int hudY = my + 9;
            boolean hudHov = isHov(mouseX, mouseY, hudX, hudY, 66, 14);
            Gui.drawRect(hudX, hudY, hudX + 66, hudY + 14, hudHov ? C_ACCENT : 0xFF1A1A1A);
            RenderUtil.drawBorder(hudX, hudY, 66, 14, 1, hudHov ? C_ACCENT : C_BORDER);
            drawCenteredString(fontRendererObj, "\u00A7f[EDITAR]", hudX + 33, hudY + 3, 0xFFFFFFFF);
        }

        // Botao Fechar
        int closeX = mx + MODAL_W - 18;
        int closeY = my + 10;
        boolean closeHov = isHov(mouseX, mouseY, closeX, closeY, 12, 12);
        Gui.drawRect(closeX, closeY, closeX + 12, closeY + 12, closeHov ? 0xFFAA0000 : 0xFF1A1A1A);
        drawCenteredString(fontRendererObj, "\u00A77\u00D7", closeX + 6, closeY + 2, 0xFFFFFFFF);

        // ── Lista de Settings ──────────────────────────────────────────────
        int contentY = my + HEADER_H;
        int maxY     = my + MODAL_H - 4;

        if (mod.getSettings().isEmpty()) {
            drawCenteredString(fontRendererObj, "\u00A78Nenhuma configuracao disponivel.", width / 2, height / 2, C_DIM);
        } else {
            int rowH  = 26;
            int curY  = contentY - scrollOffset;
            int rowIdx = 0;

            for (ModSetting<?> setting : mod.getSettings()) {
                if (curY + rowH > contentY && curY < maxY) {
                    int rowX = mx + 1;
                    int rowW = MODAL_W - 2;

                    // Fundo alternado (zebra)
                    int rowBg = (rowIdx % 2 == 0) ? C_BG : C_ROW_ALT;
                    Gui.drawRect(rowX, curY, rowX + rowW, curY + rowH, rowBg);

                    // Hover
                    if (isHov(mouseX, mouseY, rowX, curY, rowW, rowH)) {
                        Gui.drawRect(rowX, curY, rowX + rowW, curY + rowH, 0x14FFFFFF);
                    }

                    // Indicador lateral colorido por tipo
                    int typeColor = getTypeColor(setting);
                    Gui.drawRect(rowX, curY, rowX + 2, curY + rowH, typeColor);

                    // Nome da setting
                    fontRendererObj.drawStringWithShadow("\u00A77" + setting.getDisplayName(),
                            rowX + 8, curY + 9, C_TEXT);

                    // Controle à direita
                    int ctrlW = 110;
                    int ctrlX = rowX + rowW - ctrlW - 4;
                    int ctrlY = curY + 4;

                    renderControl(setting, ctrlX, ctrlY, ctrlW, rowH - 8, mouseX, mouseY);
                }
                curY += rowH;
                rowIdx++;
            }
        }

        // Linha inferior
        Gui.drawRect(mx, my + MODAL_H - 18, mx + MODAL_W, my + MODAL_H - 17, C_BORDER);
        fontRendererObj.drawString("\u00A78" + mod.getSettings().size() + " opcoes",
                mx + 8, my + MODAL_H - 12, C_DIM);

        // Drag slider ativo
        if (draggingSlider != null) {
            int curY = (my + HEADER_H) - scrollOffset;
            for (ModSetting<?> setting : mod.getSettings()) {
                if (setting == draggingSlider) {
                    int rowX  = mx + 1;
                    int rowW  = MODAL_W - 2;
                    int ctrlW = 110;
                    int ctrlX = rowX + rowW - ctrlW - 4;
                    int sliderW = 65;
                    int sliderX = ctrlX + 4;
                    double diff = Math.max(0, Math.min(sliderW, mouseX - sliderX));
                    double pct  = diff / sliderW;
                    double newVal = draggingSlider.getMin() + pct * (draggingSlider.getMax() - draggingSlider.getMin());
                    draggingSlider.setValue(newVal);
                    break;
                }
                curY += 26;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int getTypeColor(ModSetting<?> s) {
        if (s instanceof BooleanSetting) return 0xFF0055AA;
        if (s instanceof ModeSetting)   return 0xFF885500;
        if (s instanceof NumberSetting) return 0xFF005533;
        if (s instanceof ColorSetting)  return 0xFF550055;
        return C_BORDER;
    }

    private void renderControl(ModSetting<?> setting, int cx, int cy, int cw, int ch, int mouseX, int mouseY) {
        if (setting instanceof BooleanSetting) {
            BooleanSetting bs = (BooleanSetting) setting;
            boolean on = bs.isEnabled();
            // Seletor estilo moderno < ON >
            String label = on
                ? "\u00A78< \u00A7a\u00A7lON  \u00A78>"
                : "\u00A78< \u00A7c\u00A7lOFF \u00A78>";
            drawCenteredString(fontRendererObj, label, cx + cw / 2, cy + ch / 2 - 3, 0xFFFFFFFF);

        } else if (setting instanceof ModeSetting) {
            ModeSetting ms = (ModeSetting) setting;
            String val = ms.getValue();
            if (val.length() > 12) val = val.substring(0, 10) + "..";
            drawCenteredString(fontRendererObj, "\u00A78< \u00A7e" + val + " \u00A78>", cx + cw / 2, cy + ch / 2 - 3, 0xFFFFFFFF);

        } else if (setting instanceof NumberSetting) {
            NumberSetting ns = (NumberSetting) setting;
            int sliderW = 65;
            int sliderX = cx + 4;
            int sliderY = cy + ch / 2;

            double pct = (ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin());
            int handleX = sliderX + (int) (sliderW * pct);

            // Track
            Gui.drawRect(sliderX, sliderY, sliderX + sliderW, sliderY + 2, 0xFF333333);
            Gui.drawRect(sliderX, sliderY, handleX, sliderY + 2, C_ACCENT);
            // Handle
            Gui.drawRect(handleX - 3, sliderY - 3, handleX + 3, sliderY + 5, 0xFFFFFFFF);
            Gui.drawRect(handleX - 2, sliderY - 2, handleX + 2, sliderY + 4, C_ACCENT);

            // Valor
            String valStr = ns.getIncrement() == 1.0 ? String.valueOf(ns.getIntValue()) : String.format("%.1f", ns.getValue());
            fontRendererObj.drawStringWithShadow(valStr, sliderX + sliderW + 5, sliderY - 3, 0xFFFFFFFF);

        } else if (setting instanceof ColorSetting) {
            ColorSetting cs = (ColorSetting) setting;
            int cVal = cs.getValue();

            // Quadrado de cor
            Gui.drawRect(cx + 10, cy + 2, cx + 26, cy + ch - 2, cVal);
            RenderUtil.drawBorder(cx + 10, cy + 2, 16, ch - 4, 1, 0xFFFFFFFF);

            // Hex + botao
            String hex = String.format("#%06X", cVal & 0xFFFFFF);
            fontRendererObj.drawString("\u00A77" + hex, cx + 30, cy + ch / 2 - 3, 0xFFCCCCCC);
            fontRendererObj.drawString("\u00A7a(+)", cx + 30 + fontRendererObj.getStringWidth(hex) + 4, cy + ch / 2 - 3, 0xFFFFFFFF);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int mx = width  / 2 - MODAL_W / 2;
        int my = height / 2 - MODAL_H / 2;

        // Voltar
        int backX = mx + 8;
        int backY = my + 9;
        if (isHov(mouseX, mouseY, backX, backY, 40, 14)) {
            Config.salvar();
            mc.displayGuiScreen(parent);
            return;
        }

        // Fechar
        int closeX = mx + MODAL_W - 18;
        int closeY = my + 10;
        if (isHov(mouseX, mouseY, closeX, closeY, 12, 12)) {
            Config.salvar();
            mc.displayGuiScreen(parent);
            return;
        }

        // HUD Editor
        if (mod.isHUD()) {
            int hudX = mx + MODAL_W - 75;
            int hudY = my + 9;
            if (isHov(mouseX, mouseY, hudX, hudY, 66, 14)) {
                Config.salvar();
                mc.displayGuiScreen(new HUDEditorScreen());
                return;
            }
        }

        // Settings
        int contentY = my + HEADER_H;
        int curY = contentY - scrollOffset;
        int rowH = 26;

        for (ModSetting<?> setting : mod.getSettings()) {
            int rowX  = mx + 1;
            int rowW  = MODAL_W - 2;
            int ctrlW = 110;
            int ctrlX = rowX + rowW - ctrlW - 4;

            if (isHov(mouseX, mouseY, ctrlX, curY + 4, ctrlW, rowH - 8)) {
                if (setting instanceof BooleanSetting) {
                    ((BooleanSetting) setting).toggle();
                    Config.salvar();
                } else if (setting instanceof ModeSetting) {
                    if (mouseButton == 0) ((ModeSetting) setting).cycle();
                    else                 ((ModeSetting) setting).cycleBack();
                    Config.salvar();
                } else if (setting instanceof NumberSetting) {
                    draggingSlider = (NumberSetting) setting;
                } else if (setting instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting) setting;
                    int[] palette = {
                        0xFFFFFFFF, 0xFFCC1111, 0xFF0055FF, 0xFF00FFFF,
                        0xFF55FF55, 0xFFFFFF55, 0xFFFFAA00, 0xFFAA00AA
                    };
                    int cur = cs.getValue();
                    int next = palette[0];
                    for (int i = 0; i < palette.length; i++) {
                        if (palette[i] == cur) { next = palette[(i + 1) % palette.length]; break; }
                    }
                    cs.setValue(next);
                    Config.salvar();
                }
                return;
            }
            curY += rowH;
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

    private boolean isHov(int mx, int my, int rx, int ry, int rw, int rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
