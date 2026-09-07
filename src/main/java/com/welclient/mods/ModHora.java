package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModHora extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public void render(int x, int y) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String hora = "Hora: " + sdf.format(new Date());
        
        mc.fontRendererObj.drawStringWithShadow(hora, x, y, 0xFFFFFFFF);
    }
}