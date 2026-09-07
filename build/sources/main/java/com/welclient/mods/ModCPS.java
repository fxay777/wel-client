package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class ModCPS {
    private final Minecraft mc = Minecraft.getMinecraft();
    private List<Long> cliquesEsquerdo = new ArrayList<>();
    private List<Long> cliquesDireito = new ArrayList<>();
    
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            if (Mouse.getEventButton() == 0) { // Clique esquerdo
                cliquesEsquerdo.add(System.currentTimeMillis());
            } else if (Mouse.getEventButton() == 1) { // Clique direito
                cliquesDireito.add(System.currentTimeMillis());
            }
        }
    }
    
    public int getCPSEsquerdo() {
        limpar(cliquesEsquerdo);
        return cliquesEsquerdo.size();
    }
    
    public int getCPSDireito() {
        limpar(cliquesDireito);
        return cliquesDireito.size();
    }
    
    private void limpar(List<Long> cliques) {
        long tempoAtual = System.currentTimeMillis();
        cliques.removeIf(tempo -> tempoAtual - tempo > 1000);
    }
    
    public void render(int x, int y) {
        String textoCPS = "CPS: " + getCPSEsquerdo() + " | " + getCPSDireito();
        mc.fontRendererObj.drawStringWithShadow(textoCPS, x, y, 0xFFFFFFFF);
    }
}