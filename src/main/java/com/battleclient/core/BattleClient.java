package com.battleclient.core;

import com.battleclient.handlers.KeyInputHandler;
import com.battleclient.handlers.RenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BattleClient.MODID, name = BattleClient.NAME, version = BattleClient.VERSION)
public class BattleClient {
    public static final String MODID = "battleclient";
    public static final String NAME = "Battle Client";
    public static final String VERSION = "1.0.0";
    public static final String CHAT_PREFIX = "§8[§9Battle Client§8] §f";

    @Mod.Instance(MODID)
    public static BattleClient instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[Battle Client] Inicializando Pre-Init...");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[Battle Client] Registrando módulos e handlers...");

        // Registra mods no ModManager
        registerAllMods();

        // Inicializa configurações salvas
        Config.init();

        // Registra eventos no Forge
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        KeyInputHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("[Battle Client] Battle Client carregado com sucesso!");
    }

    public static void registerAllMods() {
        // Será preenchido com a lista de todos os 44 mods
        ModRegistry.init();
    }
}
