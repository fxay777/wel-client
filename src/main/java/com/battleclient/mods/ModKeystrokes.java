package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * ModKeystrokes — Clean Lunar Client style keystrokes HUD.
 *
 * Supports themes (Custom / Dark / Blue / Green), variable key size,
 * simulated corner radius, showAttackOnMouse (LMB → "ATK"), CPS with
 * colour coding, chroma outline and all previous settings.
 */
public class ModKeystrokes extends BaseMod {
    public static ModKeystrokes instance;

    // ─── Settings ─────────────────────────────────────────────────────────────
    private final BooleanSetting showMouse;
    private final BooleanSetting showCPS;
    private final BooleanSetting showSpace;
    private final BooleanSetting showSneak;
    private final BooleanSetting showAttackOnMouse; // "ATK" on LMB when pressed
    private final BooleanSetting chroma;
    private final BooleanSetting outline;
    private final ModeSetting    theme;             // Custom | Dark | Blue | Green
    private final ColorSetting   pressedBgColor;
    private final ColorSetting   unpressedBgColor;
    private final ColorSetting   textPressedColor;
    private final ColorSetting   textUnpressedColor;
    private final ColorSetting   outlineColor;
    private final NumberSetting  keySize;           // 16-28, default 20
    private final NumberSetting  cornerRadius;      // 0-5,   default 2
    private final NumberSetting  scale;

    // ─── Theme colour constants ────────────────────────────────────────────────
    private static final int DARK_PRESSED   = 0xCC333333;
    private static final int DARK_UNPRESSED = 0x88111111;
    private static final int DARK_TEXT      = 0xFFFFFFFF;

    private static final int BLUE_PRESSED   = 0xCC0055FF;
    private static final int BLUE_UNPRESSED = 0x880022AA;
    private static final int BLUE_TEXT      = 0xFFFFFFFF;

    private static final int GREEN_PRESSED   = 0xCC00AA44;
    private static final int GREEN_UNPRESSED = 0x88005522;
    private static final int GREEN_TEXT      = 0xFFFFFFFF;

    // ─── CPS tracking ─────────────────────────────────────────────────────────
    private final List<Long> leftClicks  = new ArrayList<Long>();
    private final List<Long> rightClicks = new ArrayList<Long>();

