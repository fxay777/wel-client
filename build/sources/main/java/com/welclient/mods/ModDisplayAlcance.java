package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MovingObjectPosition;

public class ModDisplayAlcance extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void render(int x, int y) {
        MovingObjectPosition alvo = mc.objectMouseOver;
        String texto = "Alcance: -";

        if (alvo != null && mc.thePlayer != null) {
            double alcance = mc.thePlayer.getDistance(alvo.hitVec.xCoord, alvo.hitVec.yCoord, alvo.hitVec.zCoord);
            texto = String.format("Alcance: %.1f", alcance);
        }

        mc.fontRendererObj.drawStringWithShadow(texto, x, y, 0xFFFFFFFF);
    }
}
