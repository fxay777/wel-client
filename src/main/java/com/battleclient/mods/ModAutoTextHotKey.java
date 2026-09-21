package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ModAutoTextHotKey extends BaseMod {
    public static ModAutoTextHotKey instance;

    private final BooleanSetting sendHub;
    private KeyBinding hubKey;

    public ModAutoTextHotKey() {
        super("autotext", "Auto Text / HotKey", "Envia comandos rápidos (/hub, /play) com apenas um clique", "Utility");
        instance = this;

        sendHub = new BooleanSetting("hub", "Ativar Atalho /hub (Tecla K)", true);
        addSetting(sendHub);

        hubKey = new KeyBinding("Comando Rápido /hub", Keyboard.KEY_K, "Battle Client");
        ClientRegistry.registerKeyBinding(hubKey);
    }

    @Override
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!enabled || mc.thePlayer == null) return;

        if (sendHub.isEnabled() && hubKey.isPressed()) {
            mc.thePlayer.sendChatMessage("/hub");
        }
    }
}
