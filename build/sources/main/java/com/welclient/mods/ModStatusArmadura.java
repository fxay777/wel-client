package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ModStatusArmadura extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        if (mc.thePlayer == null) return;
        
        ItemStack[] armadura = mc.thePlayer.inventory.armorInventory;
        
        // Renderizar peças da armadura (botas ao capacete)
        for (int i = 0; i < armadura.length; i++) {
            ItemStack peca = armadura[i];
            
            if (peca != null) {
                int yOffset = y + (i * 16);
                
                // Desenhar ícone da armadura
                Gui.drawRect(x, yOffset, x + 14, yOffset + 14, 0x80000000);
                
                // Desenhar durabilidade
                int danoMaximo = peca.getMaxDamage();
                int danoAtual = peca.getItemDamage();
                int durabilidade = danoMaximo - danoAtual;
                
                String textoDurabilidade;
                
                if (durabilidade > danoMaximo * 0.6) {
                    textoDurabilidade = EnumChatFormatting.GREEN + String.valueOf(durabilidade);
                } else if (durabilidade > danoMaximo * 0.3) {
                    textoDurabilidade = EnumChatFormatting.YELLOW + String.valueOf(durabilidade);
                } else {
                    textoDurabilidade = EnumChatFormatting.RED + String.valueOf(durabilidade);
                }
                
                mc.fontRendererObj.drawString(textoDurabilidade, x + 16, yOffset + 3, 0xFFFFFFFF);
            }
        }
    }
}