package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * ModArmorStatus — Lunar Client style
 *
 * Vertical layout (default):
 *   [Helmet icon]    120 / 55%     ← green / yellow / red depending on durability
 *   [Chestplate icon] 220
 *   [Leggings icon]   42
 *   [Boots icon]      15
 *   [Held item icon]  64
 */
public class ModArmorStatus extends BaseMod {
    public static ModArmorStatus instance;

    // ─── Settings ─────────────────────────────────────────────────────────────
    private final BooleanSetting showHeldItem;
    private final BooleanSetting showIcons;
    private final BooleanSetting showDamageNumbers;
    private final BooleanSetting dynamicDamageColor;
    private final BooleanSetting showPercent;       // % instead of absolute value
    private final BooleanSetting compactMode;       // 12×12 icons, smaller text
    private final BooleanSetting glowOnLow;         // blink when durability < 25 %
    private final BooleanSetting showBackground;
    private final NumberSetting  scale;
    private final ColorSetting   textColor;

    public ModArmorStatus() {
        super("armorstatus", "Armor Status",
              "Exibe as pecas de armadura e o item da mao com durabilidade e cores dinamicas", "HUD");
        instance  = this;
        this.x    = 10;
        this.y    = 160;
        this.width  = 54;
        this.height = 90;

        showHeldItem        = new BooleanSetting("showHeld",     "Show Held Item",       "Exibir item segurado na mao", true);
        showIcons           = new BooleanSetting("showIcons",    "Show Icons",           "Exibir icone do item", true);
        showDamageNumbers   = new BooleanSetting("showDamage",   "Show Durability",      "Exibir numero de durabilidade", true);
        dynamicDamageColor  = new BooleanSetting("dynamicColor", "Dynamic Color",        "Mudar cor conforme a armadura desgasta", true);
        showPercent         = new BooleanSetting("showPercent",  "Show Percent",         "Mostrar porcentagem ao inves de valor absoluto", false);
        compactMode         = new BooleanSetting("compact",      "Compact Mode",         "Icones 12x12 e texto menor", false);
        glowOnLow           = new BooleanSetting("glowOnLow",    "Blink On Low Durability", "Piscar numero quando durabilidade < 25%", true);
        showBackground      = new BooleanSetting("bg",           "Background Box",       "Exibir caixa de fundo escuro", false);
        scale               = new NumberSetting ("scale",        "Scale",                "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);
        textColor           = new ColorSetting  ("color",        "Text Color",           "Cor padrao do texto", 0xFFFFFFFF);

        addSetting(showHeldItem);
        addSetting(showIcons);
        addSetting(showDamageNumbers);
        addSetting(dynamicDamageColor);
        addSetting(showPercent);
        addSetting(compactMode);
        addSetting(glowOnLow);
        addSetting(showBackground);
        addSetting(scale);
        addSetting(textColor);
    }

    // ─── Render ───────────────────────────────────────────────────────────────
    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null) return;

        ItemStack[] armor  = mc.thePlayer.inventory.armorInventory;
        ItemStack   held   = mc.thePlayer.getHeldItem();

        boolean compact    = compactMode.isEnabled();
        int     iconSize   = compact ? 12 : 16;
        int     rowH       = compact ? 14 : 18;
        int     gap        = compact ? 1  : 2;

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        // Count rows that will actually render
        int rows = 4;
        if (showHeldItem.isEnabled() && held != null) rows++;

        int totalW = showIcons.isEnabled() ? (iconSize + 4 + 36) : 40;
        int totalH = rows * rowH + (rows - 1) * gap;

        this.width  = (int) (totalW * s);
        this.height = (int) (totalH * s);

        if (showBackground.isEnabled()) {
            drawHUDBox(renderX - 2, renderY - 2, totalW + 4, totalH + 4);
        }

        int curY = renderY;

        // Helmet=3, Chestplate=2, Leggings=1, Boots=0
        for (int i = 3; i >= 0; i--) {
            renderArmorRow(armor[i], renderX, curY, iconSize, rowH);
            curY += rowH + gap;
        }

        if (showHeldItem.isEnabled() && held != null) {
            renderArmorRow(held, renderX, curY, iconSize, rowH);
        }

        GlStateManager.popMatrix();
    }

    // ─── Per-row rendering ────────────────────────────────────────────────────
    private void renderArmorRow(ItemStack stack, int rx, int ry, int iconSize, int rowH) {
        if (stack == null) return;

        // ── Icon ─────────────────────────────────────────────────────────────
        if (showIcons.isEnabled()) {
            GlStateManager.pushMatrix();
            if (iconSize < 16) {
                // Scale down from 16 → iconSize
                float iconScale = iconSize / 16.0F;
                GlStateManager.translate(rx, ry, 0.0F);
                GlStateManager.scale(iconScale, iconScale, 1.0F);
                GlStateManager.translate(-rx, -ry, 0.0F);
            }
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, rx, ry);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, rx, ry);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }

        // ── Durability text ───────────────────────────────────────────────────
        if (showDamageNumbers.isEnabled() && stack.isItemStackDamageable()) {
            int max     = stack.getMaxDamage();
            int current = max - stack.getItemDamage();
            float ratio = (float) current / (float) max;

            // Blink on low durability
            if (glowOnLow.isEnabled() && ratio < 0.25F) {
                // Use tick-based visibility to avoid importing System.currentTimeMillis every draw
                long now = System.currentTimeMillis();
                if ((now / 400L) % 2 == 0) return; // skip draw = blink
            }

            String text;
            if (showPercent.isEnabled()) {
                text = (int)(ratio * 100) + "%";
            } else {
                text = String.valueOf(current);
            }

            int color = textColor.getValue();
            if (dynamicDamageColor.isEnabled()) {
                if (ratio > 0.60F)      color = 0xFF55FF55; // Green
                else if (ratio > 0.25F) color = 0xFFFFFF55; // Yellow
                else                    color = 0xFFFF5555; // Red
            }

            int textX = rx + (showIcons.isEnabled() ? (iconSize + 4) : 0);
            int textY = ry + (rowH - 8) / 2;
            mc.fontRendererObj.drawStringWithShadow(text, textX, textY, color);
        }
    }
}
