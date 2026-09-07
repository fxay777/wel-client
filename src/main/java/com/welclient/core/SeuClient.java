package com.seuclient.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import com.seuclient.handlers.KeyInputHandler;
import com.seuclient.handlers.RenderHandler;

@Mod(modid = "seuclient", name = "Seu Client", version = "1.0.0")
public class SeuClient {
    
    public static final String MODID = "seuclient";
    public static final String NOME = "Seu Client";
    public static final String VERSAO = "1.0.0";
    
    @Mod.Instance
    public static SeuClient instance;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        KeyInputHandler.init();
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("[Seu Client] Mod carregado com sucesso!");
    }
}