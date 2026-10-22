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
 * ModMenuScreen - Estilo Lunar Client
 *
 * Layout (700x450):
 *  ┌─────────────────────────────────────────────────────────────────┐
 *  │  [LOGO 48px]  BATTLE CLIENT v1.0       [Search.........] [X]   │  <- Topbar 54px
 *  ├──────────────────────────────────────────────────────────────────┤
 *  │  [ALL] [HUD] [VISUAL] [GAMEPLAY] [UTILITY] [PERFORMANCE] [OTHER]│  <- Tab bar 34px
 *  ├──────────────────────────────────────────────────────────────────┤
 *  │                                                                  │
 *  │   [Mod Row]  ModName  Desc             [gear] [  toggle  ]       │  <- rows 52px each
 *  │   [Mod Row]  ...                                                  │
 *  │                                                                  │
 *  ├──────────────────────────────────────────────────────────────────┤
 *  │  Battle Client v1.0 | 1.8.9 Forge          [HUD EDITOR]         │  <- Footer 26px
 *  └─────────────────────────────────────────────────────────────────┘
 */
public class ModMenuScreen extends GuiScreen {

    private static final ResourceLocation LOGO_TEX = new ResourceLocation("battleclient", "textures/gui/logo.png");

    // ── Paleta Lunar Client ─────────────────────────────────────────────────
    private static final int BG          = 0xFF101010; // fundo principal
    private static final int TOPBAR      = 0xFF161616; // barra superior
    private static final int TABBAR      = 0xFF121212; // barra de abas
    private static final int ROW_EVEN    = 0xFF181818; // linha par
    private static final int ROW_ODD     = 0xFF141414; // linha impar
    private static final int ROW_HOVER   = 0xFF1F1F1F; // hover
    private static final int DIVIDER     = 0xFF272727; // separador
    private static final int TAB_BG      = 0xFF1A1A1A; // aba inativa
    private static final int TAB_ACTIVE  = 0xFF0055FF; // aba ativa (azul lunar)
    private static final int TAB_HOVER   = 0xFF223366; // aba hover
    private static final int TOG_ON      = 0xFF0055FF; // toggle ligado
    private static final int TOG_OFF     = 0xFF303030; // toggle desligado
    private static final int TOG_KNOB    = 0xFFFFFFFF; // bolinha
    private static final int TEXT        = 0xFFE8E8E8;
    private static final int TEXT_DIM    = 0xFF6A6A6A;
    private static final int TEXT_CAT    = 0xFF4488FF;
    private static final int FOOTER      = 0xFF0E0E0E;
    private static final int ACCENT      = 0xFF0055FF;
    private static final int CLOSE_HOVER = 0xFFAA2222;
    private static final int SEARCH_BG   = 0xFF1C1C1C;
    private static final int SEARCH_BOR  = 0xFF2C2C2C;

    // ── Dimensoes ───────────────────────────────────────────────────────────
    private static final int W           = 680;
    private static final int H           = 430;
    private static final int TOPBAR_H    = 54;
    private static final int TABBAR_H    = 34;
    private static final int FOOTER_H    = 26;
    private static final int ROW_H       = 52;
    private static final int LOGO_SIZE   = 40;

    // ── Estado ──────────────────────────────────────────────────────────────
    private String selectedTab = "ALL";
    private GuiTextField searchField;
    private int scrollOffset  = 0;
    private int maxScroll     = 0;

    private static final String[] TABS = {"ALL", "HUD", "VISUAL", "GAMEPLAY", "UTILITY", "PERFORMANCE", "OTHER"};

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        int mx = width  / 2 - W / 2;

