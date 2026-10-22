package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModCoordinates extends BaseMod {
    public static ModCoordinates instance;

    // Axis colours (used when colorByAxis is ON)
    private static final int COLOR_X   = 0xFFFF5555; // Red
    private static final int COLOR_Y   = 0xFF55FF55; // Green
    private static final int COLOR_Z   = 0xFF5555FF; // Blue
    private static final int COLOR_VAL = 0xFFFFFFFF; // White for values
    private static final int PAD = 3;

    private final BooleanSetting showCoordinates;
    private final BooleanSetting hideYCoordinate;
    private final ModeSetting    mode;         // Vertical | Horizontal
    private final BooleanSetting showDirection;
    private final BooleanSetting showMarkers;  // + / -
    private final BooleanSetting colorByAxis;  // Per-axis colours
    private final ColorSetting   coordsColor;
    private final ColorSetting   directionColor;
    private final BooleanSetting showBackground;
    private final NumberSetting  scale;

    public ModCoordinates() {
        super("coords", "Coordinates",
              "Exibe coordenadas X, Y, Z e direcao com layout limpo e configuravel", "HUD");
        instance = this;
        this.x = 4;
        this.y = 4;
        this.width  = 60;
        this.height = 30;

        showCoordinates = new BooleanSetting("showCoords",  "Coordinates",       "Exibir coordenadas", true);
        hideYCoordinate = new BooleanSetting("hideY",       "Hide Y Coordinate", "Ocultar coordenada Y", false);
        mode            = new ModeSetting  ("mode",         "Mode",              "Estilo de exibicao", 0, "Vertical", "Horizontal");
        showDirection   = new BooleanSetting("showDir",     "Direction",         "Exibir direcao cardeal (N, S, L, O)", true);
        showMarkers     = new BooleanSetting("showMarkers", "Show Markers",      "Exibir marcadores de eixo (+ / -)", true);
        colorByAxis     = new BooleanSetting("colorByAxis", "Color by Axis",     "Colorir cada eixo com sua cor (X=vermelho, Y=verde, Z=azul)", true);
        coordsColor     = new ColorSetting  ("coordsColor", "Coordinates Color", "Cor das coordenadas (quando Color by Axis desligado)", 0xFFFFFFFF);
        directionColor  = new ColorSetting  ("dirColor",    "Direction Color",   "Cor dos marcadores de direcao e marcador", 0xFFFFFF55);
        showBackground  = new BooleanSetting("bg",          "Background Box",    "Exibir caixa de fundo escuro com padding", false);
        scale           = new NumberSetting ("scale",       "Scale",             "Tamanho do HUD", 1.0, 0.5, 2.0, 0.1);

        addSetting(showCoordinates);
        addSetting(hideYCoordinate);
        addSetting(mode);
        addSetting(showDirection);
        addSetting(showMarkers);
        addSetting(colorByAxis);
        addSetting(coordsColor);
        addSetting(directionColor);
        addSetting(showBackground);
        addSetting(scale);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!enabled || mc.thePlayer == null || !showCoordinates.isEnabled()) return;

        final int xCoord = (int) Math.floor(mc.thePlayer.posX);
        final int yCoord = (int) Math.floor(mc.thePlayer.posY);
        final int zCoord = (int) Math.floor(mc.thePlayer.posZ);

        // Cardinal direction and ± marker
        int facing = MathHelper.floor_double((double)(mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        String dirLetter;
        String marker;
        switch (facing) {
            case 0:  dirLetter = "S"; marker = "+"; break;
            case 1:  dirLetter = "W"; marker = "-"; break;
            case 2:  dirLetter = "N"; marker = "-"; break;
            default: dirLetter = "E"; marker = "+"; break;
        }

        float s = scale.getFloatValue();
        GlStateManager.pushMatrix();
        GlStateManager.scale(s, s, s);

        int renderX = (int) (x / s);
        int renderY = (int) (y / s);

        int cColor   = coordsColor.getValue();
        int dColor   = directionColor.getValue();
        boolean byAxis = colorByAxis.isEnabled();

        if (mode.is("Vertical")) {
            renderVertical(renderX, renderY, xCoord, yCoord, zCoord,
                           dirLetter, marker, cColor, dColor, byAxis, s);
        } else {
            renderHorizontal(renderX, renderY, xCoord, yCoord, zCoord,
                             dirLetter, marker, cColor, dColor, byAxis, s);
        }

        GlStateManager.popMatrix();
    }

    // ─── Vertical mode ───────────────────────────────────────────────────────
    //  X  -260  +
    //  Y    4   N    (direction on same line as Y)
    //  Z  568
    private void renderVertical(int rx, int ry,
                                int xCoord, int yCoord, int zCoord,
                                String dir, String marker,
                                int cColor, int dColor, boolean byAxis, float s) {

        String xVal = String.valueOf(xCoord);
        String yVal = String.valueOf(yCoord);
        String zVal = String.valueOf(zCoord);

        // Label widths (single letter)
        int lbW = mc.fontRendererObj.getStringWidth("X ");
        int maxValW = Math.max(mc.fontRendererObj.getStringWidth(xVal),
                     Math.max(hideYCoordinate.isEnabled() ? 0 : mc.fontRendererObj.getStringWidth(yVal),
                              mc.fontRendererObj.getStringWidth(zVal)));

        // Column positions
        int colVal = rx + lbW;                         // numbers start after label
        int colRight = colVal + maxValW + 6;           // ± and direction column
        int markerW  = mc.fontRendererObj.getStringWidth(marker);
        int dirW     = mc.fontRendererObj.getStringWidth(dir);
        int rightW   = Math.max(markerW, dirW);

        int totalW = colRight + rightW + 4;
        boolean showY = !hideYCoordinate.isEnabled();
        int rowCount  = showY ? 3 : 2;
        int totalH    = rowCount * 10;

        this.width  = (int) (totalW * s);
        this.height = (int) (totalH * s);

        if (showBackground.isEnabled()) {
            Gui.drawRect(rx - PAD, ry - PAD, rx + totalW + PAD, ry + totalH + PAD, 0x80000000);
        }

        int curY = ry;

        // ---- X row ----
        int xLabelColor = byAxis ? COLOR_X : cColor;
        int xValColor   = byAxis ? COLOR_VAL : cColor;
        mc.fontRendererObj.drawStringWithShadow("X", rx, curY, xLabelColor);
        mc.fontRendererObj.drawStringWithShadow(xVal, colVal, curY, xValColor);
        if (showMarkers.isEnabled()) {
            mc.fontRendererObj.drawStringWithShadow(marker, colRight, curY, dColor);
        }
        curY += 10;

        // ---- Y row (with direction) ----
        if (showY) {
            int yLabelColor = byAxis ? COLOR_Y : cColor;
            int yValColor   = byAxis ? COLOR_VAL : cColor;
            mc.fontRendererObj.drawStringWithShadow("Y", rx, curY, yLabelColor);
            mc.fontRendererObj.drawStringWithShadow(yVal, colVal, curY, yValColor);
            if (showDirection.isEnabled()) {
                mc.fontRendererObj.drawStringWithShadow(dir, colRight, curY, dColor);
            }
            curY += 10;
        }

        // ---- Z row ----
        int zLabelColor = byAxis ? COLOR_Z : cColor;
        int zValColor   = byAxis ? COLOR_VAL : cColor;
        mc.fontRendererObj.drawStringWithShadow("Z", rx, curY, zLabelColor);
        mc.fontRendererObj.drawStringWithShadow(zVal, colVal, curY, zValColor);
    }

    // ─── Horizontal mode ─────────────────────────────────────────────────────
    //  X: -260  Y: 4  Z: 568  N
    private void renderHorizontal(int rx, int ry,
                                  int xCoord, int yCoord, int zCoord,
                                  String dir, String marker,
                                  int cColor, int dColor, boolean byAxis, float s) {

        int curX = rx;

        // Measure total width first
        int totalW = measureHorizontalWidth(xCoord, yCoord, zCoord, dir, marker);
        int totalH = 13;
        this.width  = (int) ((totalW + PAD * 2) * s);
        this.height = (int) (totalH * s);

        if (showBackground.isEnabled()) {
            Gui.drawRect(rx - PAD, ry - PAD, rx + totalW + PAD, ry + totalH - PAD + 1, 0x80000000);
        }

        int baseY = ry + 2;

        // X
        int xLabelColor = byAxis ? COLOR_X   : cColor;
        int xValColor   = byAxis ? COLOR_VAL : cColor;
        curX = drawSegment(curX, baseY, "X: ", String.valueOf(xCoord), xLabelColor, xValColor);

        // Y
        if (!hideYCoordinate.isEnabled()) {
            int yLabelColor = byAxis ? COLOR_Y   : cColor;
            int yValColor   = byAxis ? COLOR_VAL : cColor;
            curX = drawSegment(curX, baseY, "  Y: ", String.valueOf(yCoord), yLabelColor, yValColor);
        }

        // Z
        int zLabelColor = byAxis ? COLOR_Z   : cColor;
        int zValColor   = byAxis ? COLOR_VAL : cColor;
        curX = drawSegment(curX, baseY, "  Z: ", String.valueOf(zCoord), zLabelColor, zValColor);

        // Direction / Marker
        if (showDirection.isEnabled()) {
            String dirStr = "  " + dir;
            if (showMarkers.isEnabled()) dirStr += " " + marker;
            mc.fontRendererObj.drawStringWithShadow(dirStr, curX, baseY, dColor);
        }
    }

    /** Draws a coloured label and a white (or plain) value; returns new X cursor. */
    private int drawSegment(int cx, int cy, String label, String val, int labelColor, int valColor) {
        mc.fontRendererObj.drawStringWithShadow(label, cx, cy, labelColor);
        cx += mc.fontRendererObj.getStringWidth(label);
        mc.fontRendererObj.drawStringWithShadow(val, cx, cy, valColor);
        cx += mc.fontRendererObj.getStringWidth(val);
        return cx;
    }

    private int measureHorizontalWidth(int xCoord, int yCoord, int zCoord, String dir, String marker) {
        StringBuilder sb = new StringBuilder();
        sb.append("X: ").append(xCoord);
        if (!hideYCoordinate.isEnabled()) sb.append("  Y: ").append(yCoord);
        sb.append("  Z: ").append(zCoord);
        if (showDirection.isEnabled()) {
            sb.append("  ").append(dir);
            if (showMarkers.isEnabled()) sb.append(" ").append(marker);
        }
        return mc.fontRendererObj.getStringWidth(sb.toString());
    }
}
