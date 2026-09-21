package com.battleclient.mods;

import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ModBlockOutline extends BaseMod {
    public static ModBlockOutline instance;

    private final ColorSetting outlineColor;
    private final NumberSetting thickness;

    public ModBlockOutline() {
        super("blockoutline", "Block Outline", "Personaliza a cor e espessura da linha de contorno dos blocos", "Visual");
        instance = this;

        outlineColor = new ColorSetting("color", "Cor do Contorno", 0xFF0055FF);
        thickness = new NumberSetting("thickness", "Espessura da Linha", 2.0, 1.0, 5.0, 0.5);

        addSetting(outlineColor);
        addSetting(thickness);
    }

    public int getOutlineColor() {
        return outlineColor.getValue();
    }

    public float getThickness() {
        return thickness.getFloatValue();
    }
}
