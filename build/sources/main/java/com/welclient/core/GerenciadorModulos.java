package com.seuclient.core;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class GerenciadorModulos {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static KeyBinding teclaConfig;
    
    public static void init() {
        teclaConfig = new KeyBinding("Configurações do Client", Keyboard.KEY_RSHIFT, "Seu Client");
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(teclaConfig);
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (teclaConfig.isPressed()) {
            mc.displayGuiScreen(new com.seuclient.gui.TelaConfiguracao());
        }
    }
}