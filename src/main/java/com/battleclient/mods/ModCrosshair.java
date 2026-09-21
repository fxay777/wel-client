package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModCrosshair extends BaseMod {
    public static ModCrosshair instance;

    private final ModeSetting style;
    private final ColorSetting crosshairColor;
    private final BooleanSetting highlightTarget;
    private final NumberSetting size;
    private final NumberSetting gap;
    private final NumberSetting thickness;

    public ModCrosshair() {
        super("crosshair", "Custom Crosshair", "Mira totalmente personalizável (Cruz, Ponto, Tamanho, Cor, Target Highlight)", "HUD");
        instance = this;

        style = new ModeSetting("style", "Tipo de Mira", 0, "Cruz", "Ponto", "Cruz com Ponto");
        crosshairColor = new ColorSetting("color", "Cor da Mira", 0xFF0055FF);
        highlightTarget = new BooleanSetting("highlight", "Mudar Cor ao Mirar em Jogador", true);
        size = new NumberSetting("size", "Tamanho", 4.0, 1.0, 15.0, 1.0);
        gap = new NumberSetting("gap", "Abertura (Gap)", 2.0, 0.0, 10.0, 1.0);
        thickness = new NumberSetting("thickness", "Espessura", 1.0, 1.0, 4.0, 1.0);

        addSetting(style);
        addSetting(crosshairColor);
        addSetting(highlightTarget);
        addSetting(size);
        addSetting(gap);
        addSetting(thickness);
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        ScaledResolution sr = event.resolution;
        int cx = sr.getScaledWidth() / 2;
        int cy = sr.getScaledHeight() / 2;

        int color = crosshairColor.getValue();
        if (highlightTarget.isEnabled() && mc.pointedEntity instanceof EntityLivingBase) {
            color = 0xFFFF0000; // Vermelho agressivo ao focar alvo
        }

        int s = size.getIntValue();
        int g = gap.getIntValue();
        int t = thickness.getIntValue();
        int halfT = t / 2;

        if (style.is("Ponto") || style.is("Cruz com Ponto")) {
            Gui.drawRect(cx - 1, cy - 1, cx + 2, cy + 2, color);
        }

        if (style.is("Cruz") || style.is("Cruz com Ponto")) {
            // Superior
            Gui.drawRect(cx - halfT, cy - g - s, cx - halfT + t, cy - g, color);
            // Inferior
            Gui.drawRect(cx - halfT, cy + g + 1, cx - halfT + t, cy + g + 1 + s, color);
            // Esquerda
            Gui.drawRect(cx - g - s, cy - halfT, cx - g, cy - halfT + t, color);
            // Direita
            Gui.drawRect(cx + g + 1, cy - halfT, cx + g + 1 + s, cy - halfT + t, color);
        }
    }
}
