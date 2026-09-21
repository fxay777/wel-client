package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ModeSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModArmorStatus extends BaseMod {
    private final BooleanSetting showHeldItem;
    private final BooleanSetting showDamageNumbers;
    private final ModeSetting orientation;

    public ModArmorStatus() {
        super("armorstatus", "Armor Status", "Exibe as peças de armadura e o item na mão com durabilidade", "HUD");
        this.x = 10;
        this.y = 160;
        this.width = 40;
        this.height = 75;

        showHeldItem = new BooleanSetting("showHeld", "Mostrar Item na Mão", true);
        showDamageNumbers = new BooleanSetting("showDamage", "Mostrar Valor de Durabilidade", true);
        orientation = new ModeSetting("orientation", "Orientação", 0, "Vertical", "Horizontal");

        addSetting(showHeldItem);
        addSetting(showDamageNumbers);
        addSetting(orientation);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) return;

        ItemStack[] armor = mc.thePlayer.inventory.armorInventory;
        ItemStack held = mc.thePlayer.getHeldItem();

        boolean isVert = orientation.is("Vertical");
        int curX = x;
        int curY = y;

        int count = 4 + (showHeldItem.isEnabled() ? 1 : 0);
        if (isVert) {
            this.width = 44;
            this.height = count * 18;
        } else {
            this.width = count * 36;
            this.height = 20;
        }

        drawHUDBox(x, y, width, height);

        // Capacete até Botas (índices 3 até 0)
        for (int i = 3; i >= 0; i--) {
            renderItemSlot(armor[i], curX, curY);
            if (isVert) curY += 18;
            else curX += 36;
        }

        if (showHeldItem.isEnabled()) {
            renderItemSlot(held, curX, curY);
        }
    }

    private void renderItemSlot(ItemStack stack, int sx, int sy) {
        if (stack == null) return;

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, sx + 2, sy + 1);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        if (showDamageNumbers.isEnabled() && stack.isItemStackDamageable()) {
            int max = stack.getMaxDamage();
            int current = max - stack.getItemDamage();
            int color = 0xFF00FF00;
            if (current < max * 0.3) color = 0xFFFF0000;
            else if (current < max * 0.6) color = 0xFFFFFF00;

            String dur = String.valueOf(current);
            mc.fontRendererObj.drawStringWithShadow(dur, sx + 20, sy + 5, color);
        }
    }
}
