package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ModFPS extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        int fps = Minecraft.getDebugFPS();
        String textoFPS = "FPS: " + fps;
        
        // Código de cor baseado no FPS
        int cor;
        if (fps >= 60) {
            cor = 0xFF00FF00; // Verde
        } else if (fps >= 30) {
            cor = 0xFFFFFF00; // Amarelo
        } else {
            cor = 0xFFFF0000; // Vermelho
        }
        
        mc.fontRendererObj.drawStringWithShadow(textoFPS, x, y, cor);
    }
}