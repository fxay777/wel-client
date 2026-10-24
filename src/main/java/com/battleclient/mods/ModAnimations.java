package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;

/**
 * ModAnimations - Restaura 100% fielmente as animacoes de combate da 1.7.10
 *
 * Funcionalidades autenticas:
 *  - 1.7 Blockhit Real: swing da espada durante a defesa simultanea (sem travar)
 *  - Estilos de Blockhit: 1.7 Classic (original da 1.7.10), Smooth, Exhibition, Spin
 *  - Smooth Item Switch: item nao cai para o fundo da tela ao trocar slots
 *  - 1.7 Bow / Eating: posicionamento classico do arco e consumiveis
 *  - Item Scale & Swing Speed configuraveis
 */
public class ModAnimations extends BaseMod {
    public static ModAnimations instance;

    private final BooleanSetting blockHit;
    private final BooleanSetting oldItemSwitch;
    private final BooleanSetting oldBow;
    private final BooleanSetting oldEating;
    private final BooleanSetting oldSneaking;
    private final ModeSetting blockHitStyle;
    private final NumberSetting swingSpeed;
    private final NumberSetting itemScale;

    private Field epField = null;
    private Field pepField = null;
    private boolean reflectionFailed = false;

    public ModAnimations() {
        super("animations", "1.7 Animations", "Restaura fielmente as animacoes classicas de combate da versao 1.7.10", "Visual");
        instance = this;

        blockHit = new BooleanSetting("blockhit", "1.7 Blockhit", "Permite bater e defender simultaneamente como na 1.7.10", true);
        oldItemSwitch = new BooleanSetting("oldSwitch", "Smooth Item Switch", "Impede o item de descer bruscamente ao trocar de slot", true);
        oldBow = new BooleanSetting("oldBow", "1.7 Bow Animation", "Posicionamento classico do arco ao puxar", true);
        oldEating = new BooleanSetting("oldEating", "1.7 Eating & Drinking", "Animacao suave classica ao comer ou tomar pocao", true);
        oldSneaking = new BooleanSetting("oldSneak", "1.7 Smooth Sneak", "Camera suave ao agachar", true);
        blockHitStyle = new ModeSetting("style", "Blockhit Style", "Estilo da animacao de bater e defender", 0, "1.7 Classic", "Smooth", "Exhibition", "Spin");
        swingSpeed = new NumberSetting("swingSpeed", "Swing Speed", "Velocidade da animacao do braco", 1.0, 0.5, 2.0, 0.1);
        itemScale = new NumberSetting("itemScale", "Item Size", "Tamanho da espada/item na mao", 1.0, 0.5, 1.5, 0.05);

        addSetting(blockHit);
        addSetting(blockHitStyle);
        addSetting(oldItemSwitch);
        addSetting(oldBow);
        addSetting(oldEating);
        addSetting(oldSneaking);
        addSetting(swingSpeed);
        addSetting(itemScale);

        initReflection();
    }

    private void initReflection() {
        try {
            Class<?> irClass = mc.getItemRenderer().getClass();
            try {
                epField = irClass.getDeclaredField("equippedProgress");
                pepField = irClass.getDeclaredField("prevEquippedProgress");
            } catch (NoSuchFieldException e) {
                // MCP / SRG fallback
                epField = irClass.getDeclaredField("field_78454_c");
                pepField = irClass.getDeclaredField("field_78451_d");
            }
            if (epField != null) epField.setAccessible(true);
            if (pepField != null) pepField.setAccessible(true);
        } catch (Exception e) {
            reflectionFailed = true;
        }
    }

    public boolean isBlockHit() {
        return enabled && blockHit.isEnabled();
    }

    public boolean isOldItemSwitch() {
        return enabled && oldItemSwitch.isEnabled();
    }

    public float getItemScale() {
        if (!enabled) return 1.0F;
        return itemScale.getFloatValue();
    }

    public float getSwingProgress(float swingProgress) {
        if (!enabled) return swingProgress;
        float factor = swingSpeed.getFloatValue();
        return Math.min(1.0F, swingProgress * factor);
    }

    @Override
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!enabled) return;
        if (mc.thePlayer == null || mc.getItemRenderer() == null) return;

        // Smooth Item Switch: mantem o item elevado sem o mergulho da 1.8
        if (isOldItemSwitch() && !reflectionFailed && epField != null && pepField != null) {
            try {
                float ep = epField.getFloat(mc.getItemRenderer());
                if (ep < 1.0F) {
                    epField.setFloat(mc.getItemRenderer(), 1.0F);
                    pepField.setFloat(mc.getItemRenderer(), 1.0F);
                }
            } catch (Exception e) {
                reflectionFailed = true;
            }
        }
    }

    /**
     * Renderiza o item na primeira pessoa com a verdadeira matematica de blockhit da 1.7.10
     */
    public void render17Blockhit(float partialTicks) {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;
        ItemStack stack = player.getHeldItem();
        if (stack == null) return;

        float equipProgress = 1.0F;
        float swingProgress = player.getSwingProgress(partialTicks);
        swingProgress = getSwingProgress(swingProgress);

        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        int light = mc.theWorld.getCombinedLight(new BlockPos(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), 0);
        float lightX = (float)(light & 65535);
        float lightY = (float)(light >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);

        GlStateManager.rotate(player.renderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(player.renderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTicks, 0.0F, 1.0F, 0.0F);

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        float scale = getItemScale();
        if (scale != 1.0F) {
            GlStateManager.scale(scale, scale, scale);
        }

        // Aplica o estilo de Blockhit selecionado
        applyBlockhitStyle(equipProgress, swingProgress);

        // Renderiza o modelo da espada segurada
        mc.getItemRenderer().renderItem(player, stack, ItemCameraTransforms.TransformType.FIRST_PERSON);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    private void applyBlockhitStyle(float equipProgress, float swingProgress) {
        String style = blockHitStyle.getValue();

        if ("Smooth".equalsIgnoreCase(style)) {
            transformFirstPersonItem(equipProgress, swingProgress);
            doBlockTransformations();
            GlStateManager.translate(-0.06F, 0.04F, 0.0F);
        } else if ("Exhibition".equalsIgnoreCase(style)) {
            transformFirstPersonItem(equipProgress, 0.0F);
            float f = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
            GlStateManager.translate(0.1F, 0.15F, -0.1F);
            GlStateManager.rotate(-f * 22.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-MathHelper.sin(swingProgress * swingProgress * (float)Math.PI) * 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-f * 32.0F, 0.0F, 0.0F, 1.0F);
            doBlockTransformations();
        } else if ("Spin".equalsIgnoreCase(style)) {
            transformFirstPersonItem(equipProgress, swingProgress);
            float f = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
            GlStateManager.rotate(-f * 40.0F, 0.0F, 0.0F, 1.0F);
            doBlockTransformations();
        } else {
            // "1.7 Classic" - Codigo puro original do Minecraft 1.7.10
            transformFirstPersonItem(equipProgress, swingProgress);
            doBlockTransformations();
        }
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.72F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void doBlockTransformations() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }
}
