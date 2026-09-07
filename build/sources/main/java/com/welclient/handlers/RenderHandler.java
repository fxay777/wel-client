package com.seuclient.handlers;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.seuclient.mods.*;
import com.seuclient.core.Config;

public class RenderHandler {
    private final ModTeclas teclas = new ModTeclas();
    private final ModFPS fps = new ModFPS();
    private final ModCPS cps = new ModCPS();
    private final ModStatusArmadura statusArmadura = new ModStatusArmadura();
    private final ModDisplayAlcance displayAlcance = new ModDisplayAlcance();
    private final ModCoordenadas coordenadas = new ModCoordenadas();
    private final ModPing ping = new ModPing();
    private final ModHora hora = new ModHora();
    private final ModDirecao direcao = new ModDirecao();
    private final ModPotions potions = new ModPotions();
    
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        
        int larguraTela = event.resolution.getScaledWidth();
        int alturaTela = event.resolution.getScaledHeight();
        
        // Renderizar mods baseado nas configurações
        if (Config.mostrarTeclas) {
            teclas.render(Config.posXTeclas, alturaTela + Config.posYTeclas);
        }
        
        if (Config.mostrarFPS) {
            fps.render(Config.posXFPS, Config.posYFPS);
        }
        
        if (Config.mostrarCPS) {
            cps.render(Config.posXCPS, Config.posYCPS);
        }
        
        if (Config.mostrarStatusArmadura) {
            statusArmadura.render(larguraTela + Config.posXArmadura, alturaTela + Config.posYArmadura);
        }
        
        if (Config.mostrarDisplayAlcance) {
            displayAlcance.render(larguraTela + Config.posXAlcance, Config.posYAlcance);
        }
        
        if (Config.mostrarCoordenadas) {
            coordenadas.render(Config.posXCoords, Config.posYCoords);
        }
        
        if (Config.mostrarPing) {
            ping.render(Config.posXPing, Config.posYPing);
        }
        
        if (Config.mostrarHora) {
            hora.render(Config.posXHora, Config.posYHora);
        }
        
        if (Config.mostrarDirecao) {
            direcao.render(10, 60);
        }
        
        if (Config.mostrarPotions) {
            potions.render(larguraTela - 100, alturaTela - 150);
        }
    }
}