package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.ModManager;
import com.battleclient.mods.BaseMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ModMenuScreen - Estilo Battle Client (imagem de referencia)
 *
 * Layout:
 *  ┌────────────┬──────────────────────────────────────────────────────────┐
 *  │  SIDEBAR   │  TOPBAR: < Mods   [Sort: Popular v]  [Search mods...]   │
 *  │  (160px)   ├──────────────────────────────────────────────────────────┤
 *  │  [Logo]    │                                                          │
 *  │  Home      │  [Card][Card][Card][Card]                                │
 *  │  Friends   │  [Card][Card][Card][Card]                                │
 *  │  Cosmetics │  [Card][Card][Card][Card]                                │
 *  │  Store     │  [Card][Card][Card][Card]                                │
 *  │ >Mods<     │                                                          │
 *  │  Gallery   │                                                          │
 *  │            │                                                          │
 *  │  Settings  │                                                          │
 *  └────────────┴──────────────────────────────────────────────────────────┘
 */
public class ModMenuScreen extends GuiScreen {

    private static final ResourceLocation LOGO_TEX = new ResourceLocation("battleclient", "textures/gui/logo.png");

    // ── Paleta de cores (dark theme igual imagem) ────────────────────────────
    private static final int BG_MAIN      = 0xFF141414; // fundo geral
    private static final int SIDEBAR_BG   = 0xFF0F0F0F; // sidebar esquerda
    private static final int SIDEBAR_ACT  = 0xFF1F1F1F; // item ativo sidebar
    private static final int TOPBAR_BG    = 0xFF141414; // topbar area
    private static final int CARD_BG      = 0xFF1E1E1E; // fundo card mod
    private static final int CARD_HOVER   = 0xFF252525; // hover card
    private static final int CARD_BOTTOM  = 0xFF181818; // base do card (nome)
    private static final int DIVIDER      = 0xFF2A2A2A; // linhas divisoras
    private static final int RED_ACCENT   = 0xFFE53935; // vermelho (logo + ativo)
    private static final int TEXT_WHITE   = 0xFFEEEEEE;
    private static final int TEXT_GRAY    = 0xFF888888;
    private static final int TEXT_DIM     = 0xFF555555;
    private static final int SEARCH_BG    = 0xFF1A1A1A;
    private static final int SEARCH_BOR   = 0xFF333333;
    private static final int SORT_BG      = 0xFF1A1A1A;
    private static final int SORT_BOR     = 0xFF333333;
    private static final int HEART_OFF    = 0xFF444444;
    private static final int HEART_ON     = 0xFFE53935;
    private static final int ENABLED_IND  = 0xFF2ECC71; // bolinha verde = habilitado
    private static final int DISABLED_IND = 0xFF444444; // bolinha cinza = desabilitado

    // ── Dimensoes ────────────────────────────────────────────────────────────
    private static final int SIDEBAR_W  = 160;
    private static final int TOPBAR_H   = 56;
    private static final int CARD_W     = 182;
    private static final int CARD_H     = 142; // altura total do card
    private static final int CARD_IMG_H = 108; // area de preview
    private static final int CARD_BOT_H = 34;  // area do nome
    private static final int CARD_GAP   = 10;
    private static final int GRID_PAD   = 14;

    // Nav items
    private static final String[] NAV_ITEMS = {"Home", "Friends", "Cosmetics", "Store", "Mods", "Gallery"};
    private static final int NAV_ACTIVE = 4; // "Mods"

    // Sort options
    private static final String[] SORT_OPTIONS = {"Popular", "A-Z", "Z-A", "Enabled", "Disabled"};
    private int sortIndex = 0;
    private boolean sortDropOpen = false;

    // State
    private GuiTextField searchField;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private List<Integer> favorites = new ArrayList<Integer>();
    private int hoveredCard = -1;

    // Calculated layout (set in initGui)
    private int winX, winY, winW, winH;
    private int contentX, contentY, contentW, contentH;
    private int cols = 4;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        // Fullscreen window
        winX = 0; winY = 0; winW = width; winH = height;
        contentX = winX + SIDEBAR_W;
        contentY = winY + TOPBAR_H;
        contentW = winW - SIDEBAR_W;
        contentH = winH - TOPBAR_H;

        // How many columns fit
        cols = Math.max(1, (contentW - GRID_PAD * 2 + CARD_GAP) / (CARD_W + CARD_GAP));

