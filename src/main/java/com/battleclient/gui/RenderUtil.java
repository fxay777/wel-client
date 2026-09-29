package com.battleclient.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

    public static void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void drawBorder(int x, int y, int width, int height, int thickness, int color) {
        Gui.drawRect(x, y, x + width, y + thickness, color);
        Gui.drawRect(x, y, x + thickness, y + height, color);
        Gui.drawRect(x + width - thickness, y, x + width, y + height, color);
        Gui.drawRect(x, y + height - thickness, x + width, y + height, color);
    }

    /**
     * Desenha um Switch moderno estilo iOS / Lunar Client
     */
    public static void drawToggleSwitch(int x, int y, boolean enabled) {
        int width = 24;
        int height = 12;

        // Fundo da pilula
        int bgColor = enabled ? 0xFF0055FF : 0xFF2A2A2A;
        int borderColor = enabled ? 0xFF3388FF : 0xFF444444;

        Gui.drawRect(x, y, x + width, y + height, bgColor);
        drawBorder(x, y, width, height, 1, borderColor);

        // Bolinha indicadora
        int knobSize = 8;
        int knobY = y + 2;
        int knobX = enabled ? (x + width - knobSize - 2) : (x + 2);
        int knobColor = enabled ? 0xFFFFFFFF : 0xFFAAAAAA;

        Gui.drawRect(knobX, knobY, knobX + knobSize, knobY + knobSize, knobColor);
    }

    /**
     * Desenha um botao de engrenagem / configuracoes limpo
     */
    public static void drawGearIcon(int x, int y, boolean hovered) {
        int bgColor = hovered ? 0xFF0055FF : 0xFF262626;
        int borderColor = hovered ? 0xFF55AAFF : 0xFF3A3A3A;

        Gui.drawRect(x, y, x + 16, y + 16, bgColor);
        drawBorder(x, y, 16, 16, 1, borderColor);

        // Desenha simbolo geometrico limpo de engrenagem / slider
        int iconColor = hovered ? 0xFFFFFFFF : 0xFFCCCCCC;
        Gui.drawRect(x + 5, y + 4, x + 11, y + 6, iconColor);
        Gui.drawRect(x + 7, y + 3, x + 9, y + 13, iconColor);
        Gui.drawRect(x + 5, y + 10, x + 11, y + 12, iconColor);
        Gui.drawRect(x + 4, y + 7, x + 12, y + 9, iconColor);
    }
}
