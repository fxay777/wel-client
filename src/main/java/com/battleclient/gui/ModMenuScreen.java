package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.ModManager;
import com.battleclient.mods.BaseMod;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ModMenuScreen - Interface Profissional Estilo Battle Client
 * Fiel ao design do cliente moderno:
 *  - Sidebar lateral esquerda com logo e navegacao
 *  - Grid de 4 colunas com thumbnails de paisagem Minecraft
 *  - Overlays visuais de preview para cada mod
 *  - Pesquisa em tempo real e ordenacao (Popular, A-Z, Z-A, Ativos, Desativados)
 *  - Favoritos com icone de coracao clicavel
 *  - Click esquerdo = toggle ON/OFF | Click direito = Configuracoes
 */
public class ModMenuScreen extends GuiScreen {

    private static final int SIDEBAR_WIDTH = 160;
    private static final int TOP_BAR_HEIGHT = 56;
    private static final int NAV_ITEM_HEIGHT = 40;

    private static final int CARD_WIDTH = 188;
    private static final int CARD_HEIGHT = 146;
    private static final int CARD_PREVIEW_HEIGHT = 110;
    private static final int CARD_BOTTOM_HEIGHT = 36;
    private static final int CARD_GAP = 14;
    private static final int GRID_COLS = 4;

    // Paleta de cores moderna (Dark Obsidian & Slate)
    private static final int COLOR_BG          = 0xFF111216;
    private static final int COLOR_SIDEBAR     = 0xFF0D0E11;
    private static final int COLOR_CARD_BG     = 0xFF16171C;
    private static final int COLOR_CARD_BOTTOM = 0xFF141519;
    private static final int COLOR_RED         = 0xFFE53935;
    private static final int COLOR_GREEN       = 0xFF2ECC71;
    private static final int COLOR_TEXT_WHITE  = 0xFFF0F1F5;
    private static final int COLOR_TEXT_GRAY   = 0xFF8B8E9B;
    private static final int COLOR_DIVIDER     = 0xFF1E2027;
    private static final int COLOR_SEARCH_BG   = 0xFF1A1C23;
    private static final int COLOR_SEARCH_BOR  = 0xFF2A2D38;
    private static final int COLOR_HEART_OFF   = 0xFF4A4E5C;
    private static final int COLOR_HEART_ON    = 0xFFE53935;

    private static final String[] SORT_OPTIONS = {"Popular", "A-Z", "Z-A", "Ativos", "Desativados"};

    private final GuiScreen parent;
    private final List<BaseMod> allMods = new ArrayList<BaseMod>();
    private final List<BaseMod> filteredMods = new ArrayList<BaseMod>();
    private final List<String> favorites = new ArrayList<String>();

    private String searchText = "";
    private boolean searchFocused = false;
    private int selectedSort = 0;
    private boolean sortDropdownOpen = false;

    private float scrollOffset = 0.0F;
    private float maxScroll = 0.0F;
    private int hoveredCardIndex = -1;

    private int contentX, contentY, contentW, contentH;
    private int gridAreaX, gridAreaY, gridAreaW, gridAreaH;

    public ModMenuScreen() {
        this(null);
    }

    public ModMenuScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        contentX = SIDEBAR_WIDTH;
        contentY = 0;
        contentW = width - SIDEBAR_WIDTH;
        contentH = height;

        gridAreaX = contentX + CARD_GAP;
        gridAreaY = contentY + TOP_BAR_HEIGHT + 8;
        gridAreaW = contentW - CARD_GAP * 2;
        gridAreaH = contentH - TOP_BAR_HEIGHT - 12;

