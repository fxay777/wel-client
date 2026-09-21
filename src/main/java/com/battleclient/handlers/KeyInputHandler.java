package com.battleclient.handlers;

import com.battleclient.core.BattleClient;
import com.battleclient.core.ModManager;
import com.battleclient.gui.HUDEditorScreen;
import com.battleclient.gui.ModMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyInputHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static KeyBinding keyModMenu;
    public static KeyBinding keyHUDEditor;

    public static void init() {
        keyModMenu = new KeyBinding("Abrir Menu de Mods", Keyboard.KEY_RSHIFT, "Battle Client");
        keyHUDEditor = new KeyBinding("Abrir HUD Editor", Keyboard.KEY_H, "Battle Client");

        ClientRegistry.registerKeyBinding(keyModMenu);
        ClientRegistry.registerKeyBinding(keyHUDEditor);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyModMenu != null && keyModMenu.isPressed()) {
            mc.displayGuiScreen(new ModMenuScreen());
            return;
        }

        if (keyHUDEditor != null && keyHUDEditor.isPressed()) {
            mc.displayGuiScreen(new HUDEditorScreen());
            return;
        }

        ModManager.getInstance().onKey(event);
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        ModManager.getInstance().onMouse(event);
    }

    public static void sendMessage(String msg) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(BattleClient.CHAT_PREFIX + msg));
        }
    }
}
