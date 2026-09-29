package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModReachDisplay extends BaseMod {
    public static ModReachDisplay instance;

    private final ModeSetting displayFormat;
    private final BooleanSetting dynamicColor;
    private final BooleanSetting showBackground;
    private final NumberSetting displayDuration; // segundos
    private final NumberSetting scale;
    private final ColorSetting textColor;

    private double lastReachDistance = 0.0;
    private long lastHitTime = 0;

    public ModReachDisplay() {
        super("reachdisplay", "Reach Display", "Exibe a distância exata de alcance dos seus ataques em combate PvP", "HUD");
        instance = this;
        this.x = 10;
        this.y = 290;
        this.width = 65;
        this.height = 14;

        displayFormat = new ModeSetting("format", "Format", "Estilo de texto", 0, "X.XX blocks", "Reach: X.XX", "X.XXm");
        dynamicColor = new BooleanSetting("dynamicColor", "Dynamic Color", "Cor muda conforme a distância do golpe", true);
        showBackground = new BooleanSetting("bg", "Background Box", "Exibir caixa de fundo escuro", false);
        displayDuration = new NumberSetting("duration", "Display Time (s)", "Tempo de exibição antes de sumir", 2.5, 1.0, 5.0, 0.5);
        scale = new NumberSetting("scale", "Scale", "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);
        textColor = new ColorSetting("color", "Text Color", "Cor padrão do texto", 0xFF00AAFF);

        addSetting(displayFormat);
        addSetting(dynamicColor);
        addSetting(showBackground);
        addSetting(displayDuration);
        addSetting(scale);
        addSetting(textColor);
    }

    public void onPlayerAttack(Entity target) {
        if (!enabled || mc.thePlayer == null || target == null) return;

        Vec3 eyes = mc.thePlayer.getPositionEyes(1.0F);
        MovingObjectPosition mop = mc.objectMouseOver;

        double dist;
        if (mop != null && mop.hitVec != null) {
            dist = eyes.distanceTo(mop.hitVec);
        } else {
            // Calcula distância até o ponto mais próximo da bounding box do alvo
            AxisAlignedBB bb = target.getEntityBoundingBox();
            double cx = Math.max(bb.minX, Math.min(eyes.xCoord, bb.maxX));
            double cy = Math.max(bb.minY, Math.min(eyes.yCoord, bb.maxY));
            double cz = Math.max(bb.minZ, Math.min(eyes.zCoord, bb.maxZ));
            dist = eyes.distanceTo(new Vec3(cx, cy, cz));
        }

        // Limita a valores plausíveis de Minecraft vanilla (max ~4.5 blocks)
        if (dist > 0.05 && dist < 7.0) {
            this.lastReachDistance = dist;
            this.lastHitTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null) return;

        long elapsed = System.currentTimeMillis() - lastHitTime;
        long maxDurationMs = (long) (displayDuration.getValue() * 1000);

        if (lastHitTime == 0 || elapsed > maxDurationMs) {
            return;
        }

        String text;
        if (displayFormat.is("Reach: X.XX")) {
            text = String.format("Reach: %.2f", lastReachDistance);
        } else if (displayFormat.is("X.XXm")) {
            text = String.format("%.2fm", lastReachDistance);
        } else {
            text = String.format("%.2f blocks", lastReachDistance);
        }

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        int strW = mc.fontRendererObj.getStringWidth(text);
        this.width = (int) ((strW + 8) * s);
        this.height = (int) (14 * s);

        if (showBackground.isEnabled()) {
            drawHUDBox(renderX - 2, renderY - 2, strW + 4, 14);
        }

        int color = textColor.getValue();
        if (dynamicColor.isEnabled()) {
            if (lastReachDistance >= 3.0) {
                color = 0xFF55FFFF; // Ciano / Azul alcance máximo
            } else if (lastReachDistance >= 2.5) {
                color = 0xFF55FF55; // Verde
            } else {
                color = 0xFFFFFF55; // Amarelo
            }
        }

        mc.fontRendererObj.drawStringWithShadow(text, renderX + 2, renderY + 3, color);
        GlStateManager.popMatrix();
    }
}
