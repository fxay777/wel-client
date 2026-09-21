package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModReachDisplay extends BaseMod {
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;
    private double lastReach = 0.0;
    private long lastHitTime = 0;

    public ModReachDisplay() {
        super("reachdisplay", "Reach Display", "Exibe a distância do último hit em blocos", "HUD");
        this.x = 10;
        this.y = 290;
        this.width = 65;
        this.height = 14;

        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showBackground);
        addSetting(textColor);
    }

    public void setReach(double reach) {
        this.lastReach = reach;
        this.lastHitTime = System.currentTimeMillis();
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        // Se acertou recentemente, mostra o hit, senão mede o crosshair target
        double displayVal = lastReach;
        if (System.currentTimeMillis() - lastHitTime > 3000) {
            MovingObjectPosition mop = mc.objectMouseOver;
            if (mop != null && mop.hitVec != null && mc.thePlayer != null) {
                displayVal = mc.thePlayer.getDistance(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
            } else {
                displayVal = 0.0;
            }
        }

        String text = String.format("Alcance: %.2f", displayVal);
        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
