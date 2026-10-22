package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ModPotionEffects — Lunar Client / BadLion style
 *
 * Layout (Vertical):
 *   ┌────────────────────────────────┐
 *   │ [icon] Speed II                │  ← name in white
 *   │         0:30                   │  ← duration in grey
 *   └────────────────────────────────┘
 *   Each effect has its own dark semi-transparent background card.
 */
public class ModPotionEffects extends BaseMod {
    public static ModPotionEffects instance;

    private static final ResourceLocation INVENTORY_TEXTURE =
            new ResourceLocation("textures/gui/container/inventory.png");

    // ─── Settings ─────────────────────────────────────────────────────────────
    private final BooleanSetting showIcons;
    private final BooleanSetting showNames;
    private final BooleanSetting showDuration;
    private final BooleanSetting showAmplifier;    // show II, III, IV suffix
    private final BooleanSetting textShadow;
    private final BooleanSetting blinkLowDuration; // blink + red when < 10 s
    private final BooleanSetting excludePermanent;
    private final NumberSetting  bgAlpha;          // 0-200, background per-effect
    private final ModeSetting    layoutMode;       // Vertical | Horizontal
    private final NumberSetting  effectSpacing;
    private final NumberSetting  scale;
    private final ColorSetting   textColor;

    public ModPotionEffects() {
        super("potions", "Potion Effects",
              "Exibe efeitos de pocoes ativos com icones e duracoes no estilo CheatBreaker/Lunar", "HUD");
        instance  = this;
        this.x    = 4;
        this.y    = 80;
        this.width  = 110;
        this.height = 40;

        showIcons       = new BooleanSetting("showIcons",    "Show Icons",              "Exibir icones das pocoes", true);
        showNames       = new BooleanSetting("showNames",    "Show Effect Name",        "Exibir nome do efeito", true);
        showDuration    = new BooleanSetting("showDuration", "Show Duration",           "Exibir tempo restante", true);
        showAmplifier   = new BooleanSetting("showAmp",      "Show Amplifier",         "Mostrar nivel do efeito no nome (II, III, IV)", true);
        textShadow      = new BooleanSetting("textShadow",   "Text Shadow",            "Sombra nos textos", true);
        blinkLowDuration= new BooleanSetting("blink",        "Blink When Low",         "Piscar e vermelho quando < 10 s", true);
        excludePermanent= new BooleanSetting("excludePerm",  "Exclude Permanent",      "Ocultar efeitos permanentes", false);
        bgAlpha         = new NumberSetting ("bgAlpha",      "Background Alpha",       "Transparencia do fundo por efeito (0-200)", 120.0, 0.0, 200.0, 5.0);
        layoutMode      = new ModeSetting  ("mode",          "Layout Mode",            "Estilo do layout", 0, "Vertical", "Horizontal");
        effectSpacing   = new NumberSetting ("spacing",      "Effect Spacing",         "Espacamento entre efeitos", 4.0, 0.0, 12.0, 1.0);
        scale           = new NumberSetting ("scale",        "Scale",                  "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);
        textColor       = new ColorSetting  ("color",        "Text Color",             "Cor padrao do texto", 0xFFFFFFFF);

        addSetting(showIcons);
        addSetting(showNames);
        addSetting(showDuration);
        addSetting(showAmplifier);
        addSetting(textShadow);
        addSetting(blinkLowDuration);
        addSetting(excludePermanent);
        addSetting(bgAlpha);
        addSetting(layoutMode);
        addSetting(effectSpacing);
        addSetting(scale);
        addSetting(textColor);
    }

    // ─── Render ───────────────────────────────────────────────────────────────
    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null) return;

        Collection<PotionEffect> activeEffects = mc.thePlayer.getActivePotionEffects();
        if (activeEffects.isEmpty()) return;

        // Filter
        List<PotionEffect> toRender = new ArrayList<PotionEffect>();
        for (PotionEffect pe : activeEffects) {
            if (excludePermanent.isEnabled() && pe.getIsPotionDurationMax()) continue;
            toRender.add(pe);
        }
        if (toRender.isEmpty()) return;

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        boolean vert = layoutMode.is("Vertical");
        int spacing  = effectSpacing.getIntValue();

