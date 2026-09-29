package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModCoordinates extends BaseMod {
    public static ModCoordinates instance;

    private final BooleanSetting showCoordinates;
    private final BooleanSetting hideYCoordinate;
    private final ModeSetting mode; // Vertical ou Horizontal
    private final BooleanSetting showDirection;
    private final BooleanSetting showMarkers; // + / -
    private final ColorSetting coordsColor;
    private final ColorSetting directionColor;
    private final BooleanSetting showBackground;
    private final NumberSetting scale;

    public ModCoordinates() {
        super("coords", "Coordinates", "Exibe coordenadas X, Y, Z e direção com layout limpo e configurável", "HUD");
        instance = this;
        this.x = 4;
        this.y = 4;
        this.width = 60;
        this.height = 30;

        showCoordinates = new BooleanSetting("showCoords", "Coordinates", "Exibir coordenadas", true);
        hideYCoordinate = new BooleanSetting("hideY", "Hide Y Coordinate", "Ocultar coordenada Y", false);
        mode = new ModeSetting("mode", "Mode", "Estilo de exibição", 0, "Vertical", "Horizontal");
        showDirection = new BooleanSetting("showDir", "Direction", "Exibir direção cardeal (N, S, L, O)", true);
        showMarkers = new BooleanSetting("showMarkers", "Show Markers", "Exibir marcadores de eixo (+ / -)", true);
        coordsColor = new ColorSetting("coordsColor", "Coordinates Color", "Cor das coordenadas", 0xFFFFFFFF);
        directionColor = new ColorSetting("dirColor", "Direction Color", "Cor dos marcadores de direção", 0xFFFFFFFF);
        showBackground = new BooleanSetting("bg", "Background Box", "Exibir caixa de fundo escuro", false);
        scale = new NumberSetting("scale", "Scale", "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);

        addSetting(showCoordinates);
        addSetting(hideYCoordinate);
        addSetting(mode);
        addSetting(showDirection);
        addSetting(showMarkers);
        addSetting(coordsColor);
        addSetting(directionColor);
        addSetting(showBackground);
        addSetting(scale);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null || !showCoordinates.isEnabled()) return;

        int xCoord = (int) Math.floor(mc.thePlayer.posX);
        int yCoord = (int) Math.floor(mc.thePlayer.posY);
        int zCoord = (int) Math.floor(mc.thePlayer.posZ);

        // Direção cardeal e marcador (+ / -)
        int facing = MathHelper.floor_double((double) (mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        String dirLetter = "N"; // 0: S, 1: W, 2: N, 3: E no vanilla
        String marker = "+";
        switch (facing) {
            case 0:
                dirLetter = "S";
                marker = "+";
                break;
            case 1:
                dirLetter = "W";
                marker = "-";
                break;
            case 2:
                dirLetter = "N";
                marker = "-";
                break;
            case 3:
                dirLetter = "E";
                marker = "+";
                break;
        }

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        int cColor = coordsColor.getValue();
        int dColor = directionColor.getValue();

        if (mode.is("Vertical")) {
            // Estilo idêntico à Print 1 e Print 4:
            // X: -260   +
            // Y: 4      N
            // Z: 568
            String xLine = "X: " + xCoord;
            String yLine = "Y: " + yCoord;
            String zLine = "Z: " + zCoord;

            int maxTextW = Math.max(mc.fontRendererObj.getStringWidth(xLine),
                    Math.max(hideYCoordinate.isEnabled() ? 0 : mc.fontRendererObj.getStringWidth(yLine),
                            mc.fontRendererObj.getStringWidth(zLine)));

            int dirColOffset = maxTextW + 8;
            int totalW = dirColOffset + 14;
            int totalH = hideYCoordinate.isEnabled() ? 20 : 30;

            this.width = (int) (totalW * s);
            this.height = (int) (totalH * s);

            if (showBackground.isEnabled()) {
                drawHUDBox(renderX - 2, renderY - 2, totalW + 4, totalH + 4);
            }

            int curY = renderY;
            mc.fontRendererObj.drawStringWithShadow(xLine, renderX, curY, cColor);
            if (showMarkers.isEnabled()) {
                mc.fontRendererObj.drawStringWithShadow(marker, renderX + dirColOffset, curY, dColor);
            }
            curY += 10;

            if (!hideYCoordinate.isEnabled()) {
                mc.fontRendererObj.drawStringWithShadow(yLine, renderX, curY, cColor);
                if (showDirection.isEnabled()) {
                    mc.fontRendererObj.drawStringWithShadow(dirLetter, renderX + dirColOffset, curY, dColor);
                }
                curY += 10;
            } else if (showDirection.isEnabled()) {
                mc.fontRendererObj.drawStringWithShadow(dirLetter, renderX + dirColOffset, renderY, dColor);
            }

            mc.fontRendererObj.drawStringWithShadow(zLine, renderX, curY, cColor);

        } else {
            // Modo Horizontal
            StringBuilder sb = new StringBuilder();
            sb.append("X: ").append(xCoord);
            if (!hideYCoordinate.isEnabled()) {
                sb.append(" Y: ").append(yCoord);
            }
            sb.append(" Z: ").append(zCoord);
            if (showDirection.isEnabled()) {
                sb.append(" (").append(dirLetter);
                if (showMarkers.isEnabled()) sb.append(" ").append(marker);
                sb.append(")");
            }

            String full = sb.toString();
            int totalW = mc.fontRendererObj.getStringWidth(full) + 6;
            int totalH = 13;

            this.width = (int) (totalW * s);
            this.height = (int) (totalH * s);

            if (showBackground.isEnabled()) {
                drawHUDBox(renderX - 2, renderY - 2, totalW, totalH);
            }

            mc.fontRendererObj.drawStringWithShadow(full, renderX, renderY + 2, cColor);
        }

        GlStateManager.popMatrix();
    }
}