        loadMods();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        Config.salvar();
    }

    private void loadMods() {
        allMods.clear();
        for (BaseMod mod : ModManager.getInstance().getMods()) {
            allMods.add(mod);
        }
        applyFilterAndSort();
    }

    private void applyFilterAndSort() {
        filteredMods.clear();
        String query = searchText.toLowerCase().trim();

        for (BaseMod mod : allMods) {
            if (query.isEmpty()) {
                filteredMods.add(mod);
            } else {
                String name = mod.getDisplayName().toLowerCase();
                String desc = mod.getDescription().toLowerCase();
                String cat  = mod.getCategory().toLowerCase();
                if (name.contains(query) || desc.contains(query) || cat.contains(query)) {
                    filteredMods.add(mod);
                }
            }
        }

        switch (selectedSort) {
            case 1: // A-Z
                Collections.sort(filteredMods, new Comparator<BaseMod>() {
                    @Override
                    public int compare(BaseMod a, BaseMod b) {
                        return a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
                    }
                });
                break;
            case 2: // Z-A
                Collections.sort(filteredMods, new Comparator<BaseMod>() {
                    @Override
                    public int compare(BaseMod a, BaseMod b) {
                        return b.getDisplayName().compareToIgnoreCase(a.getDisplayName());
                    }
                });
                break;
            case 3: // Ativos primeiro
                Collections.sort(filteredMods, new Comparator<BaseMod>() {
                    @Override
                    public int compare(BaseMod a, BaseMod b) {
                        if (a.isEnabled() == b.isEnabled()) {
                            return a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
                        }
                        return a.isEnabled() ? -1 : 1;
                    }
                });
                break;
            case 4: // Desativados primeiro
                Collections.sort(filteredMods, new Comparator<BaseMod>() {
                    @Override
                    public int compare(BaseMod a, BaseMod b) {
                        if (a.isEnabled() == b.isEnabled()) {
                            return a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
                        }
                        return a.isEnabled() ? 1 : -1;
                    }
                });
                break;
            default: // Popular (ordem padrão do registro)
                break;
        }

        recalcMaxScroll();
    }

    private void recalcMaxScroll() {
        int rows = (filteredMods.size() + GRID_COLS - 1) / GRID_COLS;
        int totalGridH = rows * (CARD_HEIGHT + CARD_GAP) + CARD_GAP;
        maxScroll = Math.max(0, totalGridH - gridAreaH);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;
    }

    private boolean isFavorite(BaseMod mod) {
        return favorites.contains(mod.getId());
    }

    private void toggleFavorite(BaseMod mod) {
        String id = mod.getId();
        if (favorites.contains(id)) {
            favorites.remove(id);
        } else {
            favorites.add(id);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Fundo geral escuro
        Gui.drawRect(0, 0, width, height, COLOR_BG);

        drawSidebar(mouseX, mouseY);
        drawTopBar(mouseX, mouseY);
        drawModGrid(mouseX, mouseY);

        if (sortDropdownOpen) {
            drawSortDropdown(mouseX, mouseY);
        }

        drawTooltip(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // =========================================================================
    //  SIDEBAR (Menu Lateral)
    // =========================================================================

    private void drawSidebar(int mouseX, int mouseY) {
        Gui.drawRect(0, 0, SIDEBAR_WIDTH, height, COLOR_SIDEBAR);
        Gui.drawRect(SIDEBAR_WIDTH - 1, 0, SIDEBAR_WIDTH, height, COLOR_DIVIDER);

        // Header da Sidebar (Logo Escudo Vermelho + "BATTLE CLIENT")
        int logoY = 16;
        drawShieldLogo(18, logoY);

        fontRendererObj.drawStringWithShadow("\u00A7f\u00A7lBATTLE", 56, logoY + 2, COLOR_TEXT_WHITE);
        fontRendererObj.drawStringWithShadow("\u00A7f\u00A7lCLIENT", 56, logoY + 14, COLOR_TEXT_WHITE);

        Gui.drawRect(12, 62, SIDEBAR_WIDTH - 12, 63, COLOR_DIVIDER);

        // Itens de Navegacao
        String[] navLabels = {"Home", "Friends", "Cosmetics", "Store", "Mods", "Gallery"};
        int navY = 74;

        for (int i = 0; i < navLabels.length; i++) {
            boolean isActive = (i == 4); // "Mods" ativo
            boolean isHovered = mouseX >= 0 && mouseX < SIDEBAR_WIDTH && mouseY >= navY && mouseY < navY + NAV_ITEM_HEIGHT;

            if (isActive) {
                // Fundo com destaque escuro e barra vermelha na ponta esquerda
                Gui.drawRect(0, navY, SIDEBAR_WIDTH - 1, navY + NAV_ITEM_HEIGHT, 0xFF1A171B);
                Gui.drawRect(0, navY + 4, 3, navY + NAV_ITEM_HEIGHT - 4, COLOR_RED);
            } else if (isHovered) {
                Gui.drawRect(0, navY, SIDEBAR_WIDTH - 1, navY + NAV_ITEM_HEIGHT, 0xFF14151B);
            }

            int iconColor = isActive ? COLOR_RED : (isHovered ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY);
            drawNavIcon(i, 20, navY + NAV_ITEM_HEIGHT / 2, iconColor);

            int textColor = isActive ? COLOR_TEXT_WHITE : (isHovered ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY);
            fontRendererObj.drawString(navLabels[i], 46, navY + (NAV_ITEM_HEIGHT - 8) / 2, textColor);

            navY += NAV_ITEM_HEIGHT;
        }

        // Settings na parte inferior da sidebar
        int settingsY = height - NAV_ITEM_HEIGHT - 12;
        Gui.drawRect(12, settingsY - 8, SIDEBAR_WIDTH - 12, settingsY - 7, COLOR_DIVIDER);

        boolean settingsHover = mouseX >= 0 && mouseX < SIDEBAR_WIDTH && mouseY >= settingsY && mouseY < settingsY + NAV_ITEM_HEIGHT;
        if (settingsHover) {
            Gui.drawRect(0, settingsY, SIDEBAR_WIDTH - 1, settingsY + NAV_ITEM_HEIGHT, 0xFF14151B);
        }
        drawNavIcon(6, 20, settingsY + NAV_ITEM_HEIGHT / 2, settingsHover ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY);
        fontRendererObj.drawString("Settings", 46, settingsY + (NAV_ITEM_HEIGHT - 8) / 2, settingsHover ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY);
    }

    private void drawShieldLogo(int x, int y) {
        int w = 26, h = 30;
        int red = COLOR_RED;
        int darkRed = 0xFFB71C1C;

        // Escudo externo
        Gui.drawRect(x + 2, y, x + w - 2, y + h - 8, red);
        Gui.drawRect(x + 1, y + 2, x + w - 1, y + h - 9, red);
        Gui.drawRect(x, y + 4, x + w, y + h - 11, red);
        // Ponta inferior triangular
        Gui.drawRect(x + 4, y + h - 8, x + w - 4, y + h - 5, red);
        Gui.drawRect(x + 7, y + h - 5, x + w - 7, y + h - 3, red);
        Gui.drawRect(x + 10, y + h - 3, x + w - 10, y + h, red);

        // Recorte interno escuro (cria o formato do logo da print)
        int inX = x + 4, inY = y + 4, inW = w - 8, inH = h - 14;
        Gui.drawRect(inX, inY, inX + inW, inY + inH, darkRed);
        Gui.drawRect(inX + 3, inY + 3, inX + inW - 3, inY + inH - 3, COLOR_SIDEBAR);
    }

    private void drawNavIcon(int index, int x, int cy, int color) {
        switch (index) {
            case 0: // Home
                Gui.drawRect(x, cy - 1, x + 12, cy + 6, color);
                Gui.drawRect(x + 2, cy - 4, x + 10, cy - 1, color);
                Gui.drawRect(x + 4, cy - 7, x + 8, cy - 4, color);
                Gui.drawRect(x + 4, cy + 2, x + 8, cy + 6, COLOR_SIDEBAR);
                break;
            case 1: // Friends
                Gui.drawRect(x + 2, cy - 6, x + 6, cy - 2, color);
                Gui.drawRect(x + 7, cy - 5, x + 11, cy - 2, color);
                Gui.drawRect(x, cy - 1, x + 8, cy + 5, color);
                Gui.drawRect(x + 6, cy, x + 13, cy + 5, color);
                break;
            case 2: // Cosmetics (T-Shirt)
                Gui.drawRect(x + 3, cy - 6, x + 9, cy + 6, color);
                Gui.drawRect(x, cy - 4, x + 12, cy - 1, color);
                Gui.drawRect(x + 4, cy - 6, x + 8, cy - 4, COLOR_SIDEBAR);
                break;
            case 3: // Store (Carrinho)
                Gui.drawRect(x, cy - 5, x + 2, cy - 3, color);
                Gui.drawRect(x + 2, cy - 4, x + 11, cy + 2, color);
                Gui.drawRect(x + 3, cy - 3, x + 10, cy + 1, COLOR_SIDEBAR);
                Gui.drawRect(x + 3, cy + 3, x + 5, cy + 5, color);
                Gui.drawRect(x + 8, cy + 3, x + 10, cy + 5, color);
                break;
            case 4: // Mods (Peca de Quebra-Cabeca)
                Gui.drawRect(x + 2, cy - 5, x + 10, cy + 5, color);
                Gui.drawRect(x + 4, cy - 7, x + 8, cy - 5, color);
                Gui.drawRect(x + 10, cy - 2, x + 12, cy + 2, color);
                Gui.drawRect(x, cy - 1, x + 2, cy + 3, COLOR_SIDEBAR);
                break;
            case 5: // Gallery (Foto)
                Gui.drawRect(x, cy - 5, x + 12, cy + 5, color);
                Gui.drawRect(x + 1, cy - 4, x + 11, cy + 4, COLOR_SIDEBAR);
                Gui.drawRect(x + 2, cy + 1, x + 6, cy + 3, color);
                Gui.drawRect(x + 5, cy - 1, x + 10, cy + 3, color);
                break;
            case 6: // Settings (Engrenagem)
                Gui.drawRect(x + 3, cy - 6, x + 9, cy + 6, color);
                Gui.drawRect(x, cy - 3, x + 12, cy + 3, color);
                Gui.drawRect(x + 1, cy - 5, x + 11, cy + 5, color);
                Gui.drawRect(x + 4, cy - 2, x + 8, cy + 2, COLOR_SIDEBAR);
                break;
        }
    }

    // =========================================================================
    //  TOP BAR (Barra Superior)
    // =========================================================================

    private void drawTopBar(int mouseX, int mouseY) {
        int barX = SIDEBAR_WIDTH;
        int barW = width - SIDEBAR_WIDTH;
        int barH = TOP_BAR_HEIGHT;

        Gui.drawRect(barX, 0, barX + barW, barH, COLOR_BG);
        Gui.drawRect(barX, barH - 1, barX + barW, barH, COLOR_DIVIDER);

        // Titulo "< Mods"
        boolean backHover = mouseX >= barX + 14 && mouseX <= barX + 70 && mouseY >= 14 && mouseY <= barH - 14;
        String backChevron = "\u00A78<\u00A7r  ";
        fontRendererObj.drawStringWithShadow(backChevron + (backHover ? "\u00A7cMods" : "\u00A7fMods"), barX + 16, (barH - 8) / 2, COLOR_TEXT_WHITE);

        // Botoes da Janela (Minimizar, Maximizar, Fechar)
        int winBtnX = barX + barW - 68;
        int winBtnY = (barH - 14) / 2;
        fontRendererObj.drawString("\u2014", winBtnX, winBtnY, COLOR_TEXT_GRAY);
        fontRendererObj.drawString("\u25A2", winBtnX + 22, winBtnY - 1, COLOR_TEXT_GRAY);

        boolean closeHov = mouseX >= winBtnX + 44 && mouseX <= winBtnX + 60 && mouseY >= winBtnY - 2 && mouseY <= winBtnY + 12;
        fontRendererObj.drawString("\u2715", winBtnX + 46, winBtnY, closeHov ? COLOR_RED : COLOR_TEXT_GRAY);

        // Campo de Pesquisa "Search mods..."
        int searchW = 190;
        int searchH = 26;
        int searchX = winBtnX - 18 - searchW;
        int searchY = (barH - searchH) / 2;
        drawSearchField(searchX, searchY, searchW, searchH, mouseX, mouseY);

        // Botao de Ordenacao "Sort: Popular v"
        int sortW = 135;
        int sortH = 26;
        int sortX = searchX - 10 - sortW;
        int sortY = (barH - sortH) / 2;
        drawSortButton(sortX, sortY, sortW, sortH, mouseX, mouseY);
    }

    private void drawSearchField(int x, int y, int w, int h, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        int border = searchFocused ? COLOR_RED : (hovered ? 0xFF3D4150 : COLOR_SEARCH_BOR);

        Gui.drawRect(x, y, x + w, y + h, border);
        Gui.drawRect(x + 1, y + 1, x + w - 1, y + h - 1, COLOR_SEARCH_BG);

        // Icone Lupa
        int lx = x + 9;
        int ly = y + h / 2 - 1;
        Gui.drawRect(lx, ly - 3, lx + 5, ly + 2, COLOR_TEXT_GRAY);
        Gui.drawRect(lx + 1, ly - 2, lx + 4, ly + 1, COLOR_SEARCH_BG);
        Gui.drawRect(lx + 4, ly + 1, lx + 7, ly + 4, COLOR_TEXT_GRAY);

        if (searchText.isEmpty() && !searchFocused) {
            fontRendererObj.drawString("Search mods...", x + 22, y + (h - 8) / 2, COLOR_TEXT_GRAY);
        } else {
            String cursor = searchFocused && (System.currentTimeMillis() % 1000 < 500) ? "_" : "";
            fontRendererObj.drawString(searchText + cursor, x + 22, y + (h - 8) / 2, COLOR_TEXT_WHITE);
        }
    }

    private void drawSortButton(int x, int y, int w, int h, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        int border = sortDropdownOpen ? COLOR_RED : (hovered ? 0xFF3D4150 : COLOR_SEARCH_BOR);

        Gui.drawRect(x, y, x + w, y + h, border);
        Gui.drawRect(x + 1, y + 1, x + w - 1, y + h - 1, COLOR_SEARCH_BG);

        // Icone de ordenacao (setas para cima/baixo)
        fontRendererObj.drawString("\u21C5", x + 8, y + (h - 8) / 2, COLOR_TEXT_GRAY);

        String label = "Sort: \u00A7f" + SORT_OPTIONS[selectedSort];
        fontRendererObj.drawString(label, x + 20, y + (h - 8) / 2, COLOR_TEXT_GRAY);

        fontRendererObj.drawString("\u25BC", x + w - 14, y + (h - 6) / 2, COLOR_TEXT_GRAY);
    }

    private void drawSortDropdown(int mouseX, int mouseY) {
        int barX = SIDEBAR_WIDTH;
        int barW = width - SIDEBAR_WIDTH;
        int winBtnX = barX + barW - 68;
        int searchW = 190;
        int searchX = winBtnX - 18 - searchW;
        int sortW = 135;
        int sortX = searchX - 10 - sortW;
        int sortH = 26;
        int dropY = (TOP_BAR_HEIGHT - sortH) / 2 + sortH + 2;
        int itemH = 22;
        int totalH = SORT_OPTIONS.length * itemH;

        Gui.drawRect(sortX - 1, dropY - 1, sortX + sortW + 1, dropY + totalH + 1, 0xFF3D4150);
        Gui.drawRect(sortX, dropY, sortX + sortW, dropY + totalH, 0xFF181A20);

        for (int i = 0; i < SORT_OPTIONS.length; i++) {
            int iy = dropY + i * itemH;
            boolean hov = mouseX >= sortX && mouseX < sortX + sortW && mouseY >= iy && mouseY < iy + itemH;

            if (hov) {
                Gui.drawRect(sortX, iy, sortX + sortW, iy + itemH, 0xFF242731);
            }
            if (i == selectedSort) {
                Gui.drawRect(sortX, iy, sortX + 3, iy + itemH, COLOR_RED);
            }

            int tc = (i == selectedSort) ? COLOR_RED : (hov ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY);
            fontRendererObj.drawString(SORT_OPTIONS[i], sortX + 12, iy + (itemH - 8) / 2, tc);
        }
    }

    // =========================================================================
    //  MOD GRID (Grade de Cards)
    // =========================================================================

    private void drawModGrid(int mouseX, int mouseY) {
        hoveredCardIndex = -1;

        ScaledResolution sr = new ScaledResolution(mc);
        int sf = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(gridAreaX * sf, (height - gridAreaY - gridAreaH) * sf, gridAreaW * sf, gridAreaH * sf);

        int totalGridW = GRID_COLS * CARD_WIDTH + (GRID_COLS - 1) * CARD_GAP;
        int startX = gridAreaX + Math.max(0, (gridAreaW - totalGridW) / 2);

        for (int i = 0; i < filteredMods.size(); i++) {
            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            int cx = startX + col * (CARD_WIDTH + CARD_GAP);
            int cy = gridAreaY + row * (CARD_HEIGHT + CARD_GAP) - (int) scrollOffset;

            // Culling de itens fora da tela
            if (cy + CARD_HEIGHT < gridAreaY || cy > gridAreaY + gridAreaH) continue;

            BaseMod mod = filteredMods.get(i);
            boolean hovered = mouseX >= cx && mouseX < cx + CARD_WIDTH
                    && mouseY >= cy && mouseY < cy + CARD_HEIGHT
                    && mouseY >= gridAreaY && mouseY < gridAreaY + gridAreaH;

            if (hovered && !sortDropdownOpen) {
                hoveredCardIndex = i;
            }

            drawCard(mod, cx, cy, hovered, mouseX, mouseY);
        }

        // Scrollbar elegante na borda direita
        if (maxScroll > 0) {
            int sbX = width - 6;
            int sbY = gridAreaY;
            int sbH = gridAreaH;
            Gui.drawRect(sbX, sbY, sbX + 3, sbY + sbH, 0x33000000);

            int thumbH = Math.max(24, (int) ((float) sbH * sbH / (sbH + maxScroll)));
            int thumbY = sbY + (int) (scrollOffset / maxScroll * (sbH - thumbH));
            Gui.drawRect(sbX, thumbY, sbX + 3, thumbY + thumbH, 0xFF4A4E5C);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawCard(BaseMod mod, int x, int y, boolean hovered, int mouseX, int mouseY) {
        boolean enabled = mod.isEnabled();

        // Borda do card: destaque vermelho quando ativo, sutil quando desligado
        int borderColor = hovered ? (enabled ? COLOR_RED : 0xFF3D4150) : (enabled ? 0xFF8A1E20 : 0xFF20222B);
        Gui.drawRect(x - 1, y - 1, x + CARD_WIDTH + 1, y + CARD_HEIGHT + 1, borderColor);

        // Fundo com visual de paisagem Minecraft (Ceu gradiente + horizonte + montanhas)
        drawMinecraftLandscape(x, y, CARD_WIDTH, CARD_PREVIEW_HEIGHT, mod.getId().hashCode());

        // Overlay de preview individual de cada mod
        drawModPreview(mod, x, y, CARD_WIDTH, CARD_PREVIEW_HEIGHT);

        // Indicador de status (Bolinha verde ON / cinza OFF no canto superior esquerdo)
        int dotX = x + 7, dotY = y + 7;
        int dotColor = enabled ? COLOR_GREEN : 0xFF5A5E6E;
        Gui.drawRect(dotX, dotY, dotX + 6, dotY + 6, dotColor);

        // Base inferior do card (Nome + Coracao)
        int botY = y + CARD_PREVIEW_HEIGHT;
        Gui.drawRect(x, botY, x + CARD_WIDTH, y + CARD_HEIGHT, COLOR_CARD_BOTTOM);
        Gui.drawRect(x, botY, x + CARD_WIDTH, botY + 1, COLOR_DIVIDER);

        // Nome do mod
        String name = mod.getDisplayName();
        int maxW = CARD_WIDTH - 32;
        if (fontRendererObj.getStringWidth(name) > maxW) {
            name = fontRendererObj.trimStringToWidth(name, maxW - 8) + "..";
        }
        fontRendererObj.drawStringWithShadow(name, x + 8, botY + (CARD_BOTTOM_HEIGHT - 8) / 2, enabled ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY);

        // Botao Coracao (Favorito)
        int heartX = x + CARD_WIDTH - 18;
        int heartY = botY + (CARD_BOTTOM_HEIGHT - 10) / 2;
        boolean fav = isFavorite(mod);
        boolean heartHov = mouseX >= heartX - 2 && mouseX <= heartX + 12 && mouseY >= heartY - 2 && mouseY <= heartY + 12;

        int heartColor = fav ? COLOR_HEART_ON : (heartHov ? COLOR_RED : COLOR_HEART_OFF);
        drawHeart(heartX, heartY, heartColor, fav);
    }

    private void drawMinecraftLandscape(int x, int y, int w, int h, int seed) {
        int skyH = h * 58 / 100;
        int topSky = 0xFF14243B;
        int botSky = 0xFF2B4468;
        int earth  = 0xFF1B2318;

        // Gradiente do Ceu
        for (int i = 0; i < skyH; i++) {
            float t = (float) i / skyH;
            int r = (int) (((topSky >> 16 & 0xFF) * (1 - t)) + ((botSky >> 16 & 0xFF) * t));
            int g = (int) (((topSky >> 8 & 0xFF) * (1 - t)) + ((botSky >> 8 & 0xFF) * t));
            int b = (int) (((topSky & 0xFF) * (1 - t)) + ((botSky & 0xFF) * t));
            Gui.drawRect(x, y + i, x + w, y + i + 1, 0xFF000000 | (r << 16) | (g << 8) | b);
        }

        // Chão de terra/grama escura
        Gui.drawRect(x, y + skyH, x + w, y + h, earth);
        Gui.drawRect(x, y + skyH - 1, x + w, y + skyH, 0xFF2A3A22);

        // Silhueta de arvores/montanhas no horizonte
        int tCount = 4;
        for (int t = 0; t < tCount; t++) {
            int tx = x + 16 + t * (w / tCount) + (seed % 7);
            int th = 10 + ((seed + t * 5) % 6);
            Gui.drawRect(tx, y + skyH - th, tx + 3, y + skyH, 0xFF16120C);
            Gui.drawRect(tx - 4, y + skyH - th - 6, tx + 7, y + skyH - th, 0xFF172813);
        }

        // Vinheta suave de sombra na base da imagem
        for (int v = 0; v < 14; v++) {
            int alpha = (int) (v * 7.5F);
            Gui.drawRect(x, y + h - 14 + v, x + w, y + h - 13 + v, (alpha << 24));
        }
    }

    private void drawModPreview(BaseMod mod, int x, int y, int w, int h) {
        String id = mod.getId().toLowerCase();
        int cx = x + w / 2;
        int cy = y + h / 2;

        if (id.contains("fps")) {
            drawDarkPill(x + 8, y + 10, "FPS: 523", 0xFFFFFF55);
        } else if (id.contains("keystroke")) {
            int kx = cx - 18, ky = cy - 14, ks = 11;
            drawKeyBox(kx + ks + 2, ky - ks - 2, "W");
            drawKeyBox(kx, ky, "A");
            drawKeyBox(kx + ks + 2, ky, "S");
            drawKeyBox(kx + 2 * (ks + 2), ky, "D");
            drawWideKeyBox(kx, ky + ks + 2, ks * 3 + 4, "LMB  RMB");
        } else if (id.contains("direction") || id.contains("compass")) {
            Gui.drawRect(cx - 45, y + 8, cx + 45, y + 22, 0x88000000);
            fontRendererObj.drawStringWithShadow("\u00A7fW   NW   \u00A7cN\u00A7f   NE", cx - 38, y + 11, COLOR_TEXT_WHITE);
        } else if (id.contains("cps")) {
            drawDarkPill(x + w - 58, y + 10, "CPS: 12", COLOR_TEXT_WHITE);
        } else if (id.contains("sprint")) {
            fontRendererObj.drawStringWithShadow("\u00A7f>>> \u00A7aSPRINT", cx - 28, cy - 4, COLOR_GREEN);
        } else if (id.contains("zoom")) {
            int r = 16;
            Gui.drawRect(cx - r, cy, cx + r, cy + 1, 0xDDFFFFFF);
            Gui.drawRect(cx, cy - r, cx + 1, cy + r, 0xDDFFFFFF);
        } else if (id.contains("animation")) {
            drawDarkPill(cx - 30, cy - 8, "\u00A7e1.7 \u00A7fBlockhit", 0xFFFFAA00);
        } else if (id.contains("nametag")) {
            drawDarkPill(cx - 28, cy - 8, "\u00A7c\u25C6 \u00A7fSteve", COLOR_TEXT_WHITE);
        } else if (id.contains("freelook")) {
            drawDarkPill(cx - 26, cy - 8, "\u00A7b\u25C9 \u00A7f360\u00B0", 0xFF55FFFF);
        } else if (id.contains("reach")) {
            drawDarkPill(cx - 30, cy - 8, "Reach: \u00A7c3.2m", COLOR_TEXT_WHITE);
        } else if (id.contains("coord")) {
            Gui.drawRect(x + 8, y + 8, x + 68, y + 36, 0x88000000);
            fontRendererObj.drawString("X: 128", x + 12, y + 11, COLOR_TEXT_WHITE);
            fontRendererObj.drawString("Y: 64", x + 12, y + 19, COLOR_TEXT_WHITE);
            fontRendererObj.drawString("Z: -256", x + 12, y + 27, COLOR_TEXT_WHITE);
        } else if (id.contains("potion") || id.contains("effect")) {
            drawDarkPill(x + 8, y + 8, "\u00A7bSpeed II \u00A770:45", COLOR_TEXT_WHITE);
            drawDarkPill(x + 8, y + 22, "\u00A7cStrength \u00A771:12", COLOR_TEXT_WHITE);
        } else if (id.contains("scoreboard")) {
            Gui.drawRect(cx - 36, y + 6, cx + 36, y + 36, 0xAA000000);
            fontRendererObj.drawString("\u00A7eBED WARS", cx - 26, y + 9, 0xFFFFAA00);
            fontRendererObj.drawString("\u00A7cR \u00A7fRed: \u2713 14", cx - 32, y + 19, COLOR_TEXT_WHITE);
            fontRendererObj.drawString("\u00A79B \u00A7fBlue: \u2713 12", cx - 32, y + 27, COLOR_TEXT_WHITE);
        } else if (id.contains("chat")) {
            Gui.drawRect(x + 8, cy - 14, x + w - 8, cy + 14, 0x88000000);
            fontRendererObj.drawString("\u00A77[S] \u00A7fYou: gg", x + 12, cy - 10, COLOR_TEXT_WHITE);
            fontRendererObj.drawString("\u00A77[S] \u00A7fPlayer: ez", x + 12, cy, COLOR_TEXT_WHITE);
        } else if (id.contains("armor")) {
            fontRendererObj.drawStringWithShadow("\u00A7bHelmet", cx - 20, cy - 10, 0xFF55FFFF);
            fontRendererObj.drawStringWithShadow("\u00A79Chestplate", cx - 20, cy, 0xFF5555FF);
        } else {
            drawDarkPill(cx - 24, cy - 8, mod.getDisplayName(), COLOR_TEXT_WHITE);
        }
    }

    private void drawDarkPill(int x, int y, String text, int color) {
        int pw = fontRendererObj.getStringWidth(text) + 8;
        Gui.drawRect(x, y, x + pw, y + 13, 0x99000000);
        Gui.drawRect(x, y, x + pw, y + 1, 0x33FFFFFF);
        fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, color);
    }

    private void drawKeyBox(int x, int y, String k) {
        Gui.drawRect(x, y, x + 11, y + 11, 0xAA000000);
        Gui.drawRect(x, y, x + 11, y + 1, 0x55FFFFFF);
        fontRendererObj.drawString(k, x + 3, y + 2, COLOR_TEXT_WHITE);
    }

    private void drawWideKeyBox(int x, int y, int w, String k) {
        Gui.drawRect(x, y, x + w, y + 9, 0xAA000000);
        fontRendererObj.drawString(k, x + (w - fontRendererObj.getStringWidth(k)) / 2, y + 1, COLOR_TEXT_GRAY);
    }

    private void drawHeart(int x, int y, int color, boolean filled) {
        if (filled) {
            Gui.drawRect(x + 1, y, x + 4, y + 1, color);
            Gui.drawRect(x + 5, y, x + 8, y + 1, color);
            Gui.drawRect(x, y + 1, x + 9, y + 4, color);
            Gui.drawRect(x + 1, y + 4, x + 8, y + 6, color);
            Gui.drawRect(x + 2, y + 6, x + 7, y + 7, color);
            Gui.drawRect(x + 3, y + 7, x + 6, y + 8, color);
            Gui.drawRect(x + 4, y + 8, x + 5, y + 9, color);
        } else {
            // Contorno de coracao vazado elegante
            Gui.drawRect(x + 1, y, x + 4, y + 1, color);
            Gui.drawRect(x + 5, y, x + 8, y + 1, color);
            Gui.drawRect(x, y + 1, x + 1, y + 4, color);
            Gui.drawRect(x + 8, y + 1, x + 9, y + 4, color);
            Gui.drawRect(x + 1, y + 4, x + 2, y + 6, color);
            Gui.drawRect(x + 7, y + 4, x + 8, y + 6, color);
            Gui.drawRect(x + 2, y + 6, x + 3, y + 7, color);
            Gui.drawRect(x + 6, y + 6, x + 7, y + 7, color);
            Gui.drawRect(x + 3, y + 7, x + 4, y + 8, color);
            Gui.drawRect(x + 5, y + 7, x + 6, y + 8, color);
            Gui.drawRect(x + 4, y + 8, x + 5, y + 9, color);
        }
    }

    private void drawTooltip(int mouseX, int mouseY) {
        if (hoveredCardIndex < 0 || hoveredCardIndex >= filteredMods.size()) return;
        BaseMod mod = filteredMods.get(hoveredCardIndex);

        List<String> lines = new ArrayList<String>();
        lines.add("\u00A7f\u00A7l" + mod.getDisplayName());
        lines.add("\u00A77" + mod.getDescription());
        lines.add(mod.isEnabled() ? "\u00A7aStatus: Habilitado" : "\u00A7cStatus: Desabilitado");
        lines.add("\u00A78[Clique Esquerdo] Ligar/Desligar");
        lines.add("\u00A78[Clique Direito] Abrir Configuracoes");

        int maxW = 0;
        for (String s : lines) {
            int sw = fontRendererObj.getStringWidth(s);
            if (sw > maxW) maxW = sw;
        }
        maxW += 12;
        int tH = lines.size() * 11 + 6;

        int tx = mouseX + 10;
        int ty = mouseY - tH - 4;
        if (tx + maxW > width) tx = width - maxW - 4;
        if (ty < 4) ty = mouseY + 14;

        Gui.drawRect(tx - 1, ty - 1, tx + maxW + 1, ty + tH + 1, COLOR_SEARCH_BOR);
        Gui.drawRect(tx, ty, tx + maxW, ty + tH, 0xFF14151B);

        int curY = ty + 4;
        for (String s : lines) {
            fontRendererObj.drawString(s, tx + 6, curY, COLOR_TEXT_WHITE);
            curY += 11;
        }
    }

    // =========================================================================
    //  INPUT (Teclado e Mouse)
    // =========================================================================

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Dropdown de ordenacao aberto
        if (sortDropdownOpen) {
            int barX = SIDEBAR_WIDTH;
            int barW = width - SIDEBAR_WIDTH;
            int winBtnX = barX + barW - 68;
            int searchW = 190;
            int searchX = winBtnX - 18 - searchW;
            int sortW = 135;
            int sortX = searchX - 10 - sortW;
            int sortH = 26;
            int dropY = (TOP_BAR_HEIGHT - sortH) / 2 + sortH + 2;
            int itemH = 22;
            int totalH = SORT_OPTIONS.length * itemH;

            if (mouseX >= sortX && mouseX <= sortX + sortW && mouseY >= dropY && mouseY <= dropY + totalH) {
                int clicked = (mouseY - dropY) / itemH;
                if (clicked >= 0 && clicked < SORT_OPTIONS.length) {
                    selectedSort = clicked;
                    sortDropdownOpen = false;
                    applyFilterAndSort();
                    return;
                }
            }
            sortDropdownOpen = false;
            return;
        }

        // Clique na Sidebar
        if (mouseX < SIDEBAR_WIDTH) {
            int settingsY = height - NAV_ITEM_HEIGHT - 12;
            if (mouseY >= settingsY && mouseY < settingsY + NAV_ITEM_HEIGHT) {
                // Abre o HUD Editor ou configurações
                mc.displayGuiScreen(new HUDEditorScreen());
                return;
            }
            return;
        }

        // Clique na TopBar
        if (mouseY < TOP_BAR_HEIGHT) {
            int barX = SIDEBAR_WIDTH;
            int barW = width - SIDEBAR_WIDTH;

            // Botao Voltar "< Mods"
            if (mouseX >= barX + 14 && mouseX <= barX + 70) {
                Config.salvar();
                mc.displayGuiScreen(parent);
                return;
            }

            // Botao Fechar (X)
            int winBtnX = barX + barW - 68;
            if (mouseX >= winBtnX + 40 && mouseX <= winBtnX + 65) {
                Config.salvar();
                mc.displayGuiScreen(null);
                return;
            }

            // Campo de busca
            int searchW = 190;
            int searchX = winBtnX - 18 - searchW;
            int searchH = 26;
            int searchY = (TOP_BAR_HEIGHT - searchH) / 2;
            if (mouseX >= searchX && mouseX <= searchX + searchW && mouseY >= searchY && mouseY <= searchY + searchH) {
                searchFocused = true;
                return;
            } else {
                searchFocused = false;
            }

            // Botao de ordenacao
            int sortW = 135;
            int sortX = searchX - 10 - sortW;
            int sortH = 26;
            int sortY = (TOP_BAR_HEIGHT - sortH) / 2;
            if (mouseX >= sortX && mouseX <= sortX + sortW && mouseY >= sortY && mouseY <= sortY + sortH) {
                sortDropdownOpen = !sortDropdownOpen;
                return;
            }

            return;
        }

        // Clique nos Cards da Grade
        if (hoveredCardIndex >= 0 && hoveredCardIndex < filteredMods.size()) {
            BaseMod mod = filteredMods.get(hoveredCardIndex);

            int col = hoveredCardIndex % GRID_COLS;
            int row = hoveredCardIndex / GRID_COLS;
            int totalGridW = GRID_COLS * CARD_WIDTH + (GRID_COLS - 1) * CARD_GAP;
            int startX = gridAreaX + Math.max(0, (gridAreaW - totalGridW) / 2);
            int cx = startX + col * (CARD_WIDTH + CARD_GAP);
            int cy = gridAreaY + row * (CARD_HEIGHT + CARD_GAP) - (int) scrollOffset;

            // Clique no Coracao (Favorito)
            int heartX = cx + CARD_WIDTH - 18;
            int heartY = cy + CARD_PREVIEW_HEIGHT + (CARD_BOTTOM_HEIGHT - 10) / 2;
            if (mouseX >= heartX - 4 && mouseX <= heartX + 14 && mouseY >= heartY - 4 && mouseY <= heartY + 14) {
                toggleFavorite(mod);
                return;
            }

            // Clique direito = Abre ModSettingsScreen
            if (mouseButton == 1) {
                mc.displayGuiScreen(new ModSettingsScreen(this, mod));
                return;
            }

            // Clique esquerdo = Liga/Desliga mod
            if (mouseButton == 0) {
                mod.toggle();
                Config.salvar();
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // ESCAPE ou RSHIFT fecha o menu e salva
        if (keyCode == 1 || keyCode == Keyboard.KEY_RSHIFT) {
            if (searchFocused) {
                searchFocused = false;
                return;
            }
            if (sortDropdownOpen) {
                sortDropdownOpen = false;
                return;
            }
            Config.salvar();
            mc.displayGuiScreen(parent);
            return;
        }

        if (searchFocused) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (searchText.length() > 0) {
                    searchText = searchText.substring(0, searchText.length() - 1);
                    applyFilterAndSort();
                }
            } else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
                searchFocused = false;
            } else if (typedChar >= 32 && typedChar < 127) {
                searchText = searchText + typedChar;
                applyFilterAndSort();
            }
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            scrollOffset += (dWheel > 0 ? -32 : 32);
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
