package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Collection;

public class ModPotionEffects extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModPotionEffects() {
        super("potions", "Potion Effects", "Exibe efeitos de poção ativos com tempo restante", "HUD");
        this.x = 10;
        this.y = 250;
        this.width = 90;
        this.height = 30;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFFFFFFFF);

        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) return;

        Collection<PotionEffect> effects = mc.thePlayer.getActivePotionEffects();
        if (effects.isEmpty()) return;

        int rowHeight = 12;
        this.height = Math.max(14, effects.size() * rowHeight + 4);
        int maxW = 80;

        for (PotionEffect pe : effects) {
            Potion potion = Potion.potionTypes[pe.getPotionID()];
            String name = (potion != null) ? I18n.format(potion.getName()) : "Potion";
            String duration = Potion.getDurationString(pe);
            String full = name + " " + (pe.getAmplifier() + 1) + " (" + duration + ")";
            maxW = Math.max(maxW, mc.fontRendererObj.getStringWidth(full) + 8);
        }
        this.width = maxW;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        int curY = y + 3;
        for (PotionEffect pe : effects) {
            Potion potion = Potion.potionTypes[pe.getPotionID()];
            String name = (potion != null) ? I18n.format(potion.getName()) : "Potion";
            String duration = Potion.getDurationString(pe);

            String line = "§f" + name + " §9" + (pe.getAmplifier() + 1) + " §7" + duration;
            mc.fontRendererObj.drawStringWithShadow(line, x + 4, curY, textColor.getValue());
            curY += rowHeight;
        }
    }
}
