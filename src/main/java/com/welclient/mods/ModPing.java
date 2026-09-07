package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;

public class ModPing extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;
        
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        
        if (info != null) {
            int ping = info.getResponseTime();
            String pingText = "Ping: " + ping + "ms";
            
            int cor;
            if (ping < 50) {
                cor = 0xFF00FF00; // Verde - Excelente
            } else if (ping < 100) {
                cor = 0xFFFFFF00; // Amarelo - Bom
            } else if (ping < 200) {
                cor = 0xFFFF8800; // Laranja - Médio
            } else {
                cor = 0xFFFF0000; // Vermelho - Ruim
            }
            
            mc.fontRendererObj.drawStringWithShadow(pingText, x, y, cor);
        }
    }
}