package com.battleclient.mods;

import com.battleclient.core.ModManager;
import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ModKeystrokes extends BaseMod {
    private final BooleanSetting showMouse;
    private final BooleanSetting showSpace;
    private final ColorSetting pressedColor;
    private final ColorSetting unpressedColor;
    private final ColorSetting textColor;

    public ModKeystrokes() {
        super("keystrokes", "Keystrokes", "Exibe as teclas pressionadas na tela (WASD, Mouse, Espaço)", "HUD");
        this.x = 10;
        this.y = 80;
        this.width = 66;
        this.height = 70;

        showMouse = new BooleanSetting("mouse", "Mostrar Mouse (LMB/RMB)", true);
        showSpace = new BooleanSetting("space", "Mostrar Barra de Espaço", true);
        pressedColor = new ColorSetting("pressedColor", "Cor Pressionada", 0xFF0055FF);
        unpressedColor = new ColorSetting("unpressedColor", "Cor Desativada", 0x80000000);
        textColor = new ColorSetting("textColor", "Cor do Texto", 0xFFFFFFFF);

        addSetting(showMouse);
        addSetting(showSpace);
        addSetting(pressedColor);
        addSetting(unpressedColor);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        int keyW = mc.gameSettings.keyBindForward.getKeyCode();
        int keyA = mc.gameSettings.keyBindLeft.getKeyCode();
        int keyS = mc.gameSettings.keyBindBack.getKeyCode();
        int keyD = mc.gameSettings.keyBindRight.getKeyCode();
        int keyJump = mc.gameSettings.keyBindJump.getKeyCode();

        boolean wDown = Keyboard.isKeyDown(keyW);
        boolean aDown = Keyboard.isKeyDown(keyA);
        boolean sDown = Keyboard.isKeyDown(keyS);
        boolean dDown = Keyboard.isKeyDown(keyD);
        boolean jumpDown = Keyboard.isKeyDown(keyJump);

        int keySize = 20;
        int gap = 2;

        // W
        drawKey(x + keySize + gap, y, keySize, keySize, "W", wDown);

        // A, S, D
        int row2Y = y + keySize + gap;
        drawKey(x, row2Y, keySize, keySize, "A", aDown);
        drawKey(x + keySize + gap, row2Y, keySize, keySize, "S", sDown);
        drawKey(x + (keySize + gap) * 2, row2Y, keySize, keySize, "D", dDown);

        int nextY = row2Y + keySize + gap;

        // LMB & RMB
        if (showMouse.isEnabled()) {
            boolean lmb = Mouse.isButtonDown(0);
            boolean rmb = Mouse.isButtonDown(1);
            int mouseWidth = (keySize * 3 + gap * 2 - gap) / 2;

            drawKey(x, nextY, mouseWidth, keySize, "LMB", lmb);
            drawKey(x + mouseWidth + gap, nextY, mouseWidth, keySize, "RMB", rmb);

            nextY += keySize + gap;
        }

        // Barra de espaço
        if (showSpace.isEnabled()) {
            int spaceWidth = keySize * 3 + gap * 2;
            drawKey(x, nextY, spaceWidth, 12, "—", jumpDown);
            nextY += 12;
        }

        this.width = keySize * 3 + gap * 2;
        this.height = nextY - y;
    }

    private void drawKey(int kx, int ky, int kw, int kh, String name, boolean pressed) {
        int bg = pressed ? pressedColor.getValue() : unpressedColor.getValue();
        Gui.drawRect(kx, ky, kx + kw, ky + kh, bg);

        // Borda sutil
        Gui.drawRect(kx, ky, kx + kw, ky + 1, pressed ? 0xFFFFFFFF : 0x30FFFFFF);

        int strW = mc.fontRendererObj.getStringWidth(name);
        mc.fontRendererObj.drawStringWithShadow(name, kx + (kw - strW) / 2, ky + (kh - 8) / 2, textColor.getValue());
    }
}
