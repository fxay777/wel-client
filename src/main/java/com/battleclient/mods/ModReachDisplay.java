package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModReachDisplay extends BaseMod {
    public static ModReachDisplay instance;

    // ─── Settings ─────────────────────────────────────────────────────────────
    private final BooleanSetting dynamicColor;
    private final BooleanSetting showLabel;       // "Reach" prefix
    private final ColorSetting   accentColor;     // Left dot colour
    private final NumberSetting  displayDuration; // seconds to keep visible
    private final NumberSetting  scale;
    private final ColorSetting   textColor;       // default text colour

    // ─── State ────────────────────────────────────────────────────────────────
    private double lastReachDistance = 0.0;
    private long   lastHitTime       = 0L;

    // Fade-out window (last N ms of visibility)
    private static final long FADE_MS = 500L;

    public ModReachDisplay() {
        super("reachdisplay", "Reach Display",
              "Exibe a distância exata de alcance dos seus ataques em combate PvP", "HUD");
        instance  = this;
        this.x    = 10;
        this.y    = 290;
        this.width  = 75;
        this.height = 14;

        dynamicColor    = new BooleanSetting("dynamicColor", "Dynamic Color",
                          "Cor muda conforme a distância do golpe", true);
        showLabel       = new BooleanSetting("showLabel", "Show Label",
                          "Mostrar prefixo 'Reach' antes do numero", true);
        accentColor     = new ColorSetting  ("accentColor", "Accent Dot Color",
                          "Cor do marcador à esquerda do texto", 0xFF0055FF);
        displayDuration = new NumberSetting ("duration", "Display Time (s)",
                          "Tempo de exibicao antes de sumir", 2.5, 1.0, 5.0, 0.5);
        scale           = new NumberSetting ("scale", "Scale",
                          "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);
        textColor       = new ColorSetting  ("color", "Text Color",
                          "Cor padrao do texto", 0xFFFFFFFF);

        addSetting(dynamicColor);
        addSetting(showLabel);
        addSetting(accentColor);
        addSetting(displayDuration);
        addSetting(scale);
        addSetting(textColor);
    }

    // ─── Called by the attack hook in the mod manager ─────────────────────────
    public void onPlayerAttack(Entity target) {
        if (!enabled || mc.thePlayer == null || target == null) return;

        Vec3 eyes = mc.thePlayer.getPositionEyes(1.0F);
        MovingObjectPosition mop = mc.objectMouseOver;

        double dist;
        if (mop != null && mop.hitVec != null) {
            dist = eyes.distanceTo(mop.hitVec);
        } else {
            // Closest point on the entity's bounding box
            AxisAlignedBB bb = target.getEntityBoundingBox();
            double cx = Math.max(bb.minX, Math.min(eyes.xCoord, bb.maxX));
            double cy = Math.max(bb.minY, Math.min(eyes.yCoord, bb.maxY));
            double cz = Math.max(bb.minZ, Math.min(eyes.zCoord, bb.maxZ));
            dist = eyes.distanceTo(new Vec3(cx, cy, cz));
        }

        if (dist > 0.05 && dist < 7.0) {
            lastReachDistance = dist;
            lastHitTime       = System.currentTimeMillis();
        }
    }

    // ─── Render ───────────────────────────────────────────────────────────────
    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null) return;

        long elapsed      = System.currentTimeMillis() - lastHitTime;
        long maxDurationMs = (long) (displayDuration.getValue() * 1000);

        if (lastHitTime == 0 || elapsed > maxDurationMs) return;

        // ── Compute alpha for fade-out in the last FADE_MS ms ──────────────
        int alpha = 255;
        long remaining = maxDurationMs - elapsed;
        if (remaining < FADE_MS) {
            alpha = (int) (255 * remaining / (float) FADE_MS);
        }
        alpha = Math.max(0, Math.min(255, alpha));

        // ── Build display text ─────────────────────────────────────────────
        String numStr = String.format("%.2f blocks", lastReachDistance);
        String text   = showLabel.isEnabled() ? ("Reach  " + numStr) : numStr;

        // ── Dynamic colour selection ───────────────────────────────────────
        int baseColor = textColor.getValue();
        if (dynamicColor.isEnabled()) {
            if (lastReachDistance < 2.0) {
                baseColor = 0xFFFFFF55; // Yellow  — suspiciously close
            } else if (lastReachDistance < 3.0) {
                baseColor = 0xFF55FF55; // Green   — normal
            } else if (lastReachDistance <= 3.5) {
                baseColor = 0xFF55FFFF; // Cyan    — long reach
            } else {
                baseColor = 0xFFFF5555; // Red     — extreme reach
            }
        }

        // Apply computed alpha to colour
        int finalColor = applyAlpha(baseColor, alpha);
        int accentC    = applyAlpha(accentColor.getValue(), alpha);
        int bgAlpha    = (int) (0x88 * alpha / 255.0);

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        // ── Sizes ──────────────────────────────────────────────────────────
        int dotW   = 4;
        int dotGap = 4;
        int strW   = mc.fontRendererObj.getStringWidth(text);
        int totalW = dotW + dotGap + strW + 6;
        int totalH = 14;

        this.width  = (int) (totalW * s);
        this.height = (int) (totalH * s);

        // ── Dark background ────────────────────────────────────────────────
        int bgColor = (bgAlpha << 24);
        Gui.drawRect(renderX - 2, renderY - 2, renderX + totalW + 2, renderY + totalH - 1, bgColor);

        // ── Accent dot (left side, vertically centred) ─────────────────────
        int dotY = renderY + (totalH - 6) / 2;
        Gui.drawRect(renderX, dotY, renderX + dotW, dotY + 4, accentC);

        // ── Text ───────────────────────────────────────────────────────────
        mc.fontRendererObj.drawStringWithShadow(text, renderX + dotW + dotGap, renderY + 3, finalColor);

        GlStateManager.popMatrix();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Replaces the alpha channel of an ARGB colour with the given 0-255 value. */
    private static int applyAlpha(int argb, int alpha) {
        return (argb & 0x00FFFFFF) | (alpha << 24);
    }
}
