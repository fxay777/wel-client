package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModArmorStatus extends BaseMod {
    public static ModArmorStatus instance;

    private final BooleanSetting showHeldItem;
    private final BooleanSetting showDamageNumbers;
    private final BooleanSetting dynamicDamageColor;
    private final ModeSetting textAlignment; // Left of Icon ou Right of Icon
    private final ModeSetting damageDisplayMode; // Value, Percent
    private final ModeSetting orientation; // Vertical ou Horizontal
    private final BooleanSetting showBackground;
    private final NumberSetting scale;
    private final ColorSetting textColor;

    public ModArmorStatus() {
        super("armorstatus", "Armor Status", "Exibe as peças de armadura e o item da mão com contagem e durabilidade", "HUD");
        instance = this;
        this.x = 10;
        this.y = 160;
        this.width = 50;
        this.height = 80;

        showHeldItem = new BooleanSetting("showHeld", "Show Held Item", "Exibir item segurado na mão", true);
        showDamageNumbers = new BooleanSetting("showDamage", "Show Damage Numbers", "Exibir número de durabilidade", true);
        dynamicDamageColor = new BooleanSetting("dynamicColor", "Damage Dynamic Color", "Mudar cor conforme a armadura desgasta", true);
        textAlignment = new ModeSetting("textAlign", "Text Alignment", "Posição do texto em relação ao ícone", 0, "Esquerda", "Direita");
        damageDisplayMode = new ModeSetting("damageMode", "Damage Display Mode", "Formato da durabilidade", 0, "Valor Restante", "Porcentagem");
        orientation = new ModeSetting("orientation", "Orientation", "Orientação dos itens", 0, "Vertical", "Horizontal");
        showBackground = new BooleanSetting("bg", "Background Box", "Exibir caixa de fundo escuro", false);
        scale = new NumberSetting("scale", "Scale", "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);
        textColor = new ColorSetting("color", "Text Color", "Cor padrão do texto", 0xFFFFFFFF);

        addSetting(showHeldItem);
        addSetting(showDamageNumbers);
        addSetting(dynamicDamageColor);
        addSetting(textAlignment);
        addSetting(damageDisplayMode);
        addSetting(orientation);
        addSetting(showBackground);
        addSetting(scale);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null) return;

        ItemStack[] armor = mc.thePlayer.inventory.armorInventory;
        ItemStack held = mc.thePlayer.getHeldItem();

        boolean isVert = orientation.is("Vertical");
        boolean textLeft = textAlignment.is("Esquerda");

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        int count = 4 + (showHeldItem.isEnabled() && held != null ? 1 : 0);
        int slotSize = 18;

        int totalW = isVert ? 52 : (count * 38);
        int totalH = isVert ? (count * slotSize) : slotSize;

        this.width = (int) (totalW * s);
        this.height = (int) (totalH * s);

        if (showBackground.isEnabled()) {
            drawHUDBox(renderX - 2, renderY - 2, totalW, totalH);
        }

        int curX = renderX;
        int curY = renderY;

        // Capacete (3), Peitoral (2), Calças (1), Botas (0)
        for (int i = 3; i >= 0; i--) {
            renderSlot(armor[i], curX, curY, textLeft);
            if (isVert) curY += slotSize;
            else curX += 38;
        }

        if (showHeldItem.isEnabled() && held != null) {
            renderSlot(held, curX, curY, textLeft);
        }

        GlStateManager.popMatrix();
    }

    private void renderSlot(ItemStack stack, int sx, int sy, boolean textLeft) {
        if (stack == null) return;

        int iconX = textLeft ? (sx + 32) : sx;
        int textX = textLeft ? sx : (sx + 20);

        // Renderiza o item
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, iconX, sy);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, iconX, sy);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        // Renderiza a durabilidade / contagem
        if (showDamageNumbers.isEnabled() && stack.isItemStackDamageable()) {
            int max = stack.getMaxDamage();
            int current = max - stack.getItemDamage();

            String text;
            if (damageDisplayMode.is("Porcentagem")) {
                int pct = (int) ((current / (float) max) * 100);
                text = pct + "%";
            } else {
                text = String.valueOf(current);
            }

            int color = textColor.getValue();
            if (dynamicDamageColor.isEnabled()) {
                float ratio = (float) current / (float) max;
                if (ratio > 0.6F) color = 0xFF55FF55; // Verde
                else if (ratio > 0.25F) color = 0xFFFFFF55; // Amarelo
                else color = 0xFFFF5555; // Vermelho
            }

            int strW = mc.fontRendererObj.getStringWidth(text);
            int drawX = textLeft ? (iconX - strW - 3) : textX;
            mc.fontRendererObj.drawStringWithShadow(text, drawX, sy + 5, color);
        } else if (stack.stackSize > 1 && !stack.isItemStackDamageable()) {
            String text = String.valueOf(stack.stackSize);
            int strW = mc.fontRendererObj.getStringWidth(text);
            int drawX = textLeft ? (iconX - strW - 3) : textX;
            mc.fontRendererObj.drawStringWithShadow(text, drawX, sy + 5, textColor.getValue());
        }
    }
}
