package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderHandEvent;

public class ModAnimations extends BaseMod {
    public static ModAnimations instance;

    private final BooleanSetting blockHit;
    private final BooleanSetting oldItemSwitch;
    private final BooleanSetting oldBow;
    private final BooleanSetting oldEating;
    private final BooleanSetting oldSneaking;
    private final ModeSetting blockHitStyle;
    private final NumberSetting swingSpeed;

    public ModAnimations() {
        super("animations", "1.7.10 Animations", "Restaura fielmente as animações clássicas de combate e itens da versão 1.7.10", "Visual");
        instance = this;

        blockHit = new BooleanSetting("blockhit", "1.7 Blockhit", "Animação de bater e defender com a espada levantada", true);
        oldItemSwitch = new BooleanSetting("oldSwitch", "Smooth Item Switch", "Impede o item de descer bruscamente na troca de slots", true);
        oldBow = new BooleanSetting("oldBow", "1.7 Bow Animation", "Posicionamento clássico do arco puxado", true);
        oldEating = new BooleanSetting("oldEating", "1.7 Eating & Drinking", "Animação clássica ao comer ou tomar poção", true);
        oldSneaking = new BooleanSetting("oldSneak", "1.7 Smooth Sneak", "Transição suave de câmera ao agachar", true);
        blockHitStyle = new ModeSetting("style", "Blockhit Style", "Estilo do blockhit", 0, "1.7 Clássico", "Smooth 1.7", "Exhibition");
        swingSpeed = new NumberSetting("swingSpeed", "Swing Animation Speed", "Velocidade da animação do braço", 1.0, 0.5, 2.0, 0.1);

        addSetting(blockHit);
        addSetting(oldItemSwitch);
        addSetting(oldBow);
        addSetting(oldEating);
        addSetting(oldSneaking);
        addSetting(blockHitStyle);
        addSetting(swingSpeed);
    }

    public boolean isBlockHit() {
        return enabled && blockHit.isEnabled();
    }

    public boolean isOldItemSwitch() {
        return enabled && oldItemSwitch.isEnabled();
    }

    public boolean isOldBow() {
        return enabled && oldBow.isEnabled();
    }

    public boolean isOldEating() {
        return enabled && oldEating.isEnabled();
    }

    public boolean isOldSneaking() {
        return enabled && oldSneaking.isEnabled();
    }

    public float getSwingProgress(float swingProgress) {
        if (!enabled) return swingProgress;
        float factor = swingSpeed.getFloatValue();
        return Math.min(1.0F, swingProgress * factor);
    }

    /**
     * Aplica transformações clássicas de 1.7.10 no item segurado durante o swing / block
     */
    public void transformItemFirstPerson(float equipProgress, float swingProgress) {
        if (!enabled) return;

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ItemStack stack = player.getHeldItem();
        boolean isBlocking = player.isUsingItem() && stack != null && stack.getItemUseAction() == EnumAction.BLOCK;

        if (isBlocking && isBlockHit()) {
            float swing = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            float swingSqrt = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

            if (blockHitStyle.is("1.7 Clássico")) {
                GlStateManager.translate(-0.15F, 0.15F, -0.05F);
                GlStateManager.rotate(-swingSqrt * 20.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-swing * 20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-swingSqrt * 30.0F, 0.0F, 0.0F, 1.0F);
            } else if (blockHitStyle.is("Smooth 1.7")) {
                GlStateManager.translate(-0.10F, 0.10F, -0.05F);
                GlStateManager.rotate(-swingSqrt * 18.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-swingSqrt * 25.0F, 0.0F, 0.0F, 1.0F);
            } else if (blockHitStyle.is("Exhibition")) {
                GlStateManager.translate(-0.2F, 0.18F, 0.0F);
                GlStateManager.rotate(-swingSqrt * 30.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-swing * 25.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-swingSqrt * 40.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }
}
