package com.seuclient.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import com.seuclient.core.Config;

public class TelaConfiguracao extends GuiScreen {
    
    @Override
    public void initGui() {
        int x = width / 2 - 100;
        int y = height / 2 - 60;
        
        buttonList.add(new GuiButton(0, x, y, 200, 20, 
            "Teclas: " + (Config.mostrarTeclas ? "ON" : "OFF")));
        buttonList.add(new GuiButton(1, x, y + 25, 200, 20, 
            "FPS: " + (Config.mostrarFPS ? "ON" : "OFF")));
        buttonList.add(new GuiButton(2, x, y + 50, 200, 20, 
            "CPS: " + (Config.mostrarCPS ? "ON" : "OFF")));
        buttonList.add(new GuiButton(3, x, y + 75, 200, 20, 
            "Armadura: " + (Config.mostrarStatusArmadura ? "ON" : "OFF")));
        buttonList.add(new GuiButton(4, x, y + 100, 200, 20, 
            "Alcance: " + (Config.mostrarDisplayAlcance ? "ON" : "OFF")));
        buttonList.add(new GuiButton(5, x, y + 125, 200, 20, 
            "Coordenadas: " + (Config.mostrarCoordenadas ? "ON" : "OFF")));
        buttonList.add(new GuiButton(6, x, y + 150, 200, 20, 
            "Ping: " + (Config.mostrarPing ? "ON" : "OFF")));
        buttonList.add(new GuiButton(7, x, y + 175, 200, 20, 
            "Hora: " + (Config.mostrarHora ? "ON" : "OFF")));
        buttonList.add(new GuiButton(8, x, y + 200, 200, 20, 
            "Direção: " + (Config.mostrarDirecao ? "ON" : "OFF")));
        buttonList.add(new GuiButton(9, x, y + 225, 200, 20, 
            "Poções: " + (Config.mostrarPotions ? "ON" : "OFF")));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                Config.mostrarTeclas = !Config.mostrarTeclas;
                break;
            case 1:
                Config.mostrarFPS = !Config.mostrarFPS;
                break;
            case 2:
                Config.mostrarCPS = !Config.mostrarCPS;
                break;
            case 3:
                Config.mostrarStatusArmadura = !Config.mostrarStatusArmadura;
                break;
            case 4:
                Config.mostrarDisplayAlcance = !Config.mostrarDisplayAlcance;
                break;
            case 5:
                Config.mostrarCoordenadas = !Config.mostrarCoordenadas;
                break;
            case 6:
                Config.mostrarPing = !Config.mostrarPing;
                break;
            case 7:
                Config.mostrarHora = !Config.mostrarHora;
                break;
            case 8:
                Config.mostrarDirecao = !Config.mostrarDirecao;
                break;
            case 9:
                Config.mostrarPotions = !Config.mostrarPotions;
                break;
        }
        
        initGui();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Configurações do Seu Client", width / 2, 20, 0xFFFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}