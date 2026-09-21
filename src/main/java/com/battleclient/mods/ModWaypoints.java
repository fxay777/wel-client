package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModWaypoints extends BaseMod {
    public static ModWaypoints instance;

    public static class Waypoint {
        public String name;
        public double x, y, z;
        public int color;

        public Waypoint(String name, double x, double y, double z, int color) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }

    private final List<Waypoint> waypoints = new ArrayList<>();
    private final BooleanSetting showDistance;
    private final ColorSetting defaultColor;

    public ModWaypoints() {
        super("waypoints", "Waypoints", "Cria e exibe marcadores 3D no mundo com coordenadas e distâncias", "Utility");
        instance = this;

        showDistance = new BooleanSetting("distance", "Exibir Distância em Metros", true);
        defaultColor = new ColorSetting("color", "Cor Padrão", 0xFF0055FF);

        addSetting(showDistance);
        addSetting(defaultColor);

        // Waypoint exemplo de Spawn
        waypoints.add(new Waypoint("Spawn", 0, 64, 0, 0xFF0055FF));
    }

    public void addWaypoint(String name, double x, double y, double z) {
        waypoints.add(new Waypoint(name, x, y, z, defaultColor.getValue()));
    }

    @Override
    public void onWorldRender(RenderWorldLastEvent event) {
        if (mc.thePlayer == null || waypoints.isEmpty()) return;

        RenderManager rm = mc.getRenderManager();
        double px = rm.viewerPosX;
        double py = rm.viewerPosY;
        double pz = rm.viewerPosZ;

        for (Waypoint wp : waypoints) {
            double dx = wp.x - px;
            double dy = wp.y - py;
            double dz = wp.z - pz;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            String label = wp.name + (showDistance.isEnabled() ? String.format(" [%dm]", (int) dist) : "");

            renderWaypointTag(label, dx, dy + 1.5, dz, wp.color, (float) dist);
        }
    }

    private void renderWaypointTag(String str, double x, double y, double z, int color, float dist) {
        FontRenderer font = mc.fontRendererObj;
        float scale = Math.max(1.6F, dist * 0.15F) * 0.0266F;

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
        net.minecraft.client.gui.Gui.drawRect(-strW / 2 - 2, -2, strW / 2 + 2, -1, color);
        font.drawString(str, -strW / 2, 0, color);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        font.drawString(str, -strW / 2, 0, color);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