        // Search field na topbar
        int sfX = mx + LOGO_SIZE + 180;
        int sfW = 180;
        searchField = new GuiTextField(0, fontRendererObj, sfX + 10, topbarY() + 18, sfW - 12, 14);
        searchField.setMaxStringLength(40);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setFocused(false);
        searchField.setTextColor(0xFFDDDDDD);
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
        if (dWheel != 0) {
            scrollOffset += (dWheel > 0 ? -24 : 24);
            scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, maxScroll)));
        }
    }

    // ── Posicoes calculadas ──────────────────────────────────────────────────
    private int modalX()    { return width  / 2 - W / 2; }
    private int modalY()    { return height / 2 - H / 2; }
    private int topbarY()   { return modalY(); }
    private int tabbarY()   { return modalY() + TOPBAR_H; }
    private int contentY()  { return modalY() + TOPBAR_H + TABBAR_H; }
    private int footerY()   { return modalY() + H - FOOTER_H; }
    private int contentH()  { return H - TOPBAR_H - TABBAR_H - FOOTER_H; }

    // ── Desenho principal ────────────────────────────────────────────────────
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int mx = modalX(), my = modalY();

        // Overlay escuro
        drawGradientRect(0, 0, width, height, 0xBB000000, 0xBB000000);

        // ── Sombra do modal ─────────────────────────────────────────────────
        Gui.drawRect(mx - 3, my - 3, mx + W + 3, my + H + 3, 0x55000000);

        // ── Fundo principal ─────────────────────────────────────────────────
        Gui.drawRect(mx, my, mx + W, my + H, BG);

        drawTopBar(mx, my, mouseX, mouseY);
        drawTabBar(mx, mouseX, mouseY);
        drawModList(mx, mouseX, mouseY);
        drawFooter(mx, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // ── TOPBAR ───────────────────────────────────────────────────────────────
    private void drawTopBar(int mx, int my, int mouseX, int mouseY) {
        Gui.drawRect(mx, my, mx + W, my + TOPBAR_H, TOPBAR);
        // Linha separadora azul na base da topbar
        Gui.drawRect(mx, my + TOPBAR_H - 1, mx + W, my + TOPBAR_H, ACCENT);

        // Logo
        drawLogo(mx + 8, my + 7, LOGO_SIZE, LOGO_SIZE);

        // Nome + versao
        fontRendererObj.drawStringWithShadow("\u00A7fBATTLE \u00A79CLIENT", mx + LOGO_SIZE + 16, my + 12, TEXT);
        fontRendererObj.drawString("\u00A78v1.0  \u2022  Forge 1.8.9", mx + LOGO_SIZE + 16, my + 25, TEXT_DIM);

        // Campo de busca
        int sfX = mx + W - 220;
        int sfY = my + 14;
        int sfW = 168;
        int sfH = 24;
        // Fundo do campo
        Gui.drawRect(sfX, sfY, sfX + sfW, sfY + sfH, SEARCH_BG);
        Gui.drawRect(sfX,      sfY,      sfX + sfW, sfY + 1,      SEARCH_BOR);
        Gui.drawRect(sfX,      sfY + sfH - 1, sfX + sfW, sfY + sfH, SEARCH_BOR);
        Gui.drawRect(sfX,      sfY,      sfX + 1,      sfY + sfH,  SEARCH_BOR);
        Gui.drawRect(sfX + sfW - 1, sfY, sfX + sfW,    sfY + sfH,  SEARCH_BOR);
        // Icone lupa
        fontRendererObj.drawString("\u00A78\uD83D\uDD0D", sfX + 5, sfY + 8, TEXT_DIM);
        // Placeholder
        if (searchField != null) {
            searchField.xPosition = sfX + 18;
            searchField.yPosition = sfY + 6;
            searchField.width = sfW - 22;
            searchField.drawTextBox();
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                fontRendererObj.drawString("\u00A78Pesquisar...", sfX + 20, sfY + 8, TEXT_DIM);
            }
        }

        // Botao fechar (X)
        int clX = mx + W - 24;
        int clY = my + 8;
        boolean clHov = isIn(mouseX, mouseY, clX, clY, 18, 18);
        Gui.drawRect(clX, clY, clX + 18, clY + 18, clHov ? CLOSE_HOVER : 0xFF1C1C1C);
        drawCenteredString(fontRendererObj, "\u00A7f\u00D7", clX + 9, clY + 5, TEXT);
    }

    // ── TABBAR ───────────────────────────────────────────────────────────────
    private void drawTabBar(int mx, int mouseX, int mouseY) {
        int ty = tabbarY();
        Gui.drawRect(mx, ty, mx + W, ty + TABBAR_H, TABBAR);
        Gui.drawRect(mx, ty + TABBAR_H - 1, mx + W, ty + TABBAR_H, DIVIDER);

        int tabX = mx + 6;
        for (String tab : TABS) {
            int tw = fontRendererObj.getStringWidth(tab) + 20;
            boolean active = tab.equalsIgnoreCase(selectedTab);
            boolean hov    = isIn(mouseX, mouseY, tabX, ty + 4, tw, TABBAR_H - 8);

            int tabBg = active ? TAB_ACTIVE : (hov ? TAB_HOVER : TAB_BG);
            Gui.drawRect(tabX, ty + 4, tabX + tw, ty + TABBAR_H - 4, tabBg);

            // Underline na aba ativa
            if (active) {
                Gui.drawRect(tabX, ty + TABBAR_H - 4, tabX + tw, ty + TABBAR_H - 2, 0xFF66AAFF);
            }

            int textColor = active ? 0xFFFFFFFF : (hov ? 0xFFBBBBBB : 0xFF666666);
            fontRendererObj.drawStringWithShadow(tab, tabX + 10, ty + 11, textColor);
            tabX += tw + 4;
        }
    }

    // ── LISTA DE MODS ────────────────────────────────────────────────────────
    private void drawModList(int mx, int mouseX, int mouseY) {
        List<BaseMod> mods = getFilteredMods();
        int cy = contentY();
        int ch = contentH();
        int rowCount = mods.size();
        maxScroll = Math.max(0, rowCount * ROW_H - ch);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        // Clip via GL scissor
        int scX = mx;
        int scY = cy;
        int scW = W;
        int scH = ch;
        enableScissor(scX, scY, scW, scH);

        for (int i = 0; i < mods.size(); i++) {
            BaseMod mod  = mods.get(i);
            int ry       = cy + i * ROW_H - scrollOffset;

            if (ry + ROW_H < cy || ry > cy + ch) continue;

            boolean enabled = mod.isEnabled();
            boolean hov     = isIn(mouseX, mouseY, mx, ry, W, ROW_H);

            // Fundo da linha
            int rowBg = hov ? ROW_HOVER : (i % 2 == 0 ? ROW_EVEN : ROW_ODD);
            Gui.drawRect(mx, ry, mx + W, ry + ROW_H, rowBg);

            // Barra lateral esquerda (azul se habilitado)
            Gui.drawRect(mx, ry, mx + 3, ry + ROW_H, enabled ? ACCENT : 0xFF2A2A2A);

            // Linha divisoria
            Gui.drawRect(mx, ry + ROW_H - 1, mx + W, ry + ROW_H, DIVIDER);

            // Nome do mod (tamanho normal - 1.0x)
            String nameColor = enabled ? "\u00A7f" : "\u00A78";
            fontRendererObj.drawStringWithShadow(nameColor + mod.getDisplayName(), mx + 16, ry + 11, TEXT);

            // Descricao
            String desc = mod.getDescription();
            if (desc != null && !desc.isEmpty()) {
                if (desc.length() > 60) desc = desc.substring(0, 58) + "..";
                fontRendererObj.drawString("\u00A78" + desc, mx + 16, ry + 25, TEXT_DIM);
            }

            // Categoria badge
            String cat = mod.getCategory();
            int catW = fontRendererObj.getStringWidth(cat) + 10;
            int catX = mx + 310;
            int catY = ry + 18;
            Gui.drawRect(catX, catY, catX + catW, catY + 13, 0xFF1A2244);
            fontRendererObj.drawString("\u00A79" + cat, catX + 5, catY + 3, TEXT_CAT);

            // Botao Settings (engrenagem)
            int gearX = mx + W - 100;
            int gearY = ry + ROW_H / 2 - 10;
            boolean gearHov = isIn(mouseX, mouseY, gearX, gearY, 22, 20);
            Gui.drawRect(gearX, gearY, gearX + 22, gearY + 20, gearHov ? 0xFF223377 : 0xFF1A1A1A);
            Gui.drawRect(gearX, gearY, gearX + 22, gearY + 1, gearHov ? ACCENT : DIVIDER);
            Gui.drawRect(gearX, gearY + 19, gearX + 22, gearY + 20, gearHov ? ACCENT : DIVIDER);
            fontRendererObj.drawString("\u00A77\u2699", gearX + 5, gearY + 6, gearHov ? 0xFFAABBFF : 0xFF666666);

            // Toggle switch estilo iOS
            int toggleX = mx + W - 66;
            int toggleY = ry + ROW_H / 2 - 8;
            drawToggle(toggleX, toggleY, enabled);
        }

        disableScissor();

        // Scrollbar
        if (maxScroll > 0) {
            int sbX = mx + W - 4;
            int sbH = ch;
            int sbY = cy;
            // Track
            Gui.drawRect(sbX, sbY, sbX + 3, sbY + sbH, 0xFF1A1A1A);
            // Thumb
            int thumbH = Math.max(20, sbH * ch / (ch + maxScroll));
            int thumbY = sbY + (int)((long)scrollOffset * (sbH - thumbH) / maxScroll);
            Gui.drawRect(sbX, thumbY, sbX + 3, thumbY + thumbH, 0xFF0044BB);
        }

        // Sem mods encontrados
        if (mods.isEmpty()) {
            drawCenteredString(fontRendererObj, "\u00A78Nenhum mod encontrado", mx + W / 2, cy + ch / 2 - 4, TEXT_DIM);
        }
    }

    // ── FOOTER ───────────────────────────────────────────────────────────────
    private void drawFooter(int mx, int mouseX, int mouseY) {
        int fy = footerY();
        Gui.drawRect(mx, fy, mx + W, fy + FOOTER_H, FOOTER);
        Gui.drawRect(mx, fy, mx + W, fy + 1, DIVIDER);

        fontRendererObj.drawString("\u00A78Battle Client v1.0  \u2022  Forge 1.8.9",
                mx + 10, fy + 8, TEXT_DIM);

        // Botao HUD EDITOR
        int hudX = mx + W - 100;
        int hudY = fy + 4;
        boolean hudHov = isIn(mouseX, mouseY, hudX, hudY, 92, 18);
        Gui.drawRect(hudX, hudY, hudX + 92, hudY + 18, hudHov ? ACCENT : 0xFF1A2244);
        drawCenteredString(fontRendererObj, "\u00A7fHUD EDITOR", hudX + 46, hudY + 5, TEXT);
    }

    // ── Toggle switch iOS-style ──────────────────────────────────────────────
    private void drawToggle(int tx, int ty, boolean on) {
        int tw = 44, th = 18;
        // Fundo pill
        int bg = on ? TOG_ON : TOG_OFF;
        // Desenhar fundo arredondado simulado (sem rounded rects nativos)
        Gui.drawRect(tx + 2, ty,      tx + tw - 2, ty + th,      bg);
        Gui.drawRect(tx,     ty + 2,  tx + tw,     ty + th - 2,  bg);
        // Borda sutil
        int bor = on ? 0xFF0044CC : 0xFF444444;
        Gui.drawRect(tx + 2, ty,           tx + tw - 2, ty + 1,            bor);
        Gui.drawRect(tx + 2, ty + th - 1,  tx + tw - 2, ty + th,           bor);
        Gui.drawRect(tx,     ty + 2,       tx + 1,      ty + th - 2,       bor);
        Gui.drawRect(tx + tw - 1, ty + 2,  tx + tw,     ty + th - 2,       bor);
        // Knob
        int knobX = on ? (tx + tw - th + 2) : (tx + 2);
        int knobSize = th - 4;
        Gui.drawRect(knobX, ty + 2, knobX + knobSize, ty + 2 + knobSize, TOG_KNOB);
        // Sombra na knob
        Gui.drawRect(knobX, ty + 2 + knobSize, knobX + knobSize, ty + 3 + knobSize, 0x44000000);
        // Texto ON/OFF
        String lbl = on ? "\u00A7fON" : "\u00A78OFF";
        if (on) {
            fontRendererObj.drawString(lbl, tx + 5, ty + 5, 0xFFFFFFFF);
        } else {
            fontRendererObj.drawString(lbl, tx + knobSize + 8, ty + 5, 0xFF666666);
        }
    }

    private void drawLogo(int lx, int ly, int lw, int lh) {
        try {
            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mc.getTextureManager().bindTexture(LOGO_TEX);
            Gui.drawModalRectWithCustomSizedTexture(lx, ly, 0, 0, lw, lh, lw, lh);
            GlStateManager.disableBlend();
        } catch (Exception e) {
            Gui.drawRect(lx, ly, lx + lw, ly + lh, ACCENT);
            fontRendererObj.drawStringWithShadow("\u00A7fBC", lx + lw / 2 - 4, ly + lh / 2 - 4, 0xFFFFFFFF);
        }
    }

    // ── Scissor (clip) ──────────────────────────────────────────────────────
    private void enableScissor(int x, int y, int w, int h) {
        double scaleX = mc.displayWidth  / (double) width;
        double scaleY = mc.displayHeight / (double) height;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (int)(x * scaleX),
            (int)(mc.displayHeight - (y + h) * scaleY),
            (int)(w * scaleX),
            (int)(h * scaleY)
        );
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private boolean isIn(int mx, int my, int rx, int ry, int rw, int rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
    }

    private List<BaseMod> getFilteredMods() {
        List<BaseMod> list = new ArrayList<BaseMod>();
        String query = (searchField != null) ? searchField.getText().toLowerCase().trim() : "";
        for (BaseMod mod : ModManager.getInstance().getMods()) {
            boolean cat    = selectedTab.equalsIgnoreCase("ALL") || mod.getCategory().equalsIgnoreCase(selectedTab);
            boolean search = query.isEmpty()
                    || mod.getDisplayName().toLowerCase().contains(query)
                    || mod.getDescription().toLowerCase().contains(query);
            if (cat && search) list.add(mod);
        }
        return list;
    }

    // ── Mouse click ─────────────────────────────────────────────────────────
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, mouseButton);

        int mx = modalX(), my = modalY();

        // Fechar
        int clX = mx + W - 24, clY = my + 8;
        if (isIn(mouseX, mouseY, clX, clY, 18, 18)) {
            Config.salvar();
            mc.displayGuiScreen(null);
            return;
        }

        // HUD Editor (footer)
        int hudX = mx + W - 100, hudY = footerY() + 4;
        if (isIn(mouseX, mouseY, hudX, hudY, 92, 18)) {
            mc.displayGuiScreen(new HUDEditorScreen());
            return;
        }

        // Tabs
        if (isIn(mouseX, mouseY, mx, tabbarY(), W, TABBAR_H)) {
            int tabX = mx + 6;
            for (String tab : TABS) {
                int tw = fontRendererObj.getStringWidth(tab) + 20;
                if (isIn(mouseX, mouseY, tabX, tabbarY() + 4, tw, TABBAR_H - 8)) {
                    selectedTab = tab;
                    scrollOffset = 0;
                    return;
                }
                tabX += tw + 4;
            }
        }

        // Linhas de mod
        List<BaseMod> mods = getFilteredMods();
        int cy = contentY();
        int ch = contentH();

        for (int i = 0; i < mods.size(); i++) {
            BaseMod mod = mods.get(i);
            int ry = cy + i * ROW_H - scrollOffset;
            if (ry + ROW_H < cy || ry > cy + ch) continue;
            if (!isIn(mouseX, mouseY, mx, ry, W, ROW_H)) continue;

            // Gear / settings
            int gearX = mx + W - 100, gearY = ry + ROW_H / 2 - 10;
            if (isIn(mouseX, mouseY, gearX, gearY, 22, 20)) {
                mc.displayGuiScreen(new ModSettingsScreen(this, mod));
                return;
            }

            // Toggle
            int toggleX = mx + W - 66, toggleY = ry + ROW_H / 2 - 8;
            if (isIn(mouseX, mouseY, toggleX, toggleY, 44, 18)) {
                mod.toggle();
                Config.salvar();
                return;
            }

            // Clique em qualquer outra parte da linha = toggle
            if (mouseX < gearX - 10) {
                mod.toggle();
                Config.salvar();
            }
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
