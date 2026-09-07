package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.PotionEffect;
import java.util.Collection;

public class ModPotions extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        if (mc.thePlayer == null) return;
        
        Collection<PotionEffect> potions = mc.thePlayer.getActivePotionEffects();
        
        if (potions.isEmpty()) return;
        
        int yOffset = y;
        for (PotionEffect potion : potions) {
            String nomePotion = potion.getEffectName();
            int duracao = potion.getDuration() / 20; // Converter para segundos
            int nivel = potion.getAmplifier() + 1;
            
            String texto = String.format("%s %s (%ds)", nomePotion, nivel, duracao);
            
            mc.fontRendererObj.drawStringWithShadow(texto, x, yOffset, 0xFFFFFFFF);
            yOffset += 10;
        }
    }
}