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
        searchField = new GuiTextField(0, fontRendererObj, width / 2 - 100, 38, 200, 16);
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

        // Barra Superior Principal (Estilo CheatBreaker / Lunar)
        Gui.drawRect(0, 0, width, 32, 0xF5101010);
        Gui.drawRect(0, 31, width, 32, 0xFF0055FF);

        // Branding
        fontRendererObj.drawStringWithShadow("§9§lBATTLE §f§lCLIENT", 16, 12, 0xFFFFFFFF);

        // Abas de categorias centralizadas no topo
        int catStartX = width / 2 - (categories.length * 60) / 2;
        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            int cx = catStartX + (i * 60);
            boolean isSelected = cat.equalsIgnoreCase(selectedCategory);
            boolean isHovered = mouseX >= cx && mouseX <= cx + 55 && mouseY >= 8 && mouseY <= 24;

            if (isSelected) {
                Gui.drawRect(cx, 8, cx + 55, 24, 0xFF0055FF);
            } else if (isHovered) {
                Gui.drawRect(cx, 8, cx + 55, 24, 0x400055FF);
            }

            drawCenteredString(fontRendererObj, isSelected ? "§f§l" + cat : "§7" + cat, cx + 27, 12, 0xFFFFFFFF);
        }

        // Botão HUD EDITOR destacado no topo direito
        int hudBtnX = width - 135;
        int hudBtnY = 6;
        boolean hudHover = mouseX >= hudBtnX && mouseX <= hudBtnX + 120 && mouseY >= hudBtnY && mouseY <= hudBtnY + 20;
        Gui.drawRect(hudBtnX, hudBtnY, hudBtnX + 120, hudBtnY + 20, hudHover ? 0xFF0077FF : 0xFF1E1E1E);
        Gui.drawRect(hudBtnX, hudBtnY, hudBtnX + 120, hudBtnY + 1, 0xFF0055FF);
        drawCenteredString(fontRendererObj, "§b§l[✥] HUD EDITOR", hudBtnX + 60, hudBtnY + 6, 0xFFFFFFFF);

        // Barra de pesquisa
        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused()) {
            drawCenteredString(fontRendererObj, "§8Pesquisar mod...", width / 2, 42, 0xFF888888);
        }

        // Lista de mods
        List<BaseMod> filteredMods = getFilteredMods();

        int listY = 64;
        int cardWidth = 230;
        int cardHeight = 44;
        int spacing = 8;
        int totalCols = Math.max(1, Math.min(2, width / (cardWidth + spacing)));
        int gridStartX = width / 2 - ((cardWidth * totalCols) + (spacing * (totalCols - 1))) / 2;

        int row = 0;
        int col = 0;

        for (BaseMod mod : filteredMods) {
            int cardX = gridStartX + (col * (cardWidth + spacing));
            int cardY = listY + (row * (cardHeight + spacing)) - scrollOffset;

            // Só desenha se estiver dentro dos limites da tela visível
            if (cardY + cardHeight > 58 && cardY < height - 10) {
                boolean isEnabled = mod.isEnabled();
                boolean cardHover = mouseX >= cardX && mouseX <= cardX + cardWidth && mouseY >= cardY && mouseY <= cardY + cardHeight;

                // Fundo do card
                Gui.drawRect(cardX, cardY, cardX + cardWidth, cardY + cardHeight, isEnabled ? 0xB5121212 : 0x900C0C0C);
                Gui.drawRect(cardX, cardY, cardX + 3, cardY + cardHeight, isEnabled ? 0xFF0055FF : 0xFF333333);

                if (cardHover) {
                    Gui.drawRect(cardX, cardY, cardX + cardWidth, cardY + 1, 0x400055FF);
                }

                // Nome do mod e badge de categoria
                fontRendererObj.drawStringWithShadow(mod.getDisplayName(), cardX + 10, cardY + 8, isEnabled ? 0xFFFFFFFF : 0xFFAAAAAA);
                fontRendererObj.drawStringWithShadow("§8[§9" + mod.getCategory() + "§8]", cardX + 10 + fontRendererObj.getStringWidth(mod.getDisplayName()) + 5, cardY + 8, 0xFF888888);

                // Descrição curta
                String desc = mod.getDescription();
                if (fontRendererObj.getStringWidth(desc) > 140) {
                    desc = fontRendererObj.trimStringToWidth(desc, 135) + "...";
                }
                fontRendererObj.drawStringWithShadow("§7" + desc, cardX + 10, cardY + 22, 0xFF777777);

                // Botão de Engrenagem (Settings)
                int gearX = cardX + cardWidth - 54;
                int gearY = cardY + 12;
                boolean gearHover = mouseX >= gearX && mouseX <= gearX + 20 && mouseY >= gearY && mouseY <= gearY + 20;
                Gui.drawRect(gearX, gearY, gearX + 20, gearY + 20, gearHover ? 0xFF0055FF : 0xFF222222);
                drawCenteredString(fontRendererObj, "§f⚙", gearX + 10, gearY + 6, 0xFFFFFFFF);

                // Botão Switch ON / OFF
                int switchX = cardX + cardWidth - 30;
                int switchY = cardY + 12;
                boolean switchHover = mouseX >= switchX && mouseX <= switchX + 24 && mouseY >= switchY && mouseY <= switchY + 20;
                int switchBg = isEnabled ? 0xFF0055FF : (switchHover ? 0xFF353535 : 0xFF1C1C1C);
                Gui.drawRect(switchX, switchY, switchX + 24, switchY + 20, switchBg);
                drawCenteredString(fontRendererObj, isEnabled ? "§a✓" : "§c✕", switchX + 12, switchY + 6, 0xFFFFFFFF);
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

        // Clique no botão HUD EDITOR
        int hudBtnX = width - 135;
        int hudBtnY = 6;
        if (mouseX >= hudBtnX && mouseX <= hudBtnX + 120 && mouseY >= hudBtnY && mouseY <= hudBtnY + 20) {
            mc.displayGuiScreen(new HUDEditorScreen());
            return;
        }

        // Clique nas abas de categorias
        int catStartX = width / 2 - (categories.length * 60) / 2;
        for (int i = 0; i < categories.length; i++) {
            int cx = catStartX + (i * 60);
            if (mouseX >= cx && mouseX <= cx + 55 && mouseY >= 8 && mouseY <= 24) {
                selectedCategory = categories[i];
                scrollOffset = 0;
                return;
            }
        }

        // Clique nos cards de mods
        List<BaseMod> filteredMods = getFilteredMods();
        int listY = 64;
        int cardWidth = 230;
        int cardHeight = 44;
        int spacing = 8;
        int totalCols = Math.max(1, Math.min(2, width / (cardWidth + spacing)));
        int gridStartX = width / 2 - ((cardWidth * totalCols) + (spacing * (totalCols - 1))) / 2;

        int row = 0;
        int col = 0;

        for (BaseMod mod : filteredMods) {
            int cardX = gridStartX + (col * (cardWidth + spacing));
            int cardY = listY + (row * (cardHeight + spacing)) - scrollOffset;

            if (cardY + cardHeight > 58 && cardY < height - 10) {
                int gearX = cardX + cardWidth - 54;
                int gearY = cardY + 12;
                int switchX = cardX + cardWidth - 30;
                int switchY = cardY + 12;

                // Engrenagem (Abrir Settings do Mod)
                if (mouseX >= gearX && mouseX <= gearX + 20 && mouseY >= gearY && mouseY <= gearY + 20) {
                    mc.displayGuiScreen(new ModSettingsScreen(this, mod));
                    return;
                }

                // Switch ON/OFF ou Card
                if (mouseX >= switchX && mouseX <= switchX + 24 && mouseY >= switchY && mouseY <= switchY + 20) {
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
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RSHIFT) {
            Config.salvar();
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
}