        if (vert) {
            renderVertical(toRender, renderX, renderY, spacing, s);
        } else {
            renderHorizontal(toRender, renderX, renderY, spacing, s);
        }

        GlStateManager.popMatrix();
    }

    // ─── Vertical layout ─────────────────────────────────────────────────────
    private void renderVertical(List<PotionEffect> effects, int rx, int ry, int spacing, float s) {
        final int ICON_SIZE = 16;
        final int PAD       = 4;
        // Card height: icon OR two text lines (name + duration), whichever is taller
        // Icon=16, two text lines=8+8+2gap = 18 → card inner=18, outer=18+PAD*2
        final int cardInnerH = Math.max(ICON_SIZE, (showNames.isEnabled() && showDuration.isEnabled()) ? 18 : 10);
        final int cardH      = cardInnerH + PAD * 2;

        // Compute max text width across all effects for uniform card width
        int maxTextW = 30;
        for (PotionEffect pe : effects) {
            Potion pot = Potion.potionTypes[pe.getPotionID()];
            if (pot == null) continue;
            String name = buildName(pot, pe);
            String dur  = buildDuration(pe);
            int tw = Math.max(mc.fontRendererObj.getStringWidth(name),
                              mc.fontRendererObj.getStringWidth(dur));
            if (tw > maxTextW) maxTextW = tw;
        }

        int iconAreaW = showIcons.isEnabled() ? (ICON_SIZE + 4) : 0;
        int cardW     = PAD + iconAreaW + maxTextW + PAD;
        int totalH    = 0;
        int curY      = ry;

        for (PotionEffect pe : effects) {
            Potion pot = Potion.potionTypes[pe.getPotionID()];
            if (pot == null) continue;

            boolean isLow   = blinkLowDuration.isEnabled() && pe.getDuration() <= 200;
            boolean visible = true;
            if (isLow) {
                int speed = pe.getDuration() <= 100 ? 5 : 10;
                visible   = (pe.getDuration() / speed) % 2 == 0;
            }

            if (!visible) {
                curY   += cardH + spacing;
                totalH += cardH + spacing;
                continue;
            }

            int bg = buildBgColor(isLow);
            Gui.drawRect(rx, curY, rx + cardW, curY + cardH, bg);

            int textX = rx + PAD;

            // Icon
            if (showIcons.isEnabled() && pot.hasStatusIcon()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(INVENTORY_TEXTURE);
                int iconIdx = pot.getStatusIconIndex();
                int u = (iconIdx % 8) * 18;
                int v = 198 + (iconIdx / 8) * 18;
                Gui.drawModalRectWithCustomSizedTexture(
                        rx + PAD, curY + PAD, u, v, ICON_SIZE, ICON_SIZE, 256, 256);
                textX += ICON_SIZE + 4;
            }

            // Name + Duration text
            String name = buildName(pot, pe);
            String dur  = buildDuration(pe);
            boolean shadow = textShadow.isEnabled();
            int nameColor  = isLow ? 0xFFFF5555 : textColor.getValue();

            if (showNames.isEnabled() && showDuration.isEnabled()) {
                mc.fontRendererObj.drawString(name, textX, curY + PAD + 1, nameColor, shadow);
                mc.fontRendererObj.drawString(dur,  textX, curY + PAD + 10, 0xFFAAAAAA, shadow);
            } else if (showNames.isEnabled()) {
                mc.fontRendererObj.drawString(name, textX, curY + PAD + (cardInnerH - 8) / 2, nameColor, shadow);
            } else if (showDuration.isEnabled()) {
                mc.fontRendererObj.drawString(dur,  textX, curY + PAD + (cardInnerH - 8) / 2, 0xFFAAAAAA, shadow);
            }

            curY   += cardH + spacing;
            totalH += cardH + spacing;
        }

        this.width  = (int) (cardW * s);
        this.height = (int) (totalH * s);
    }

    // ─── Horizontal layout ────────────────────────────────────────────────────
    private void renderHorizontal(List<PotionEffect> effects, int rx, int ry, int spacing, float s) {
        final int ICON_SIZE = 16;
        final int PAD       = 4;
        final int cardH     = ICON_SIZE + PAD * 2;

        int curX   = rx;
        int totalW = 0;

        for (PotionEffect pe : effects) {
            Potion pot = Potion.potionTypes[pe.getPotionID()];
            if (pot == null) continue;

            boolean isLow   = blinkLowDuration.isEnabled() && pe.getDuration() <= 200;
            boolean visible = true;
            if (isLow) {
                int speed = pe.getDuration() <= 100 ? 5 : 10;
                visible   = (pe.getDuration() / speed) % 2 == 0;
            }

            // Compute card width
            String name   = buildName(pot, pe);
            String dur    = buildDuration(pe);
            int textW     = Math.max(mc.fontRendererObj.getStringWidth(name),
                                     mc.fontRendererObj.getStringWidth(dur));
            int iconAreaW = showIcons.isEnabled() ? (ICON_SIZE + 4) : 0;
            int cardW     = PAD + iconAreaW + textW + PAD;

            if (!visible) {
                curX   += cardW + spacing;
                totalW += cardW + spacing;
                continue;
            }

            int bg = buildBgColor(isLow);
            Gui.drawRect(curX, ry, curX + cardW, ry + cardH, bg);

            int textX = curX + PAD;

            if (showIcons.isEnabled() && pot.hasStatusIcon()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(INVENTORY_TEXTURE);
                int iconIdx = pot.getStatusIconIndex();
                int u = (iconIdx % 8) * 18;
                int v = 198 + (iconIdx / 8) * 18;
                Gui.drawModalRectWithCustomSizedTexture(
                        curX + PAD, ry + PAD, u, v, ICON_SIZE, ICON_SIZE, 256, 256);
                textX += ICON_SIZE + 4;
            }

            boolean shadow   = textShadow.isEnabled();
            int     nameColor = isLow ? 0xFFFF5555 : textColor.getValue();

            if (showNames.isEnabled() && showDuration.isEnabled()) {
                mc.fontRendererObj.drawString(name, textX, ry + PAD + 1, nameColor, shadow);
                mc.fontRendererObj.drawString(dur,  textX, ry + PAD + 10, 0xFFAAAAAA, shadow);
            } else if (showNames.isEnabled()) {
                mc.fontRendererObj.drawString(name, textX, ry + (cardH - 8) / 2, nameColor, shadow);
            } else if (showDuration.isEnabled()) {
                mc.fontRendererObj.drawString(dur,  textX, ry + (cardH - 8) / 2, 0xFFAAAAAA, shadow);
            }

            curX   += cardW + spacing;
            totalW += cardW + spacing;
        }

        this.width  = (int) (totalW * s);
        this.height = (int) (cardH  * s);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Builds the display name with optional amplifier suffix. */
    private String buildName(Potion pot, PotionEffect pe) {
        String name = I18n.format(pot.getName());
        if (showAmplifier.isEnabled()) {
            int amp = pe.getAmplifier();
            if (amp == 1)      name += " II";
            else if (amp == 2) name += " III";
            else if (amp >= 3) name += " IV";
        }
        return name;
    }

    /** Returns the duration string or "**:**" for permanent effects. */
    private String buildDuration(PotionEffect pe) {
        return pe.getIsPotionDurationMax() ? "**:**" : Potion.getDurationString(pe);
    }

    /**
     * Builds a background ARGB colour.
     * Uses bgAlpha setting; turns reddish when the effect is low.
     */
    private int buildBgColor(boolean isLow) {
        int alpha = Math.min(bgAlpha.getIntValue(), 255);
        if (isLow) {
            // Subtle red tint on low duration
            return (alpha << 24) | 0x00220000;
        }
        return (alpha << 24); // pure black at configured alpha
    }
}
