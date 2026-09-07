package com.seuclient.core;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class Config {
    // Mods visíveis
    public static boolean mostrarTeclas = true;
    public static boolean mostrarFPS = true;
    public static boolean mostrarCPS = true;
    public static boolean mostrarStatusArmadura = true;
    public static boolean mostrarDisplayAlcance = true;
    public static boolean mostrarCoordenadas = true;
    public static boolean mostrarPing = true;
    public static boolean mostrarHora = false;
    public static boolean mostrarDirecao = true;
    public static boolean mostrarPotions = true;
    
    // Posições (podem ser personalizadas)
    public static int posXTeclas = 10;
    public static int posYTeclas = -80;
    public static int posXFPS = 10;
    public static int posYFPS = 10;
    public static int posXCPS = 10;
    public static int posYCPS = 20;
    public static int posXArmadura = -80;
    public static int posYArmadura = -90;
    public static int posXAlcance = -80;
    public static int posYAlcance = 10;
    public static int posXCoords = 10;
    public static int posYCoords = 30;
    public static int posXPing = 10;
    public static int posYPing = 40;
    public static int posXHora = 10;
    public static int posYHora = 50;
    
    // Cores
    public static int corTexto = 0xFFFFFFFF;
    public static int corFundo = 0x80000000;
    public static int corDestaque = 0xFF00FF00;
    
    public static void init(File arquivoConfig) {
        Configuration config = new Configuration(arquivoConfig);
        
        config.load();
        
        // Categoria Mods
        mostrarTeclas = config.getBoolean("mostrarTeclas", "mods", true, "Mostrar Mod de Teclas");
        mostrarFPS = config.getBoolean("mostrarFPS", "mods", true, "Mostrar Mod de FPS");
        mostrarCPS = config.getBoolean("mostrarCPS", "mods", true, "Mostrar Mod de CPS");
        mostrarStatusArmadura = config.getBoolean("mostrarStatusArmadura", "mods", true, "Mostrar Mod de Status da Armadura");
        mostrarDisplayAlcance = config.getBoolean("mostrarDisplayAlcance", "mods", true, "Mostrar Mod de Display de Alcance");
        mostrarCoordenadas = config.getBoolean("mostrarCoordenadas", "mods", true, "Mostrar Mod de Coordenadas");
        mostrarPing = config.getBoolean("mostrarPing", "mods", true, "Mostrar Mod de Ping");
        mostrarHora = config.getBoolean("mostrarHora", "mods", false, "Mostrar Mod de Hora");
        mostrarDirecao = config.getBoolean("mostrarDirecao", "mods", true, "Mostrar Mod de Direção");
        mostrarPotions = config.getBoolean("mostrarPotions", "mods", true, "Mostrar Mod de Poções");
        
        config.save();
    }
}