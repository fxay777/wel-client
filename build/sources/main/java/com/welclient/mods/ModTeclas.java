package com.seuclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ModTeclas extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final int LARGURA = 80;
    private static final int ALTURA = 60;
    
    public void render(int x, int y) {
        // Fundo
        Gui.drawRect(x, y, x + LARGURA, y + ALTURA, 0x80000000);
        
        // Tecla W
        desenharTecla(x + 30, y, Keyboard.getKeyName(mc.gameSettings.keyBindForward.getKeyCode()), 
                teclaPressionada(mc.gameSettings.keyBindForward));
        
        // Tecla A
        desenharTecla(x, y + 20, Keyboard.getKeyName(mc.gameSettings.keyBindLeft.getKeyCode()), 
                teclaPressionada(mc.gameSettings.keyBindLeft));
        
        // Tecla S
        desenharTecla(x + 30, y + 20, Keyboard.getKeyName(mc.gameSettings.keyBindBack.getKeyCode()), 
                teclaPressionada(mc.gameSettings.keyBindBack));
        
        // Tecla D
        desenharTecla(x + 60, y + 20, Keyboard.getKeyName(mc.gameSettings.keyBindRight.getKeyCode()), 
                teclaPressionada(mc.gameSettings.keyBindRight));
        
        // Tecla de Pular
        desenharTecla(x + 30, y + 40, "Pular", teclaPressionada(mc.gameSettings.keyBindJump));
        
        // Tecla de Correr
        desenharTecla(x, y + 40, "Correr", teclaPressionada(mc.gameSettings.keyBindSprint));
    }
    
    private void desenharTecla(int x, int y, String tecla, boolean pressionada) {
        int cor = pressionada ? 0xFFFFFFFF : 0xFF888888;
        Gui.drawRect(x, y, x + 18, y + 18, cor);
        Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0xFF000000);
        
        mc.fontRendererObj.drawString(tecla, x + 5, y + 5, pressionada ? 0xFFFF5555 : 0xFFFFFFFF);
    }
    
    private boolean teclaPressionada(KeyBinding keyBinding) {
        return Keyboard.isKeyDown(keyBinding.getKeyCode());
    }
}