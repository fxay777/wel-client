package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;

public class ModDirecao extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        if (mc.thePlayer == null) return;
        
        EnumFacing direcao = mc.thePlayer.getHorizontalFacing();
        String textoDirecao;
        
        switch (direcao) {
            case NORTH:
                textoDirecao = "Norte (Z-)";
                break;
            case SOUTH:
                textoDirecao = "Sul (Z+)";
                break;
            case EAST:
                textoDirecao = "Leste (X+)";
                break;
            case WEST:
                textoDirecao = "Oeste (X-)";
                break;
            default:
                textoDirecao = "Desconhecida";
        }
        
        mc.fontRendererObj.drawStringWithShadow("Direção: " + textoDirecao, x, y, 0xFFFFFFFF);
    }
}