package com.battleclient.mods;

import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ModTNTCountdown extends BaseMod {
    public static ModTNTCountdown instance;

    private final ColorSetting textColor;

    public ModTNTCountdown() {
        super("tntcountdown", "TNT Countdown", "Exibe a contagem regressiva em segundos acima de TNTs ativados", "Utility");
        instance = this;

        textColor = new ColorSetting("color", "Cor do Texto", 0xFFFF2222);
        addSetting(textColor);
    }

    @Override
    public void onWorldRender(RenderWorldLastEvent event) {
        if (mc.theWorld == null) return;

        RenderManager rm = mc.getRenderManager();
        double px = rm.viewerPosX;
        double py = rm.viewerPosY;
        double pz = rm.viewerPosZ;

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityTNTPrimed) {
                EntityTNTPrimed tnt = (EntityTNTPrimed) entity;
                double x = tnt.lastTickPosX + (tnt.posX - tnt.lastTickPosX) * event.partialTicks - px;
                double y = tnt.lastTickPosY + (tnt.posY - tnt.lastTickPosY) * event.partialTicks - py;
                double z = tnt.lastTickPosZ + (tnt.posZ - tnt.lastTickPosZ) * event.partialTicks - pz;

                float seconds = tnt.fuse / 20.0F;
                String text = String.format("%.1fs", seconds);

                renderTNTTag(text, x, y + 1.2, z);
            }
        }
    }

    private void renderTNTTag(String str, double x, double y, double z) {
        FontRenderer font = mc.fontRendererObj;
        float scale = 0.0266F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int strW = font.getStringWidth(str);
        net.minecraft.client.gui.Gui.drawRect(-strW / 2 - 2, -2, strW / 2 + 2, 9, 0x90000000);
        font.drawString(str, -strW / 2, 0, textColor.getValue());

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        font.drawString(str, -strW / 2, 0, textColor.getValue());
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
