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
        searchField = new GuiTextField(0, fontRendererObj, width / 2 - 100, 48, 200, 18);
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
        drawDefaultBackground();

        // Top Banner (Azul forte e Preto)
        Gui.drawRect(0, 0, width, 40, 0xF0000000);
        Gui.drawRect(0, 39, width, 40, 0xFF0055FF);

        // Logo / Nome
        fontRendererObj.drawStringWithShadow("§9§lBATTLE §f§lCLIENT", 20, 15, 0xFFFFFFFF);

        // Botão HUD Editor no topo à direita
        int hudBtnX = width - 130;
        int hudBtnY = 10;
        boolean hudHover = mouseX >= hudBtnX && mouseX <= hudBtnX + 110 && mouseY >= hudBtnY && mouseY <= hudBtnY + 20;
        Gui.drawRect(hudBtnX, hudBtnY, hudBtnX + 110, hudBtnY + 20, hudHover ? 0xFF0055FF : 0xFF151515);
        Gui.drawRect(hudBtnX, hudBtnY, hudBtnX + 110, hudBtnY + 1, 0xFF0055FF);
        drawCenteredString(fontRendererObj, "§f§lHUD EDITOR", hudBtnX + 55, hudBtnY + 6, 0xFFFFFFFF);

        // Abas de categoria no centro do topo
        int catStartX = width / 2 - (categories.length * 60) / 2;
        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            int cx = catStartX + (i * 60);
            boolean isSelected = cat.equalsIgnoreCase(selectedCategory);
            boolean isHovered = mouseX >= cx && mouseX <= cx + 55 && mouseY >= 12 && mouseY <= 28;

            if (isSelected) {
                Gui.drawRect(cx, 12, cx + 55, 28, 0xFF0055FF);
            } else if (isHovered) {
                Gui.drawRect(cx, 12, cx + 55, 28, 0x500055FF);
            }

            drawCenteredString(fontRendererObj, isSelected ? "§f§l" + cat : "§7" + cat, cx + 27, 16, 0xFFFFFFFF);
        }

        // Barra de pesquisa
        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused()) {
            drawCenteredString(fontRendererObj, "§8Pesquisar mod...", width / 2, 53, 0xFF888888);
        }

        // Filtra mods
        List<BaseMod> filteredMods = getFilteredMods();

        // Área de exibição dos mods (Grid / Lista com 2 colunas)
        int listY = 75;
        int cardWidth = 220;
        int cardHeight = 44;
        int spacing = 10;
        int totalCols = Math.max(1, Math.min(2, width / (cardWidth + spacing)));
        int gridStartX = width / 2 - ((cardWidth * totalCols) + (spacing * (totalCols - 1))) / 2;

        int row = 0;
        int col = 0;

        for (BaseMod mod : filteredMods) {
            int cardX = gridStartX + (col * (cardWidth + spacing));
            int cardY = listY + (row * (cardHeight + spacing)) - scrollOffset;

            // Só desenha se estiver visível na janela
            if (cardY + cardHeight > 70 && cardY < height - 10) {
                boolean isEnabled = mod.isEnabled();
                boolean cardHover = mouseX >= cardX && mouseX <= cardX + cardWidth && mouseY >= cardY && mouseY <= cardY + cardHeight;

                // Fundo do card
                Gui.drawRect(cardX, cardY, cardX + cardWidth, cardY + cardHeight, isEnabled ? 0xB0080808 : 0x80050505);
                Gui.drawRect(cardX, cardY, cardX + 3, cardY + cardHeight, isEnabled ? 0xFF0055FF : 0xFF333333);

                if (cardHover) {
                    Gui.drawRect(cardX, cardY, cardX + cardWidth, cardY + 1, 0x500055FF);
                }

                // Nome do mod e categoria
                fontRendererObj.drawStringWithShadow(mod.getDisplayName(), cardX + 10, cardY + 8, isEnabled ? 0xFFFFFFFF : 0xFFAAAAAA);
                fontRendererObj.drawStringWithShadow("§8[§b" + mod.getCategory() + "§8]", cardX + 10 + fontRendererObj.getStringWidth(mod.getDisplayName()) + 5, cardY + 8, 0xFF888888);

                // Descrição curta
                String desc = mod.getDescription();
                if (fontRendererObj.getStringWidth(desc) > 135) {
                    desc = fontRendererObj.trimStringToWidth(desc, 130) + "...";
                }
                fontRendererObj.drawStringWithShadow("§7" + desc, cardX + 10, cardY + 22, 0xFF888888);

                // Botão de Engrenagem (Settings)
                int gearX = cardX + cardWidth - 54;
                int gearY = cardY + 12;
                boolean gearHover = mouseX >= gearX && mouseX <= gearX + 18 && mouseY >= gearY && mouseY <= gearY + 18;
                Gui.drawRect(gearX, gearY, gearX + 18, gearY + 18, gearHover ? 0xFF0055FF : 0xFF222222);
                drawCenteredString(fontRendererObj, "§f⚙", gearX + 9, gearY + 5, 0xFFFFFFFF);

                // Botão Switch ON/OFF
                int switchX = cardX + cardWidth - 32;
                int switchY = cardY + 12;
                boolean switchHover = mouseX >= switchX && mouseX <= switchX + 26 && mouseY >= switchY && mouseY <= switchY + 18;
                Gui.drawRect(switchX, switchY, switchX + 26, switchY + 18, isEnabled ? 0xFF0055FF : (switchHover ? 0xFF333333 : 0xFF181818));
                drawCenteredString(fontRendererObj, isEnabled ? "§a✓" : "§c✕", switchX + 13, switchY + 5, 0xFFFFFFFF);
            }

            col++;
            if (col >= totalCols) {
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

        // Clique no botão HUD Editor
        int hudBtnX = width - 130;
        int hudBtnY = 10;
        if (mouseX >= hudBtnX && mouseX <= hudBtnX + 110 && mouseY >= hudBtnY && mouseY <= hudBtnY + 20) {
            mc.displayGuiScreen(new HUDEditorScreen());
            return;
        }

        // Clique nas categorias
        int catStartX = width / 2 - (categories.length * 60) / 2;
        for (int i = 0; i < categories.length; i++) {
            int cx = catStartX + (i * 60);
            if (mouseX >= cx && mouseX <= cx + 55 && mouseY >= 12 && mouseY <= 28) {
                selectedCategory = categories[i];
                scrollOffset = 0;
                return;
            }
        }

        // Clique nos cards de mods (toggle ou engrenagem)
        List<BaseMod> filteredMods = getFilteredMods();
        int listY = 75;
        int cardWidth = 220;
        int cardHeight = 44;
        int spacing = 10;
        int totalCols = Math.max(1, Math.min(2, width / (cardWidth + spacing)));
        int gridStartX = width / 2 - ((cardWidth * totalCols) + (spacing * (totalCols - 1))) / 2;

        int row = 0;
        int col = 0;

        for (BaseMod mod : filteredMods) {
            int cardX = gridStartX + (col * (cardWidth + spacing));
            int cardY = listY + (row * (cardHeight + spacing)) - scrollOffset;

            if (cardY + cardHeight > 70 && cardY < height - 10) {
                int gearX = cardX + cardWidth - 54;
                int gearY = cardY + 12;
                int switchX = cardX + cardWidth - 32;
                int switchY = cardY + 12;

                // Engrenagem clicada
                if (mouseX >= gearX && mouseX <= gearX + 18 && mouseY >= gearY && mouseY <= gearY + 18) {
                    mc.displayGuiScreen(new ModSettingsScreen(this, mod));
                    return;
                }

                // Switch ou card clicado
                if (mouseX >= switchX && mouseX <= switchX + 26 && mouseY >= switchY && mouseY <= switchY + 18) {
                    mod.toggle();
                    Config.salvar();
                    return;
                }
            }

            col++;
            if (col >= totalCols) {
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
        super.keyTyped(typedChar, keyCode);
    }
}
