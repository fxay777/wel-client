package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import java.text.DecimalFormat;

public class ModCoordenadas extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        if (mc.thePlayer == null) return;
        
        DecimalFormat df = new DecimalFormat("#.#");
        String coords = String.format("XYZ: %s, %s, %s", 
            df.format(mc.thePlayer.posX),
            df.format(mc.thePlayer.posY),
            df.format(mc.thePlayer.posZ));
        
        mc.fontRendererObj.drawStringWithShadow(coords, x, y, 0xFFFFFFFF);
    }
}