        // Search field
        int sfX = contentX + contentW - 210;
        int sfY = winY + TOPBAR_H / 2 - 8;
        searchField = new GuiTextField(0, fontRendererObj, sfX + 20, sfY + 2, 170, 14);
        searchField.setMaxStringLength(40);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setFocused(false);
        searchField.setTextColor(0xFFCCCCCC);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.salvar();
    }

    @Override
    public void updateScreen() {
        if (searchField != null) searchField.updateCursorCounter();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0 && !sortDropOpen) {
            scrollOffset += (dWheel > 0 ? -30 : 30);
            scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, maxScroll)));
        }
    }

    // ── DRAW ─────────────────────────────────────────────────────────────────
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Fundo geral
        Gui.drawRect(winX, winY, winX + winW, winY + winH, BG_MAIN);

        drawSidebar(mouseX, mouseY);
        drawTopBar(mouseX, mouseY);
        drawModGrid(mouseX, mouseY);

        // Dropdown por cima de tudo
        if (sortDropOpen) drawSortDropdown(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────────────
    private void drawSidebar(int mouseX, int mouseY) {
        // Fundo da sidebar
        Gui.drawRect(winX, winY, winX + SIDEBAR_W, winY + winH, SIDEBAR_BG);
        // Borda direita
        Gui.drawRect(winX + SIDEBAR_W - 1, winY, winX + SIDEBAR_W, winY + winH, DIVIDER);

        // Logo area (topo)
        int logoAreaH = 80;
        Gui.drawRect(winX, winY, winX + SIDEBAR_W, winY + logoAreaH, 0xFF0A0A0A);
        Gui.drawRect(winX, winY + logoAreaH - 1, winX + SIDEBAR_W, winY + logoAreaH, DIVIDER);

        // Logo icone (escudo vermelho simulado)
        int iconX = winX + 16;
        int iconY = winY + 16;
        drawShieldIcon(iconX, iconY, 28);

        // Texto "BATTLE / CLIENT"
        fontRendererObj.drawStringWithShadow("\u00A7fBATTLE", iconX + 36, winY + 22, TEXT_WHITE);
        fontRendererObj.drawStringWithShadow("\u00A7fCLIENT", iconX + 36, winY + 34, TEXT_WHITE);

        // Nav items
        int navStartY = winY + logoAreaH + 8;
        for (int i = 0; i < NAV_ITEMS.length; i++) {
            String item = NAV_ITEMS[i];
            int iy = navStartY + i * 44;
            boolean active = (i == NAV_ACTIVE);
            boolean hov = mouseX >= winX && mouseX <= winX + SIDEBAR_W - 1
                       && mouseY >= iy && mouseY <= iy + 40;

            // Fundo do item
            if (active) {
                Gui.drawRect(winX, iy, winX + SIDEBAR_W - 1, iy + 40, SIDEBAR_ACT);
                // Barra vermelha esquerda
                Gui.drawRect(winX, iy, winX + 3, iy + 40, RED_ACCENT);
            } else if (hov) {
                Gui.drawRect(winX, iy, winX + SIDEBAR_W - 1, iy + 40, 0xFF181818);
            }

            // Icone do nav (letra simulando icone)
            String icon = getNavIcon(i);
            int iconColor = active ? TEXT_WHITE : (hov ? 0xFFBBBBBB : TEXT_GRAY);
            fontRendererObj.drawStringWithShadow(icon, winX + 18, iy + 9, iconColor);

            // Label
            int txtColor = active ? TEXT_WHITE : (hov ? 0xFFBBBBBB : TEXT_GRAY);
            fontRendererObj.drawStringWithShadow(active ? "\u00A7f" + item : "\u00A77" + item,
                    winX + 36, iy + 9, txtColor);

            // Separador inferior
            if (i < NAV_ITEMS.length - 1) {
                Gui.drawRect(winX + 12, iy + 40, winX + SIDEBAR_W - 12, iy + 41, 0xFF1E1E1E);
            }
        }

        // Settings no fundo
        int settY = winY + winH - 48;
        Gui.drawRect(winX, settY, winX + SIDEBAR_W - 1, settY + 1, DIVIDER);
        boolean settHov = mouseX >= winX && mouseX <= winX + SIDEBAR_W - 1
                       && mouseY >= settY && mouseY <= settY + 48;
        if (settHov) Gui.drawRect(winX, settY, winX + SIDEBAR_W - 1, settY + 48, 0xFF181818);
        fontRendererObj.drawString("\u00A77\u2699", winX + 18, settY + 16, settHov ? TEXT_WHITE : TEXT_GRAY);
        fontRendererObj.drawString(settHov ? "\u00A7fSettings" : "\u00A77Settings",
                winX + 36, settY + 16, settHov ? TEXT_WHITE : TEXT_GRAY);
    }

    private String getNavIcon(int i) {
        switch (i) {
            case 0: return "\u00A77\u2302"; // home
            case 1: return "\u00A77\u263A"; // friends
            case 2: return "\u00A77\u25C6"; // cosmetics
            case 3: return "\u00A77\u25A0"; // store
            case 4: return "\u00A7c\u2605"; // mods (star red)
            case 5: return "\u00A77\u25A3"; // gallery
            default: return "\u00A77\u25CF";
        }
    }

    // ── TOPBAR ───────────────────────────────────────────────────────────────
    private void drawTopBar(int mouseX, int mouseY) {
        // Fundo topbar (apenas area de conteudo, nao sobre sidebar)
        Gui.drawRect(contentX, winY, contentX + contentW, winY + TOPBAR_H, TOPBAR_BG);
        // Borda inferior
        Gui.drawRect(contentX, winY + TOPBAR_H - 1, contentX + contentW, winY + TOPBAR_H, DIVIDER);

        int midY = winY + TOPBAR_H / 2;

        // Seta voltar
        fontRendererObj.drawStringWithShadow("\u00A77<", contentX + 14, midY - 4, TEXT_GRAY);

        // Titulo "Mods"
        fontRendererObj.drawStringWithShadow("\u00A7fMods", contentX + 28, midY - 4, TEXT_WHITE);

        // Sort dropdown
        int sortX = contentX + contentW - 430;
        int sortY = winY + TOPBAR_H / 2 - 11;
        int sortW = 140;
        int sortH = 22;
        boolean sortHov = mouseX >= sortX && mouseX <= sortX + sortW
                       && mouseY >= sortY && mouseY <= sortY + sortH;

        Gui.drawRect(sortX, sortY, sortX + sortW, sortY + sortH, sortHov || sortDropOpen ? 0xFF252525 : SORT_BG);
        // borda
        Gui.drawRect(sortX, sortY, sortX + sortW, sortY + 1, SORT_BOR);
        Gui.drawRect(sortX, sortY + sortH - 1, sortX + sortW, sortY + sortH, SORT_BOR);
        Gui.drawRect(sortX, sortY, sortX + 1, sortY + sortH, SORT_BOR);
        Gui.drawRect(sortX + sortW - 1, sortY, sortX + sortW, sortY + sortH, SORT_BOR);
        // icone setas
        fontRendererObj.drawString("\u00A77\u21C5", sortX + 6, sortY + 7, TEXT_GRAY);
        fontRendererObj.drawString("\u00A77Sort: \u00A7f" + SORT_OPTIONS[sortIndex], sortX + 18, sortY + 7, TEXT_GRAY);
        // seta dropdown
        fontRendererObj.drawString("\u00A77v", sortX + sortW - 12, sortY + 7, TEXT_GRAY);

        // Search box
        int sfX = contentX + contentW - 215;
        int sfY = winY + TOPBAR_H / 2 - 12;
        int sfW = 200;
        int sfH = 24;
        boolean sfHov = mouseX >= sfX && mouseX <= sfX + sfW
                     && mouseY >= sfY && mouseY <= sfY + sfH;

        Gui.drawRect(sfX, sfY, sfX + sfW, sfY + sfH, sfHov ? 0xFF1E1E1E : SEARCH_BG);
        Gui.drawRect(sfX, sfY, sfX + sfW, sfY + 1, SEARCH_BOR);
        Gui.drawRect(sfX, sfY + sfH - 1, sfX + sfW, sfY + sfH, SEARCH_BOR);
        Gui.drawRect(sfX, sfY, sfX + 1, sfY + sfH, SEARCH_BOR);
        Gui.drawRect(sfX + sfW - 1, sfY, sfX + sfW, sfY + sfH, SEARCH_BOR);

        // Icone lupa
        fontRendererObj.drawString("\u00A77O", sfX + 6, sfY + 7, TEXT_GRAY);

        if (searchField != null) {
            searchField.xPosition = sfX + 20;
            searchField.yPosition = sfY + 6;
            searchField.width = sfW - 24;
            searchField.drawTextBox();
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                fontRendererObj.drawString("\u00A78Search mods...", sfX + 22, sfY + 7, TEXT_DIM);
            }
        }
    }

    private void drawSortDropdown(int mouseX, int mouseY) {
        int sortX = contentX + contentW - 430;
        int sortY = winY + TOPBAR_H / 2 - 11 + 23; // abaixo do botao
        int sortW = 140;
        int optH = 20;

        // Sombra
        Gui.drawRect(sortX + 2, sortY + 2, sortX + sortW + 2, sortY + SORT_OPTIONS.length * optH + 2, 0x66000000);
        // Fundo
        Gui.drawRect(sortX, sortY, sortX + sortW, sortY + SORT_OPTIONS.length * optH, 0xFF1C1C1C);
        Gui.drawRect(sortX, sortY, sortX + sortW, sortY + 1, SORT_BOR);
        Gui.drawRect(sortX, sortY + SORT_OPTIONS.length * optH - 1, sortX + sortW, sortY + SORT_OPTIONS.length * optH, SORT_BOR);
        Gui.drawRect(sortX, sortY, sortX + 1, sortY + SORT_OPTIONS.length * optH, SORT_BOR);
        Gui.drawRect(sortX + sortW - 1, sortY, sortX + sortW, sortY + SORT_OPTIONS.length * optH, SORT_BOR);

        for (int i = 0; i < SORT_OPTIONS.length; i++) {
            int oy = sortY + i * optH;
            boolean hov = mouseX >= sortX && mouseX <= sortX + sortW
                       && mouseY >= oy && mouseY <= oy + optH;
            boolean selected = (i == sortIndex);
            if (hov || selected) {
                Gui.drawRect(sortX + 1, oy, sortX + sortW - 1, oy + optH, selected ? 0xFF252525 : 0xFF202020);
            }
            if (i > 0) Gui.drawRect(sortX + 8, oy, sortX + sortW - 8, oy + 1, 0xFF222222);
            int tc = selected ? TEXT_WHITE : (hov ? 0xFFCCCCCC : TEXT_GRAY);
            if (selected) fontRendererObj.drawString("\u00A7f> ", sortX + 6, oy + 6, RED_ACCENT);
            fontRendererObj.drawString(SORT_OPTIONS[i], sortX + (selected ? 20 : 10), oy + 6, tc);
        }
    }

    // ── GRID DE CARDS ────────────────────────────────────────────────────────
    private void drawModGrid(int mouseX, int mouseY) {
        List<BaseMod> mods = getFilteredMods();
        int rows = (int) Math.ceil((double) mods.size() / cols);
        int totalH = rows * (CARD_H + CARD_GAP) + GRID_PAD * 2;
        maxScroll = Math.max(0, totalH - contentH);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        hoveredCard = -1;

        enableScissor(contentX, contentY, contentW, contentH);

        // Calcular largura real dos cards para preencher a area
        int totalGap = CARD_GAP * (cols - 1) + GRID_PAD * 2;
        int realCardW = (contentW - totalGap) / cols;
        int realCardH = CARD_H;

        for (int i = 0; i < mods.size(); i++) {
            BaseMod mod = mods.get(i);
            int col = i % cols;
            int row = i / cols;

            int cx = contentX + GRID_PAD + col * (realCardW + CARD_GAP);
            int cy = contentY + GRID_PAD + row * (realCardH + CARD_GAP) - scrollOffset;

            if (cy + realCardH < contentY || cy > contentY + contentH) continue;

            boolean hov = mouseX >= cx && mouseX <= cx + realCardW
                       && mouseY >= cy && mouseY <= cy + realCardH;
            if (hov) hoveredCard = i;

            drawCard(cx, cy, realCardW, realCardH, mod, i, hov, mouseX, mouseY);
        }

        disableScissor();

        // Scrollbar
        if (maxScroll > 0) {
            int sbX = winX + winW - 5;
            int sbH = contentH;
            int sbY = contentY;
            Gui.drawRect(sbX, sbY, sbX + 4, sbY + sbH, 0xFF1A1A1A);
            int thumbH = Math.max(24, sbH * contentH / (contentH + maxScroll));
            int thumbY = sbY + (int)((long) scrollOffset * (sbH - thumbH) / maxScroll);
            Gui.drawRect(sbX + 1, thumbY, sbX + 3, thumbY + thumbH, 0xFF555555);
        }

        // Sem resultados
        if (mods.isEmpty()) {
            drawCenteredString(fontRendererObj, "\u00A78Nenhum mod encontrado",
                    contentX + contentW / 2, contentY + contentH / 2 - 4, TEXT_DIM);
        }
    }

    private void drawCard(int cx, int cy, int cw, int ch, BaseMod mod, int index, boolean hov, int mouseX, int mouseY) {
        boolean enabled = mod.isEnabled();
        boolean fav = favorites.contains(index);

        // Sombra do card
        Gui.drawRect(cx + 2, cy + 2, cx + cw + 2, cy + ch + 2, 0x44000000);

        // Area de preview (topo do card)
        int imgH = ch - CARD_BOT_H;
        int imgBg = getCardPreviewColor(index);
        Gui.drawRect(cx, cy, cx + cw, cy + imgH, hov ? blendColor(imgBg, 0xFF222222, 0.15f) : imgBg);

        // Efeito de preview - simula screenshot com gradiente escuro nas bordas
        drawCardPreviewContent(cx, cy, cw, imgH, mod, index);

        // Overlay escuro gradiente na base da preview (igual ao da imagem)
        for (int g = 0; g < 18; g++) {
            int alpha = (int)(g * 8.5f);
            Gui.drawRect(cx, cy + imgH - 18 + g, cx + cw, cy + imgH - 17 + g, (alpha << 24));
        }

        // Indicador de status (bolinha pequena canto superior esquerdo)
        int dotColor = enabled ? ENABLED_IND : DISABLED_IND;
        Gui.drawRect(cx + 7, cy + 7, cx + 13, cy + 13, dotColor);
        Gui.drawRect(cx + 6, cy + 8, cx + 14, cy + 12, dotColor);
        Gui.drawRect(cx + 8, cy + 6, cx + 12, cy + 14, dotColor);

        // Base do card (nome + coração)
        Gui.drawRect(cx, cy + imgH, cx + cw, cy + ch, CARD_BOTTOM);
        // Borda superior da base
        Gui.drawRect(cx, cy + imgH, cx + cw, cy + imgH + 1, 0xFF2A2A2A);

        // Nome do mod
        String name = mod.getDisplayName();
        if (fontRendererObj.getStringWidth(name) > cw - 30) {
            name = fontRendererObj.trimStringToWidth(name, cw - 32) + "..";
        }
        fontRendererObj.drawStringWithShadow("\u00A7f" + name, cx + 8, cy + imgH + 10, TEXT_WHITE);

        // Botao coracao (favorito)
        int heartX = cx + cw - 22;
        int heartY = cy + imgH + 8;
        boolean heartHov = mouseX >= heartX && mouseX <= heartX + 16
                        && mouseY >= heartY && mouseY <= heartY + 16;
        int heartCol = fav ? HEART_ON : (heartHov ? 0xFF888888 : HEART_OFF);
        // Coração desenhado com retangulos
        drawHeart(heartX, heartY, heartCol);

        // Borda do card (sutil)
        drawCardBorder(cx, cy, cw, ch, hov, enabled);

        // Tooltip no hover
        if (hov) {
            String desc = mod.getDescription();
            if (desc != null && !desc.isEmpty()) {
                int ttX = cx;
                int ttY = cy - 18;
                if (ttY < contentY) ttY = cy + ch + 2;
                int ttW = Math.min(fontRendererObj.getStringWidth(desc) + 10, 180);
                Gui.drawRect(ttX, ttY, ttX + ttW, ttY + 15, 0xDD1A1A1A);
                Gui.drawRect(ttX, ttY, ttX + ttW, ttY + 1, 0xFF333333);
                fontRendererObj.drawString("\u00A78" + (desc.length() > 28 ? desc.substring(0, 26) + ".." : desc),
                        ttX + 5, ttY + 4, TEXT_GRAY);
            }
        }
    }

    /** Conteudo de preview simulado (imitando screenshot do Minecraft) */
    private void drawCardPreviewContent(int cx, int cy, int cw, int imgH, BaseMod mod, int index) {
        // Cor de fundo do preview baseado no indice (variedade visual)
        int[] skyColors = {
            0xFF1A4A7A, 0xFF0D3A5C, 0xFF1E3A5F, 0xFF142B45,
            0xFF1A2E4A, 0xFF0F2840, 0xFF1E4060, 0xFF0A2235
        };
        int[] groundColors = {
            0xFF2D1F0E, 0xFF3A2510, 0xFF2A1C0C, 0xFF352112,
            0xFF1F1509, 0xFF2E1E0B, 0xFF381F0A, 0xFF261708
        };
        int sky = skyColors[index % skyColors.length];
        int ground = groundColors[index % groundColors.length];

        // Ceu (metade superior)
        int horizon = cy + imgH * 55 / 100;
        Gui.drawRect(cx, cy, cx + cw, horizon, sky);

        // Grama (metade inferior)
        Gui.drawRect(cx, horizon, cx + cw, cy + imgH, ground);

        // Simular arvores / cubos no horizonte
        int treeCount = 3 + (index % 3);
        for (int t = 0; t < treeCount; t++) {
            int tx = cx + 10 + (t * cw / (treeCount + 1));
            int treeH = imgH / 3 + (t % 2) * 8;
            int tw2 = 6 + (t % 3) * 4;
            // Tronco
            Gui.drawRect(tx + tw2/2 - 1, horizon - treeH, tx + tw2/2 + 2, horizon, 0xFF4A3000);
            // Folhagem
            Gui.drawRect(tx, horizon - treeH - 8, tx + tw2, horizon - treeH + 4, 0xFF2D5A1B);
            Gui.drawRect(tx - 2, horizon - treeH - 4, tx + tw2 + 2, horizon - treeH + 2, 0xFF3A6E22);
        }

        // Preview do mod em si (no centro)
        drawModPreviewOverlay(cx, cy, cw, imgH, mod, index);
    }

    /** Overlay de preview especifico do mod (texto/icone no centro do card) */
    private void drawModPreviewOverlay(int cx, int cy, int cw, int imgH, BaseMod mod, int index) {
        int centerX = cx + cw / 2;
        int centerY = cy + imgH / 2;
        String name = mod.getDisplayName();

        // Para alguns mods conhecidos, desenhar preview especifico
        String lName = name.toLowerCase();
        if (lName.contains("fps")) {
            // FPS: numero grande
            fontRendererObj.drawStringWithShadow("\u00A7fFPS: 523", cx + 8, cy + 12, 0xFFFFFF55);
        } else if (lName.contains("keystroke") || lName.contains("keystrokes") || lName.contains("teclas")) {
            // Teclas WASD
            drawKeystrokesPreview(cx, cy, cw, imgH);
        } else if (lName.contains("direction") || lName.contains("direcao") || lName.contains("direcção")) {
            // Bussola
            fontRendererObj.drawStringWithShadow("\u00A7fNW  N  NE", cx + cw/2 - 26, cy + 8, TEXT_WHITE);
            fontRendererObj.drawStringWithShadow("\u00A7c|", cx + cw/2, cy + 18, RED_ACCENT);
        } else if (lName.contains("cps")) {
            fontRendererObj.drawStringWithShadow("\u00A7fCPS: 12", cx + cw - 54, cy + 10, TEXT_WHITE);
        } else if (lName.contains("sprint") || lName.contains("toggle")) {
            // Icone corrida (simulado)
            fontRendererObj.drawStringWithShadow("\u00A7f>>>", centerX - 8, centerY - 4, TEXT_WHITE);
        } else if (lName.contains("zoom") || lName.contains("scope")) {
            // Mira
            int r = imgH / 4;
            Gui.drawRect(centerX - r, centerY - 1, centerX + r, centerY + 1, 0xAAFFFFFF);
            Gui.drawRect(centerX - 1, centerY - r, centerX + 1, centerY + r, 0xAAFFFFFF);
            Gui.drawRect(centerX - r, centerY - r, centerX + r, centerY - r + 1, 0x44FFFFFF);
            Gui.drawRect(centerX - r, centerY + r - 1, centerX + r, centerY + r, 0x44FFFFFF);
        } else if (lName.contains("animation") || lName.contains("animacao")) {
            fontRendererObj.drawStringWithShadow("\u00A7e1.7", centerX - 6, centerY - 4, 0xFFFFAA00);
            fontRendererObj.drawStringWithShadow("\u00A77anim", centerX - 8, centerY + 4, TEXT_GRAY);
        } else if (lName.contains("nametag")) {
            fontRendererObj.drawStringWithShadow("\u00A7c\u25C6 \u00A7fSteve", cx + cw/2 - 18, cy + 14, TEXT_WHITE);
        } else if (lName.contains("armor") || lName.contains("armadura")) {
            fontRendererObj.drawString("\u00A77Armor", cx + 8, cy + 10, TEXT_GRAY);
            Gui.drawRect(cx + 8, cy + 22, cx + 8 + (cw - 20) * 70 / 100, cy + 26, 0xFF5588FF);
            Gui.drawRect(cx + 8, cy + 22, cx + 8 + (cw - 20), cy + 26, 0xFF2A2A2A);
        } else if (lName.contains("potion") || lName.contains("effect") || lName.contains("efeito")) {
            fontRendererObj.drawString("\u00A7dSpeed II", cx + 8, cy + 10, 0xFFFF88FF);
            fontRendererObj.drawString("\u00A72Regen I", cx + 8, cy + 20, 0xFF55FF55);
        } else if (lName.contains("scoreboard") || lName.contains("placar")) {
            fontRendererObj.drawString("\u00A7cBED WARS", cx + cw/2 - 20, cy + 6, RED_ACCENT);
            fontRendererObj.drawString("\u00A7cR\u00A7f 14", cx + 8, cy + 18, TEXT_WHITE);
            fontRendererObj.drawString("\u00A79B\u00A7f 12", cx + 8, cy + 28, TEXT_WHITE);
        } else if (lName.contains("chat")) {
            fontRendererObj.drawString("\u00A77[S] \u00A7fYou: gg", cx + 6, cy + 14, TEXT_WHITE);
            fontRendererObj.drawString("\u00A77[S] \u00A7fPlayer: ez", cx + 6, cy + 24, TEXT_WHITE);
        } else if (lName.contains("freelook") || lName.contains("livre")) {
            // Olho
            Gui.drawRect(centerX - 10, centerY - 4, centerX + 10, centerY + 4, 0xAA333333);
            Gui.drawRect(centerX - 7, centerY - 6, centerX + 7, centerY + 6, 0xAA333333);
            Gui.drawRect(centerX - 3, centerY - 3, centerX + 3, centerY + 3, 0xFFDDDDDD);
            Gui.drawRect(centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFF111111);
        } else if (lName.contains("coord") || lName.contains("coordenada")) {
            fontRendererObj.drawString("\u00A77X: \u00A7f128", cx + 8, cy + 10, TEXT_WHITE);
            fontRendererObj.drawString("\u00A77Y: \u00A7f64", cx + 8, cy + 20, TEXT_WHITE);
            fontRendererObj.drawString("\u00A77Z: \u00A7f-256", cx + 8, cy + 30, TEXT_WHITE);
        } else if (lName.contains("reach") || lName.contains("alcance")) {
            fontRendererObj.drawStringWithShadow("\u00A7fReach: \u00A7c3.2", cx + 8, cy + 12, TEXT_WHITE);
        } else {
            // Preview generico: nome centralizado com icone
            String ico = "\u25CF";
            fontRendererObj.drawStringWithShadow("\u00A77" + ico, centerX - 2, centerY - 8, RED_ACCENT);
        }
    }

    private void drawKeystrokesPreview(int cx, int cy, int cw, int imgH) {
        int ks = 14; // tamanho de cada tecla
        int startX = cx + cw / 2 - ks - 2;
        int startY = cy + imgH / 2 - 10;

        // W
        Gui.drawRect(startX + ks + 2, startY - ks - 2, startX + ks * 2 + 2, startY - 2, 0xAA333333);
        fontRendererObj.drawString("W", startX + ks + 6, startY - ks + 1, TEXT_WHITE);

        // A S D
        int[] keys = {0, ks + 2, ks * 2 + 4};
        String[] labels = {"A", "S", "D"};
        for (int k = 0; k < 3; k++) {
            Gui.drawRect(startX + keys[k], startY, startX + keys[k] + ks, startY + ks, 0xAA333333);
            fontRendererObj.drawString(labels[k], startX + keys[k] + 4, startY + 4, TEXT_WHITE);
        }
        // LMB RMB
        int btnY = startY + ks + 4;
        Gui.drawRect(startX, btnY, startX + ks + 4, btnY + ks - 2, 0xAA333333);
        fontRendererObj.drawString("LMB", startX + 1, btnY + 3, 0xFFAAAAAA);
        Gui.drawRect(startX + ks + 6, btnY, startX + ks * 2 + 8, btnY + ks - 2, 0xAA333333);
        fontRendererObj.drawString("RMB", startX + ks + 7, btnY + 3, 0xFFAAAAAA);
    }

    private void drawHeart(int x, int y, int color) {
        // Coração simplificado com pixels
        Gui.drawRect(x + 2, y,     x + 6, y + 1,  color);
        Gui.drawRect(x + 8, y,     x + 12, y + 1, color);
        Gui.drawRect(x + 1, y + 1, x + 7,  y + 5, color);
        Gui.drawRect(x + 7, y + 1, x + 13, y + 5, color);
        Gui.drawRect(x + 2, y + 5, x + 12, y + 9, color);
        Gui.drawRect(x + 3, y + 9, x + 11, y + 12, color);
        Gui.drawRect(x + 4, y + 12, x + 10, y + 14, color);
        Gui.drawRect(x + 5, y + 14, x + 9,  y + 15, color);
        Gui.drawRect(x + 6, y + 15, x + 8,  y + 16, color);
    }

    private void drawCardBorder(int cx, int cy, int cw, int ch, boolean hov, boolean enabled) {
        int bc = hov ? (enabled ? 0xFF3A6FBB : 0xFF444444) : 0xFF282828;
        // Top
        Gui.drawRect(cx, cy, cx + cw, cy + 1, bc);
        // Bottom
        Gui.drawRect(cx, cy + ch - 1, cx + cw, cy + ch, bc);
        // Left
        Gui.drawRect(cx, cy, cx + 1, cy + ch, bc);
        // Right
        Gui.drawRect(cx + cw - 1, cy, cx + cw, cy + ch, bc);
    }

    private int getCardPreviewColor(int index) {
        int[] colors = {
            0xFF1A3A5C, 0xFF1E2A3A, 0xFF1A2E3A,
            0xFF0D1F35, 0xFF182840, 0xFF1A1E38,
            0xFF101824, 0xFF1A2438
        };
        return colors[index % colors.length];
    }

    /** Desenha um escudo simplificado com a cor RED_ACCENT (logo sidebar) */
    private void drawShieldIcon(int x, int y, int size) {
        // Escudo basico: retangulo superior + triangulo inferior
        int hw = size / 2;
        // Corpo principal do escudo
        Gui.drawRect(x, y, x + size, y + size * 2 / 3, RED_ACCENT);
        // Ponta do escudo (3 camadas triangulares)
        int base = y + size * 2 / 3;
        for (int i = 0; i < size / 2; i++) {
            int margin = (int)((float) i * hw / (size / 2));
            if (x + margin < x + size - margin)
                Gui.drawRect(x + margin, base + i, x + size - margin, base + i + 1, RED_ACCENT);
        }
        // Detalhe branco central (letra B)
        Gui.drawRect(x + size / 2 - 3, y + 4, x + size / 2 + 3, y + size / 3, 0xCCFFFFFF);
    }

    private int blendColor(int c1, int c2, float t) {
        int r = (int)(((c1 >> 16) & 0xFF) * (1 - t) + ((c2 >> 16) & 0xFF) * t);
        int g = (int)(((c1 >> 8)  & 0xFF) * (1 - t) + ((c2 >> 8)  & 0xFF) * t);
        int b = (int)((c1 & 0xFF) * (1 - t) + (c2 & 0xFF) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    // ── Scissor ──────────────────────────────────────────────────────────────
    private void enableScissor(int x, int y, int w, int h) {
        double sx = mc.displayWidth  / (double) width;
        double sy = mc.displayHeight / (double) height;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(x * sx), (int)(mc.displayHeight - (y + h) * sy), (int)(w * sx), (int)(h * sy));
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    // ── Filtros e ordenacao ──────────────────────────────────────────────────
    private List<BaseMod> getFilteredMods() {
        List<BaseMod> all = ModManager.getInstance().getMods();
        String query = (searchField != null) ? searchField.getText().toLowerCase().trim() : "";

        List<BaseMod> list = new ArrayList<BaseMod>();
        for (BaseMod mod : all) {
            boolean search = query.isEmpty()
                    || mod.getDisplayName().toLowerCase().contains(query)
                    || mod.getDescription().toLowerCase().contains(query);
            if (search) list.add(mod);
        }

        // Ordenacao
        if (sortIndex == 1) { // A-Z
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).getDisplayName().compareToIgnoreCase(list.get(j).getDisplayName()) > 0) {
                        BaseMod tmp = list.get(i); list.set(i, list.get(j)); list.set(j, tmp);
                    }
                }
            }
        } else if (sortIndex == 2) { // Z-A
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).getDisplayName().compareToIgnoreCase(list.get(j).getDisplayName()) < 0) {
                        BaseMod tmp = list.get(i); list.set(i, list.get(j)); list.set(j, tmp);
                    }
                }
            }
        } else if (sortIndex == 3) { // Enabled first
            List<BaseMod> en = new ArrayList<BaseMod>(), dis = new ArrayList<BaseMod>();
            for (BaseMod m : list) { if (m.isEnabled()) en.add(m); else dis.add(m); }
            list.clear(); list.addAll(en); list.addAll(dis);
        } else if (sortIndex == 4) { // Disabled first
            List<BaseMod> en = new ArrayList<BaseMod>(), dis = new ArrayList<BaseMod>();
            for (BaseMod m : list) { if (m.isEnabled()) en.add(m); else dis.add(m); }
            list.clear(); list.addAll(dis); list.addAll(en);
        }

        return list;
    }

    // ── Interacao ────────────────────────────────────────────────────────────
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Fecha dropdown ao clicar fora
        if (sortDropOpen) {
            int sortX = contentX + contentW - 430;
            int sortDropY = winY + TOPBAR_H / 2 - 11 + 23;
            int sortW = 140;
            int optH = 20;
            int dropH = SORT_OPTIONS.length * optH;
            if (mouseX >= sortX && mouseX <= sortX + sortW
             && mouseY >= sortDropY && mouseY <= sortDropY + dropH) {
                int clickedOpt = (mouseY - sortDropY) / optH;
                if (clickedOpt >= 0 && clickedOpt < SORT_OPTIONS.length) {
                    sortIndex = clickedOpt;
                    scrollOffset = 0;
                }
                sortDropOpen = false;
                return;
            }
            sortDropOpen = false;
        }

        // Click na busca
        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, mouseButton);

        // Click no sort
        int sortX = contentX + contentW - 430;
        int sortY = winY + TOPBAR_H / 2 - 11;
        int sortW = 140;
        int sortH = 22;
        if (mouseX >= sortX && mouseX <= sortX + sortW
         && mouseY >= sortY && mouseY <= sortY + sortH) {
            sortDropOpen = !sortDropOpen;
            return;
        }

        // Seta voltar (fecha menu)
        if (mouseX >= contentX + 8 && mouseX <= contentX + 24
         && mouseY >= winY && mouseY <= winY + TOPBAR_H) {
            Config.salvar();
            mc.displayGuiScreen(null);
            return;
        }

        // Click nos cards
        List<BaseMod> mods = getFilteredMods();
        int totalGap = CARD_GAP * (cols - 1) + GRID_PAD * 2;
        int realCardW = (contentW - totalGap) / cols;

        for (int i = 0; i < mods.size(); i++) {
            BaseMod mod = mods.get(i);
            int col = i % cols;
            int row = i / cols;
            int cx = contentX + GRID_PAD + col * (realCardW + CARD_GAP);
            int cy = contentY + GRID_PAD + row * (CARD_H + CARD_GAP) - scrollOffset;

            if (cy + CARD_H < contentY || cy > contentY + contentH) continue;
            if (mouseX < cx || mouseX > cx + realCardW || mouseY < cy || mouseY > cy + CARD_H) continue;

            int imgH = CARD_H - CARD_BOT_H;

            // Coração (favorito)
            int heartX = cx + realCardW - 22;
            int heartY = cy + imgH + 8;
            if (mouseX >= heartX && mouseX <= heartX + 16 && mouseY >= heartY && mouseY <= heartY + 16) {
                if (favorites.contains(i)) favorites.remove(Integer.valueOf(i));
                else favorites.add(i);
                return;
            }

            // Click na base do card (area nome) = toggle
            if (mouseY >= cy + imgH) {
                mod.toggle();
                Config.salvar();
                return;
            }

            // Click na preview = abre settings se tiver settings, senao toggle
            if (mouseButton == 1) { // botao direito = settings
                mc.displayGuiScreen(new ModSettingsScreen(this, mod));
                return;
            }
            // botao esquerdo = toggle
            mod.toggle();
            Config.salvar();
            return;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField != null && searchField.textboxKeyTyped(typedChar, keyCode)) {
            scrollOffset = 0;
            return;
        }
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RSHIFT) {
            Config.salvar();
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
