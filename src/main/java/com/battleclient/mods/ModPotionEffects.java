package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
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

public class ModPotionEffects extends BaseMod {
    public static ModPotionEffects instance;

    private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");

    private final BooleanSetting showIcons;
    private final BooleanSetting showNames;
    private final BooleanSetting showDuration;
    private final BooleanSetting textShadow;
    private final BooleanSetting blinkLowDuration;
    private final BooleanSetting excludePermanent;
    private final BooleanSetting showBackground;
    private final NumberSetting effectSpacing;
    private final NumberSetting scale;
    private final ColorSetting textColor;

    public ModPotionEffects() {
        super("potions", "Potion Effects", "Exibe efeitos de poções ativos com ícones e durações no estilo CheatBreaker/Lunar", "HUD");
        instance = this;
        this.x = 4;
        this.y = 80;
        this.width = 90;
        this.height = 40;

        showIcons = new BooleanSetting("showIcons", "Show Icons", "Exibir ícones das poções", true);
        showNames = new BooleanSetting("showNames", "Show Effect Name", "Exibir nome do efeito", true);
        showDuration = new BooleanSetting("showDuration", "Show Duration", "Exibir tempo restante", true);
        textShadow = new BooleanSetting("textShadow", "Text Shadow", "Sombra nos textos", true);
        blinkLowDuration = new BooleanSetting("blink", "Blink Options", "Piscar quando estiver acabando (< 10s)", true);
        excludePermanent = new BooleanSetting("excludePerm", "Exclude Permanent Effects", "Ocultar efeitos permanentes/infinitos", false);
        showBackground = new BooleanSetting("bg", "Background Box", "Exibir caixa de fundo escuro", false);
        effectSpacing = new NumberSetting("spacing", "Effect Spacing", "Espaçamento vertical entre os efeitos", 4.0, 0.0, 12.0, 1.0);
        scale = new NumberSetting("scale", "Scale", "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);
        textColor = new ColorSetting("color", "Text Color", "Cor padrão do texto", 0xFFFFFFFF);

        addSetting(showIcons);
        addSetting(showNames);
        addSetting(showDuration);
        addSetting(textShadow);
        addSetting(blinkLowDuration);
        addSetting(excludePermanent);
        addSetting(showBackground);
        addSetting(effectSpacing);
        addSetting(scale);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null) return;

        Collection<PotionEffect> activeEffects = mc.thePlayer.getActivePotionEffects();
        if (activeEffects.isEmpty()) return;

        List<PotionEffect> effectsToRender = new ArrayList<>();
        for (PotionEffect pe : activeEffects) {
            if (excludePermanent.isEnabled() && pe.getIsPotionDurationMax()) {
                continue;
            }
            effectsToRender.add(pe);
        }

        if (effectsToRender.isEmpty()) return;

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        int spacing = effectSpacing.getIntValue();
        int itemH = 18;
        int totalH = effectsToRender.size() * (itemH + spacing) - spacing;
        int maxW = 80;

        // Calcula largura máxima para a bounding box do HUD
        for (PotionEffect pe : effectsToRender) {
            Potion potion = Potion.potionTypes[pe.getPotionID()];
            if (potion == null) continue;
            String name = I18n.format(potion.getName());
            if (pe.getAmplifier() == 1) name += " II";
            else if (pe.getAmplifier() == 2) name += " III";
            else if (pe.getAmplifier() == 3) name += " IV";

            String dur = pe.getIsPotionDurationMax() ? "**:**" : Potion.getDurationString(pe);
            int textW = Math.max(mc.fontRendererObj.getStringWidth(name), mc.fontRendererObj.getStringWidth(dur));
            int curW = (showIcons.isEnabled() ? 22 : 0) + textW;
            if (curW > maxW) maxW = curW;
        }

        this.width = (int) ((maxW + 4) * s);
        this.height = (int) ((totalH + 4) * s);

        if (showBackground.isEnabled()) {
            drawHUDBox(renderX - 2, renderY - 2, maxW + 4, totalH + 4);
        }

        int curY = renderY;
        for (PotionEffect pe : effectsToRender) {
            Potion potion = Potion.potionTypes[pe.getPotionID()];
            if (potion == null) continue;

            // Piscar se duração < 10 segundos (200 ticks)
            if (blinkLowDuration.isEnabled() && pe.getDuration() <= 200) {
                int blinkSpeed = pe.getDuration() <= 100 ? 5 : 10;
                if ((pe.getDuration() / blinkSpeed) % 2 == 0) {
                    curY += itemH + spacing;
                    continue;
                }
            }

            int textX = renderX;

            // Renderiza o ícone oficial da poção
            if (showIcons.isEnabled() && potion.hasStatusIcon()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(INVENTORY_TEXTURE);
                int iconIndex = potion.getStatusIconIndex();
                int u = (iconIndex % 8) * 18;
                int v = 198 + (iconIndex / 8) * 18;
                Gui.drawModalRectWithCustomSizedTexture(renderX, curY, u, v, 18, 18, 256, 256);
                textX += 22;
            }

            // Textos
            String name = I18n.format(potion.getName());
            if (pe.getAmplifier() == 1) name += " II";
            else if (pe.getAmplifier() == 2) name += " III";
            else if (pe.getAmplifier() == 3) name += " IV";

            String dur = pe.getIsPotionDurationMax() ? "**:**" : Potion.getDurationString(pe);

            int color = textColor.getValue();
            boolean shadow = textShadow.isEnabled();

            if (showNames.isEnabled() && showDuration.isEnabled()) {
                // Duas linhas empilhadas (estilo Lunar/CheatBreaker)
                mc.fontRendererObj.drawString(name, textX, curY + 1, color, shadow);
                mc.fontRendererObj.drawString("§7" + dur, textX, curY + 10, 0xFFAAAAAA, shadow);
            } else if (showNames.isEnabled()) {
                mc.fontRendererObj.drawString(name, textX, curY + 5, color, shadow);
            } else if (showDuration.isEnabled()) {
                mc.fontRendererObj.drawString(dur, textX, curY + 5, color, shadow);
            }

            curY += itemH + spacing;
        }

        GlStateManager.popMatrix();
    }
}
