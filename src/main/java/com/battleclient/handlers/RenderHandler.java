package com.battleclient.handlers;

import com.battleclient.core.ModManager;
import com.battleclient.mods.*;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderHandler {

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        ModManager.getInstance().onRenderOverlay(event);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        ModManager.getInstance().onRenderWorld(event);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ModManager.getInstance().onTick(event);
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (ModFreelook.instance != null) {
            ModFreelook.instance.onCameraSetup(event);
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.target != null) {
            if (ModReachDisplay.instance != null) {
                ModReachDisplay.instance.onPlayerAttack(event.target);
            }
            if (ModParticleChanger.instance != null) {
                ModParticleChanger.instance.onAttack(event.target);
            }
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event) {
        if (ModFOVChanger.instance != null && ModFOVChanger.instance.isEnabled()) {
            if (ModFOVChanger.instance.isDisableSpeedModifier()) {
                event.newfov = 1.0F;
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (ModAnimations.instance != null && ModAnimations.instance.isEnabled()) {
            // Aplica transformações adicionais quando em blockhit
            ModAnimations.instance.transformItemFirstPerson(0.0F, event.partialTicks);
        }
    }
}
