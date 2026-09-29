package com.battleclient.gui;

import com.battleclient.core.Config;
import com.battleclient.core.ModManager;
import com.battleclient.mods.BaseMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends GuiScreen {
    private String selectedCategory = "ALL";
    private GuiTextField searchField;
    private int scrollOffset = 0;
    private final String[] categories = {"ALL", "HUD", "VISUAL", "GAMEPLAY", "UTILITY"};

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int modalW = 440;
        int modalX = width / 2 - modalW / 2;
        int modalY = height / 2 - 145;

        searchField = new GuiTextField(0, fontRendererObj, modalX + modalW - 130, modalY + 38, 120, 16);
        searchField.setMaxStringLength(32);
        searchField.setFocused(false);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.salvar();
    }

    @Override
    public void updateScreen() {
        searchField.updateCursorCounter();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            if (dWheel > 0) {
                scrollOffset = Math.max(0, scrollOffset - 25);
            } else {
                scrollOffset += 25;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Fundo escuro com desfoque/transparencia
        drawDefaultBackground();

        int modalW = 440;
        int modalH = 290;
        int modalX = width / 2 - modalW / 2;
        int modalY = height / 2 - modalH / 2;

        // Fundo principal da janela modal (estilo CheatBreaker / Lunar)
        Gui.drawRect(modalX, modalY, modalX + modalW, modalY + modalH, 0xF2121212);
        RenderUtil.drawBorder(modalX, modalY, modalW, modalH, 1, 0x30FFFFFF);

        // Barra de topo da janela (Top Bar)
        Gui.drawRect(modalX, modalY, modalX + modalW, modalY + 30, 0xFF191919);
        Gui.drawRect(modalX, modalY + 29, modalX + modalW, modalY + 30, 0xFF0055FF);

        // Logo / Nome do Client
        fontRendererObj.drawStringWithShadow("\u00A79\u00A7lBATTLE \u00A7fCLIENT", modalX + 12, modalY + 11, 0xFFFFFFFF);

        // Botao HUD EDITOR no topo da janela
        int hudBtnX = modalX + modalW - 125;
        int hudBtnY = modalY + 6;
        boolean hudHover = mouseX >= hudBtnX && mouseX <= hudBtnX + 90 && mouseY >= hudBtnY && mouseY <= hudBtnY + 18;
        Gui.drawRect(hudBtnX, hudBtnY, hudBtnX + 90, hudBtnY + 18, hudHover ? 0xFF0055FF : 0xFF242424);
        RenderUtil.drawBorder(hudBtnX, hudBtnY, 90, 18, 1, hudHover ? 0xFF3388FF : 0xFF383838);
        drawCenteredString(fontRendererObj, "\u00A7bHUD EDITOR", hudBtnX + 45, hudBtnY + 5, 0xFFFFFFFF);

        // Botao Fechar (X)
        int closeX = modalX + modalW - 24;
        int closeY = modalY + 7;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + 16 && mouseY >= closeY && mouseY <= closeY + 16;
        Gui.drawRect(closeX, closeY, closeX + 16, closeY + 16, closeHover ? 0xFFAA2222 : 0xFF242424);
        drawCenteredString(fontRendererObj, "\u00A77X", closeX + 8, closeY + 4, 0xFFFFFFFF);

        // Sub-Barra de Categorias (Pilulas / Abas)
        int catStartX = modalX + 12;
        int catY = modalY + 38;
        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            int cw = fontRendererObj.getStringWidth(cat) + 12;
            boolean isSelected = cat.equalsIgnoreCase(selectedCategory);
            boolean isHovered = mouseX >= catStartX && mouseX <= catStartX + cw && mouseY >= catY && mouseY <= catY + 16;

            int catBg = isSelected ? 0xFF0055FF : (isHovered ? 0xFF2A2A2A : 0xFF1C1C1C);
            Gui.drawRect(catStartX, catY, catStartX + cw, catY + 16, catBg);
            RenderUtil.drawBorder(catStartX, catY, cw, 16, 1, isSelected ? 0xFF3388FF : 0xFF282828);

            drawCenteredString(fontRendererObj, isSelected ? "\u00A7f\u00A7l" + cat : "\u00A77" + cat, catStartX + cw / 2, catY + 4, 0xFFFFFFFF);
            catStartX += cw + 4;
        }

        // Barra de Pesquisa
        searchField.xPosition = modalX + modalW - 130;
        searchField.yPosition = modalY + 38;
        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused()) {
            fontRendererObj.drawString("\u00A78Pesquisar...", searchField.xPosition + 6, searchField.yPosition + 4, 0xFF888888);
        }

        // Linha divisoria antes da lista
        Gui.drawRect(modalX + 10, modalY + 59, modalX + modalW - 10, modalY + 60, 0xFF222222);

        // Area de Grid de Mods (2 Colunas perfeitas dentro da janela modal)
        List<BaseMod> filteredMods = getFilteredMods();

        int listY = modalY + 66;
        int cardWidth = 200;
        int cardHeight = 36;
        int spacing = 6;
        int gridStartX = modalX + 12;
        int maxVisibleY = modalY + modalH - 8;

        int row = 0;
        int col = 0;

        for (BaseMod mod : filteredMods) {
            int cardX = gridStartX + (col * (cardWidth + spacing + 6));
            int cardY = listY + (row * (cardHeight + spacing)) - scrollOffset;

            // Renderiza apenas se estiver dentro da area visivel da janela
            if (cardY + cardHeight > listY && cardY < maxVisibleY) {
                boolean isEnabled = mod.isEnabled();
                boolean cardHover = mouseX >= cardX && mouseX <= cardX + cardWidth && mouseY >= cardY && mouseY <= cardY + cardHeight;

                // Fundo do Card
                int cardBg = isEnabled ? 0xFF1C1C1C : 0xFF161616;
                if (cardHover) cardBg = isEnabled ? 0xFF222222 : 0xFF1A1A1A;
                Gui.drawRect(cardX, cardY, cardX + cardWidth, cardY + cardHeight, cardBg);

                // Borda e barra de destaque
                RenderUtil.drawBorder(cardX, cardY, cardWidth, cardHeight, 1, isEnabled ? 0x600055FF : 0xFF252525);
                Gui.drawRect(cardX, cardY, cardX + 2, cardY + cardHeight, isEnabled ? 0xFF0055FF : 0xFF353535);

                // Nome do Mod
                fontRendererObj.drawStringWithShadow(mod.getDisplayName(), cardX + 8, cardY + 7, isEnabled ? 0xFFFFFFFF : 0xFF999999);

                // Categoria e descricao sutil
                String categoryLabel = "\u00A78" + mod.getCategory();
                fontRendererObj.drawString(categoryLabel, cardX + 8, cardY + 20, 0xFF777777);

                // Botao de Configuracoes (Gear Icon)
                int gearX = cardX + cardWidth - 52;
                int gearY = cardY + 10;
                boolean gearHover = mouseX >= gearX && mouseX <= gearX + 16 && mouseY >= gearY && mouseY <= gearY + 16;
                RenderUtil.drawGearIcon(gearX, gearY, gearHover);

                // Toggle Switch moderno estilo iOS / Lunar Client
                int switchX = cardX + cardWidth - 30;
                int switchY = cardY + 12;
                RenderUtil.drawToggleSwitch(switchX, switchY, isEnabled);
            }

            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private List<BaseMod> getFilteredMods() {
        List<BaseMod> list = new ArrayList<>();
        String query = searchField.getText().toLowerCase().trim();

        for (BaseMod mod : ModManager.getInstance().getMods()) {
            boolean matchesCat = selectedCategory.equalsIgnoreCase("ALL") || mod.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = query.isEmpty() || mod.getDisplayName().toLowerCase().contains(query) || mod.getDescription().toLowerCase().contains(query);

            if (matchesCat && matchesSearch) {
                list.add(mod);
            }
        }
        return list;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        int modalW = 440;
        int modalH = 290;
        int modalX = width / 2 - modalW / 2;
        int modalY = height / 2 - modalH / 2;

        // Clique no Botao HUD EDITOR
        int hudBtnX = modalX + modalW - 125;
        int hudBtnY = modalY + 6;
        if (mouseX >= hudBtnX && mouseX <= hudBtnX + 90 && mouseY >= hudBtnY && mouseY <= hudBtnY + 18) {
            mc.displayGuiScreen(new HUDEditorScreen());
            return;
        }

        // Clique no Botao Fechar (X)
        int closeX = modalX + modalW - 24;
        int closeY = modalY + 7;
        if (mouseX >= closeX && mouseX <= closeX + 16 && mouseY >= closeY && mouseY <= closeY + 16) {
            Config.salvar();
            mc.displayGuiScreen(null);
            return;
        }

        // Clique nas Abas de Categorias
        int catStartX = modalX + 12;
        int catY = modalY + 38;
        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            int cw = fontRendererObj.getStringWidth(cat) + 12;
            if (mouseX >= catStartX && mouseX <= catStartX + cw && mouseY >= catY && mouseY <= catY + 16) {
                selectedCategory = categories[i];
                scrollOffset = 0;
                return;
            }
            catStartX += cw + 4;
        }

        // Clique nos Cards de Mods
        List<BaseMod> filteredMods = getFilteredMods();
        int listY = modalY + 66;
        int cardWidth = 200;
        int cardHeight = 36;
        int spacing = 6;
        int gridStartX = modalX + 12;
        int maxVisibleY = modalY + modalH - 8;

        int row = 0;
        int col = 0;

        for (BaseMod mod : filteredMods) {
            int cardX = gridStartX + (col * (cardWidth + spacing + 6));
            int cardY = listY + (row * (cardHeight + spacing)) - scrollOffset;

            if (cardY + cardHeight > listY && cardY < maxVisibleY) {
                int gearX = cardX + cardWidth - 52;
                int gearY = cardY + 10;
                int switchX = cardX + cardWidth - 30;
                int switchY = cardY + 12;

                // Engrenagem (Abrir Settings)
                if (mouseX >= gearX && mouseX <= gearX + 16 && mouseY >= gearY && mouseY <= gearY + 16) {
                    mc.displayGuiScreen(new ModSettingsScreen(this, mod));
                    return;
                }

                // Switch ou clique no card
                if (mouseX >= switchX && mouseX <= switchX + 24 && mouseY >= switchY && mouseY <= switchY + 12 ||
                    mouseX >= cardX && mouseX <= cardX + cardWidth - 56 && mouseY >= cardY && mouseY <= cardY + cardHeight) {
                    mod.toggle();
                    Config.salvar();
                    return;
                }
            }

            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
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
}
