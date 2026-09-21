package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class ModHitBox extends BaseMod {
    public static ModHitBox instance;

    private final ColorSetting boxColor;
    private final BooleanSetting showEyeHeight;
    private final BooleanSetting playersOnly;

    public ModHitBox() {
        super("hitbox", "HitBox", "Renderiza a caixa de colisão (hitbox) e linha de visão das entidades", "Visual");
        instance = this;

        boxColor = new ColorSetting("color", "Cor da HitBox", 0xFF0055FF);
        showEyeHeight = new BooleanSetting("eyeHeight", "Mostrar Linha dos Olhos", true);
        playersOnly = new BooleanSetting("playersOnly", "Apenas Jogadores", true);

        addSetting(boxColor);
        addSetting(showEyeHeight);
        addSetting(playersOnly);
    }

    @Override
    public void onWorldRender(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        RenderManager rm = mc.getRenderManager();
        double renderPosX = rm.viewerPosX;
        double renderPosY = rm.viewerPosY;
        double renderPosZ = rm.viewerPosZ;

        int color = boxColor.getValue();
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glLineWidth(1.5F);

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity == mc.thePlayer) continue;
            if (playersOnly.isEnabled() && !(entity instanceof EntityPlayer)) continue;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks - renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks - renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks - renderPosZ;

            AxisAlignedBB bb = entity.getEntityBoundingBox();
            AxisAlignedBB renderBB = new AxisAlignedBB(
                    bb.minX - entity.posX + x,
                    bb.minY - entity.posY + y,
                    bb.minZ - entity.posZ + z,
                    bb.maxX - entity.posX + x,
                    bb.maxY - entity.posY + y,
                    bb.maxZ - entity.posZ + z
            );

            RenderGlobal.drawOutlinedBoundingBox(renderBB, (int)(r * 255), (int)(g * 255), (int)(b * 255), 255);

            if (showEyeHeight.isEnabled() && entity instanceof EntityLivingBase) {
                float eye = entity.getEyeHeight();
                AxisAlignedBB eyeBB = new AxisAlignedBB(
                        renderBB.minX, renderBB.minY + eye - 0.01, renderBB.minZ,
                        renderBB.maxX, renderBB.minY + eye + 0.01, renderBB.maxZ
                );
                RenderGlobal.drawOutlinedBoundingBox(eyeBB, 255, 0, 0, 255);
            }
        }

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