    public ModKeystrokes() {
        super("keystrokes", "Keystrokes",
              "Exibe as teclas pressionadas na tela (WASD, Mouse com CPS, Espaco e Sneak)", "HUD");
        instance  = this;
        this.x    = 10;
        this.y    = 80;
        this.width  = 66;
        this.height = 70;

        showMouse         = new BooleanSetting("mouse",         "Show Mouse Buttons",      "Exibir botoes LMB e RMB", true);
        showCPS           = new BooleanSetting("showCPS",       "Show CPS on Mouse",       "Exibir numero de CPS nos botoes do mouse", true);
        showSpace         = new BooleanSetting("space",         "Show Spacebar",           "Exibir barra de espaco", true);
        showSneak         = new BooleanSetting("sneak",         "Show Sneak (Shift)",      "Exibir tecla de agachar (Shift)", false);
        showAttackOnMouse = new BooleanSetting("atkOnMouse",    "Show ATK on LMB Press",   "Mostrar 'ATK' no LMB quando atacando", false);
        chroma            = new BooleanSetting("chroma",        "Chroma (Rainbow)",        "Efeito arco-iris dinamico nas bordas", false);
        outline           = new BooleanSetting("outline",       "Outline Borders",         "Contorno nas teclas", true);
        theme             = new ModeSetting  ("theme",         "Theme",                   "Tema visual das teclas", 0,
                                              "Custom", "Dark", "Blue", "Green");

        pressedBgColor    = new ColorSetting ("pressedBg",     "Pressed Background",      "Cor de fundo ao pressionar (Custom)", 0x900055FF);
        unpressedBgColor  = new ColorSetting ("unpressedBg",   "Unpressed Background",    "Cor de fundo normal (Custom)", 0x80000000);
        textPressedColor  = new ColorSetting ("textPressed",   "Text Pressed Color",      "Cor do texto ao pressionar (Custom)", 0xFFFFFFFF);
        textUnpressedColor= new ColorSetting ("textUnpressed", "Text Unpressed Color",    "Cor do texto normal (Custom)", 0xFFDDDDDD);
        outlineColor      = new ColorSetting ("outlineColor",  "Outline Color",           "Cor da borda (Custom)", 0x50FFFFFF);
        keySize           = new NumberSetting("keySize",       "Key Size",                "Tamanho das teclas em pixels", 20.0, 16.0, 28.0, 1.0);
        cornerRadius      = new NumberSetting("corner",        "Corner Radius",           "Raio de bordas arredondadas simulado", 2.0, 0.0, 5.0, 1.0);
        scale             = new NumberSetting("scale",         "Scale",                   "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);

        addSetting(showMouse);
        addSetting(showCPS);
        addSetting(showSpace);
        addSetting(showSneak);
        addSetting(showAttackOnMouse);
        addSetting(chroma);
        addSetting(outline);
        addSetting(theme);
        addSetting(pressedBgColor);
        addSetting(unpressedBgColor);
        addSetting(textPressedColor);
        addSetting(textUnpressedColor);
        addSetting(outlineColor);
        addSetting(keySize);
        addSetting(cornerRadius);
        addSetting(scale);
    }

    // ─── Mouse click tracking ─────────────────────────────────────────────────
    @Override
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            int btn = Mouse.getEventButton();
            long now = System.currentTimeMillis();
            if (btn == 0) leftClicks.add(now);
            else if (btn == 1) rightClicks.add(now);
        }
    }

    private int getCPS(List<Long> clicks) {
        long now = System.currentTimeMillis();
        clicks.removeIf(t -> now - t > 1000L);
        return clicks.size();
    }

    // ─── Render ───────────────────────────────────────────────────────────────
    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null || mc.gameSettings == null) return;

        boolean wDown    = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        boolean aDown    = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        boolean sDown    = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        boolean dDown    = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
        boolean jumpDown = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        boolean sneakDown= Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());

        float s   = scale.getFloatValue();
        int   ksz = keySize.getIntValue();
        int   gap = 2;

        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int rx = (int) (x / s);
        int ry = (int) (y / s);

        // Row 1: W (centred over A-S-D)
        drawKey(rx + ksz + gap, ry, ksz, ksz, "W", null, wDown);

        // Row 2: A, S, D
        int row2Y = ry + ksz + gap;
        drawKey(rx,                   row2Y, ksz, ksz, "A", null, aDown);
        drawKey(rx + ksz + gap,       row2Y, ksz, ksz, "S", null, sDown);
        drawKey(rx + (ksz + gap) * 2, row2Y, ksz, ksz, "D", null, dDown);

        int nextY = row2Y + ksz + gap;

        // Row 3: LMB / RMB
        if (showMouse.isEnabled()) {
            boolean lmb      = Mouse.isButtonDown(0);
            boolean rmb      = Mouse.isButtonDown(1);
            int mouseW       = (ksz * 3 + gap * 2 - gap) / 2;
            int lCPS         = getCPS(leftClicks);
            int rCPS         = getCPS(rightClicks);
            int mouseH       = showCPS.isEnabled() ? 24 : ksz;

            String lLabel = (showAttackOnMouse.isEnabled() && lmb) ? "ATK" : "LMB";
            String rLabel = "RMB";
            String lSub   = showCPS.isEnabled() ? (lCPS + " CPS") : null;
            String rSub   = showCPS.isEnabled() ? (rCPS + " CPS") : null;

            drawKey(rx,              nextY, mouseW, mouseH, lLabel, lSub, lmb);
            drawKey(rx + mouseW + gap, nextY, mouseW, mouseH, rLabel, rSub, rmb);

            nextY += mouseH + gap;
        }

        // Row 4: Spacebar
        if (showSpace.isEnabled()) {
            int spW = ksz * 3 + gap * 2;
            drawKey(rx, nextY, spW, 12, "—", null, jumpDown);
            nextY += 12 + gap;
        }

        // Row 5: Sneak
        if (showSneak.isEnabled()) {
            int spW = ksz * 3 + gap * 2;
            drawKey(rx, nextY, spW, 12, "Sneak", null, sneakDown);
            nextY += 12 + gap;
        }

        this.width  = (int) ((ksz * 3 + gap * 2) * s);
        this.height = (int) ((nextY - ry) * s);

        GlStateManager.popMatrix();
    }

    // ─── Key drawing ──────────────────────────────────────────────────────────

    private void drawKey(int kx, int ky, int kw, int kh,
                         String name, String subText, boolean pressed) {

        // Resolve theme colours
        int bg, tColorBase;
        String th = theme.getValue();
        if ("Dark".equalsIgnoreCase(th)) {
            bg          = pressed ? DARK_PRESSED   : DARK_UNPRESSED;
            tColorBase  = DARK_TEXT;
        } else if ("Blue".equalsIgnoreCase(th)) {
            bg          = pressed ? BLUE_PRESSED   : BLUE_UNPRESSED;
            tColorBase  = BLUE_TEXT;
        } else if ("Green".equalsIgnoreCase(th)) {
            bg          = pressed ? GREEN_PRESSED  : GREEN_UNPRESSED;
            tColorBase  = GREEN_TEXT;
        } else {
            // Custom
            bg         = pressed ? pressedBgColor.getValue() : unpressedBgColor.getValue();
            tColorBase = pressed ? textPressedColor.getValue() : textUnpressedColor.getValue();
        }

        // Chroma overrides text colour
        int chromaColor = 0;
        if (chroma.isEnabled()) {
            float hue = (System.currentTimeMillis() % 4000L) / 4000.0F;
            chromaColor = Color.HSBtoRGB(hue, 0.8F, 1.0F);
        }

        int tColor = chroma.isEnabled() ? chromaColor : tColorBase;

        // ── Background with simulated rounded corners ─────────────────────────
        int cr = cornerRadius.getIntValue();
        drawRoundedRect(kx, ky, kw, kh, cr, bg);

        // ── Outline ───────────────────────────────────────────────────────────
        if (outline.isEnabled()) {
            int oColor = chroma.isEnabled() ? chromaColor : outlineColor.getValue();
            drawRoundedRectOutline(kx, ky, kw, kh, cr, oColor);
        }

        // ── Text ──────────────────────────────────────────────────────────────
        if (subText != null) {
            // Main label
            int strW = mc.fontRendererObj.getStringWidth(name);
            mc.fontRendererObj.drawStringWithShadow(name,
                    kx + (kw - strW) / 2, ky + 3, tColor);

            // CPS sub-label (smaller, via matrix scale)
            int cpsVal = 0;
            try { cpsVal = Integer.parseInt(subText.replace(" CPS", "").trim()); }
            catch (NumberFormatException ignored) {}
            int cpsColor = cpsColorFor(cpsVal);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.7F, 0.7F, 0.7F);
            int subW  = mc.fontRendererObj.getStringWidth(subText);
            float subX = (kx + (kw - subW * 0.7F) / 2) / 0.7F;
            float subY = (ky + 14) / 0.7F;
            mc.fontRendererObj.drawStringWithShadow(subText, (int) subX, (int) subY, cpsColor);
            GlStateManager.popMatrix();
        } else {
            int strW = mc.fontRendererObj.getStringWidth(name);
            mc.fontRendererObj.drawStringWithShadow(name,
                    kx + (kw - strW) / 2, ky + (kh - 8) / 2, tColor);
        }
    }

    /** CPS colour: green >8, yellow 4-8, white <4. */
    private static int cpsColorFor(int cps) {
        if (cps > 8) return 0xFF55FF55;
        if (cps > 3) return 0xFFFFFF55;
        return 0xFFDDDDDD;
    }

    /**
     * Simulates rounded corners by drawing a full rect and then masking corners
     * with background-coloured squares of size {@code cr × cr}.
     * This approach works entirely with Gui.drawRect — no GL blending trickery.
     */
    private static void drawRoundedRect(int x, int y, int w, int h, int cr, int color) {
        if (cr <= 0) {
            Gui.drawRect(x, y, x + w, y + h, color);
            return;
        }
        // Centre body
        Gui.drawRect(x,      y + cr, x + w,      y + h - cr, color);
        // Top strip (sans corners)
        Gui.drawRect(x + cr, y,      x + w - cr, y + cr,     color);
        // Bottom strip (sans corners)
        Gui.drawRect(x + cr, y + h - cr, x + w - cr, y + h, color);
    }

    /**
     * Draws a 1px outline respecting corner radius — simply skips corner pixels
     * of the outline, giving a rounded visual.
     */
    private static void drawRoundedRectOutline(int x, int y, int w, int h, int cr, int color) {
        // Top and bottom edges (minus corners)
        Gui.drawRect(x + cr, y,         x + w - cr, y + 1,         color);
        Gui.drawRect(x + cr, y + h - 1, x + w - cr, y + h,         color);
        // Left and right edges (minus corners)
        Gui.drawRect(x,      y + cr,     x + 1,      y + h - cr,    color);
        Gui.drawRect(x + w - 1, y + cr, x + w,      y + h - cr,    color);
        // Corner diagonal pixels (single 1×1 dots at diagonal distance cr)
        if (cr >= 1) {
            int c = cr - 1;
            Gui.drawRect(x + c,     y + c,     x + c + 1,     y + c + 1,     color); // TL
            Gui.drawRect(x + w-c-1, y + c,     x + w - c,     y + c + 1,     color); // TR
            Gui.drawRect(x + c,     y + h-c-1, x + c + 1,     y + h - c,     color); // BL
            Gui.drawRect(x + w-c-1, y + h-c-1, x + w - c,     y + h - c,     color); // BR
        }
    }
}
