package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ModKeystrokes extends BaseMod {
    public static ModKeystrokes instance;

    private final BooleanSetting showMouse;
    private final BooleanSetting showCPS;
    private final BooleanSetting showSpace;
    private final BooleanSetting showSneak;
    private final BooleanSetting chroma;
    private final BooleanSetting outline;
    private final ColorSetting pressedBgColor;
    private final ColorSetting unpressedBgColor;
    private final ColorSetting textPressedColor;
    private final ColorSetting textUnpressedColor;
    private final ColorSetting outlineColor;
    private final NumberSetting scale;

    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

    public ModKeystrokes() {
        super("keystrokes", "Keystrokes", "Exibe as teclas pressionadas na tela (WASD, Mouse com CPS, Espaço e Sneak)", "HUD");
        instance = this;
        this.x = 10;
        this.y = 80;
        this.width = 66;
        this.height = 70;

        showMouse = new BooleanSetting("mouse", "Show Mouse Buttons", "Exibir botões LMB e RMB", true);
        showCPS = new BooleanSetting("showCPS", "Show CPS on Mouse", "Exibir número de CPS nos botões do mouse", true);
        showSpace = new BooleanSetting("space", "Show Spacebar", "Exibir barra de espaço", true);
        showSneak = new BooleanSetting("sneak", "Show Sneak (Shift)", "Exibir tecla de agachar (Shift)", false);
        chroma = new BooleanSetting("chroma", "Chroma (Rainbow)", "Efeito arco-íris dinâmico", false);
        outline = new BooleanSetting("outline", "Outline Borders", "Contorno destacado nas teclas", true);

        pressedBgColor = new ColorSetting("pressedBg", "Pressed Background", "Cor de fundo ao pressionar", 0x900055FF);
        unpressedBgColor = new ColorSetting("unpressedBg", "Unpressed Background", "Cor de fundo normal", 0x80000000);
        textPressedColor = new ColorSetting("textPressed", "Text Pressed Color", "Cor do texto ao pressionar", 0xFFFFFFFF);
        textUnpressedColor = new ColorSetting("textUnpressed", "Text Unpressed Color", "Cor do texto normal", 0xFFDDDDDD);
        outlineColor = new ColorSetting("outlineColor", "Outline Color", "Cor da borda das teclas", 0x50FFFFFF);
        scale = new NumberSetting("scale", "Scale", "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);

        addSetting(showMouse);
        addSetting(showCPS);
        addSetting(showSpace);
        addSetting(showSneak);
        addSetting(chroma);
        addSetting(outline);
        addSetting(pressedBgColor);
        addSetting(unpressedBgColor);
        addSetting(textPressedColor);
        addSetting(textUnpressedColor);
        addSetting(outlineColor);
        addSetting(scale);
    }

    @Override
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            int button = Mouse.getEventButton();
            long now = System.currentTimeMillis();
            if (button == 0) {
                leftClicks.add(now);
            } else if (button == 1) {
                rightClicks.add(now);
            }
        }
    }

    private int getCPS(List<Long> clicks) {
        long now = System.currentTimeMillis();
        clicks.removeIf(time -> now - time > 1000L);
        return clicks.size();
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null || mc.gameSettings == null) return;

        int keyW = mc.gameSettings.keyBindForward.getKeyCode();
        int keyA = mc.gameSettings.keyBindLeft.getKeyCode();
        int keyS = mc.gameSettings.keyBindBack.getKeyCode();
        int keyD = mc.gameSettings.keyBindRight.getKeyCode();
        int keyJump = mc.gameSettings.keyBindJump.getKeyCode();
        int keySneak = mc.gameSettings.keyBindSneak.getKeyCode();

        boolean wDown = Keyboard.isKeyDown(keyW);
        boolean aDown = Keyboard.isKeyDown(keyA);
        boolean sDown = Keyboard.isKeyDown(keyS);
        boolean dDown = Keyboard.isKeyDown(keyD);
        boolean jumpDown = Keyboard.isKeyDown(keyJump);
        boolean sneakDown = Keyboard.isKeyDown(keySneak);

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        int keySize = 20;
        int gap = 2;

        // W
        drawKey(renderX + keySize + gap, renderY, keySize, keySize, "W", null, wDown);

        // A, S, D
        int row2Y = renderY + keySize + gap;
        drawKey(renderX, row2Y, keySize, keySize, "A", null, aDown);
        drawKey(renderX + keySize + gap, row2Y, keySize, keySize, "S", null, sDown);
        drawKey(renderX + (keySize + gap) * 2, row2Y, keySize, keySize, "D", null, dDown);

        int nextY = row2Y + keySize + gap;

        // LMB & RMB
        if (showMouse.isEnabled()) {
            boolean lmb = Mouse.isButtonDown(0);
            boolean rmb = Mouse.isButtonDown(1);
            int mouseWidth = (keySize * 3 + gap * 2 - gap) / 2;

            String lmbSub = showCPS.isEnabled() ? (getCPS(leftClicks) + " CPS") : null;
            String rmbSub = showCPS.isEnabled() ? (getCPS(rightClicks) + " CPS") : null;

            int mouseH = showCPS.isEnabled() ? 24 : keySize;
            drawKey(renderX, nextY, mouseWidth, mouseH, "LMB", lmbSub, lmb);
            drawKey(renderX + mouseWidth + gap, nextY, mouseWidth, mouseH, "RMB", rmbSub, rmb);

            nextY += mouseH + gap;
        }

        // Barra de espaço
        if (showSpace.isEnabled()) {
            int spaceWidth = keySize * 3 + gap * 2;
            drawKey(renderX, nextY, spaceWidth, 12, "—", null, jumpDown);
            nextY += 12 + gap;
        }

        // Sneak (Shift)
        if (showSneak.isEnabled()) {
            int spaceWidth = keySize * 3 + gap * 2;
            drawKey(renderX, nextY, spaceWidth, 12, "Sneak", null, sneakDown);
            nextY += 12 + gap;
        }

        this.width = (int) ((keySize * 3 + gap * 2) * s);
        this.height = (int) ((nextY - renderY) * s);

        GlStateManager.popMatrix();
    }

    private void drawKey(int kx, int ky, int kw, int kh, String name, String subText, boolean pressed) {
        int bg = pressed ? pressedBgColor.getValue() : unpressedBgColor.getValue();
        Gui.drawRect(kx, ky, kx + kw, ky + kh, bg);

        if (outline.isEnabled()) {
            int oColor = outlineColor.getValue();
            if (chroma.isEnabled()) {
                float hue = (System.currentTimeMillis() % 4000L) / 4000.0F;
                oColor = Color.HSBtoRGB(hue, 0.8F, 1.0F);
            }
            // Borda elegante
            Gui.drawRect(kx, ky, kx + kw, ky + 1, oColor);
            Gui.drawRect(kx, ky, kx + 1, ky + kh, oColor);
            Gui.drawRect(kx + kw - 1, ky, kx + kw, ky + kh, oColor);
            Gui.drawRect(kx, ky + kh - 1, kx + kw, ky + kh, oColor);
        }

        int tColor = pressed ? textPressedColor.getValue() : textUnpressedColor.getValue();
        if (chroma.isEnabled()) {
            float hue = (System.currentTimeMillis() % 4000L) / 4000.0F;
            tColor = Color.HSBtoRGB(hue, 0.8F, 1.0F);
        }

        if (subText != null) {
            // Nome no topo e CPS embaixo menor
            int strW = mc.fontRendererObj.getStringWidth(name);
            mc.fontRendererObj.drawStringWithShadow(name, kx + (kw - strW) / 2, ky + 3, tColor);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.7F, 0.7F, 0.7F);
            int subW = mc.fontRendererObj.getStringWidth(subText);
            float subX = (kx + (kw - subW * 0.7F) / 2) / 0.7F;
            float subY = (ky + 14) / 0.7F;
            mc.fontRendererObj.drawStringWithShadow(subText, (int) subX, (int) subY, 0xFFAAAAAA);
            GlStateManager.popMatrix();
        } else {
            int strW = mc.fontRendererObj.getStringWidth(name);
            mc.fontRendererObj.drawStringWithShadow(name, kx + (kw - strW) / 2, ky + (kh - 8) / 2, tColor);
        }
    }
}
