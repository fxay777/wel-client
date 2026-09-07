package com.seuclient.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import com.seuclient.core.Config;
import com.seuclient.gui.TelaConfiguracao;
import com.seuclient.gui.TelaCores;
import com.seuclient.gui.TelaPosicoes;

public class KeyInputHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    // KeyBindings
    private static KeyBinding teclaConfiguracao;
    private static KeyBinding teclaCores;
    private static KeyBinding teclaPosicoes;
    private static KeyBinding teclaToggleFPS;
    private static KeyBinding teclaToggleCPS;
    private static KeyBinding teclaToggleTeclas;
    private static KeyBinding teclaToggleArmadura;
    private static KeyBinding teclaToggleAlcance;
    private static KeyBinding teclaToggleCoordenadas;
    private static KeyBinding teclaTogglePing;
    private static KeyBinding teclaToggleHora;
    private static KeyBinding teclaToggleDirecao;
    private static KeyBinding teclaTogglePotions;
    private static KeyBinding teclaToggleTodos;
    
    // Configurações das teclas
    public static int teclaConfig = Keyboard.KEY_RSHIFT;
    public static int teclaCoresConfig = Keyboard.KEY_C;
    public static int teclaPosicoesConfig = Keyboard.KEY_P;
    public static int teclaFPS = Keyboard.KEY_F;
    public static int teclaCPS = Keyboard.KEY_K;
    public static int teclaTeclas = Keyboard.KEY_J;
    public static int teclaArmadura = Keyboard.KEY_H;
    public static int teclaAlcance = Keyboard.KEY_R;
    public static int teclaCoords = Keyboard.KEY_X;
    public static int teclaPing = Keyboard.KEY_V;
    public static int teclaHora = Keyboard.KEY_B;
    public static int teclaDirecao = Keyboard.KEY_N;
    public static int teclaPotions = Keyboard.KEY_M;
    public static int teclaTodos = Keyboard.KEY_L;
    
    public static void init() {
        // Registrar KeyBindings
        teclaConfiguracao = new KeyBinding("Abrir Configurações", teclaConfig, "Seu Client");
        teclaCores = new KeyBinding("Configurar Cores", teclaCoresConfig, "Seu Client");
        teclaPosicoes = new KeyBinding("Configurar Posições", teclaPosicoesConfig, "Seu Client");
        teclaToggleFPS = new KeyBinding("Toggle FPS", teclaFPS, "Seu Client");
        teclaToggleCPS = new KeyBinding("Toggle CPS", teclaCPS, "Seu Client");
        teclaToggleTeclas = new KeyBinding("Toggle Teclas", teclaTeclas, "Seu Client");
        teclaToggleArmadura = new KeyBinding("Toggle Armadura", teclaArmadura, "Seu Client");
        teclaToggleAlcance = new KeyBinding("Toggle Alcance", teclaAlcance, "Seu Client");
        teclaToggleCoordenadas = new KeyBinding("Toggle Coordenadas", teclaCoords, "Seu Client");
        teclaTogglePing = new KeyBinding("Toggle Ping", teclaPing, "Seu Client");
        teclaToggleHora = new KeyBinding("Toggle Hora", teclaHora, "Seu Client");
        teclaToggleDirecao = new KeyBinding("Toggle Direção", teclaDirecao, "Seu Client");
        teclaTogglePotions = new KeyBinding("Toggle Poções", teclaPotions, "Seu Client");
        teclaToggleTodos = new KeyBinding("Toggle Todos os Mods", teclaTodos, "Seu Client");
        
        // Registrar no Minecraft
        ClientRegistry.registerKeyBinding(teclaConfiguracao);
        ClientRegistry.registerKeyBinding(teclaCores);
        ClientRegistry.registerKeyBinding(teclaPosicoes);
        ClientRegistry.registerKeyBinding(teclaToggleFPS);
        ClientRegistry.registerKeyBinding(teclaToggleCPS);
        ClientRegistry.registerKeyBinding(teclaToggleTeclas);
        ClientRegistry.registerKeyBinding(teclaToggleArmadura);
        ClientRegistry.registerKeyBinding(teclaToggleAlcance);
        ClientRegistry.registerKeyBinding(teclaToggleCoordenadas);
        ClientRegistry.registerKeyBinding(teclaTogglePing);
        ClientRegistry.registerKeyBinding(teclaToggleHora);
        ClientRegistry.registerKeyBinding(teclaToggleDirecao);
        ClientRegistry.registerKeyBinding(teclaTogglePotions);
        ClientRegistry.registerKeyBinding(teclaToggleTodos);
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Verificar teclas de configuração
        if (teclaConfiguracao.isPressed()) {
            mc.displayGuiScreen(new TelaConfiguracao());
            return;
        }
        
        if (teclaCores.isPressed()) {
            mc.displayGuiScreen(new TelaCores());
            return;
        }
        
        if (teclaPosicoes.isPressed()) {
            mc.displayGuiScreen(new TelaPosicoes());
            return;
        }
        
        // Verificar toggles dos mods
        if (teclaToggleFPS.isPressed()) {
            Config.mostrarFPS = !Config.mostrarFPS;
            enviarMensagem("FPS " + (Config.mostrarFPS ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleCPS.isPressed()) {
            Config.mostrarCPS = !Config.mostrarCPS;
            enviarMensagem("CPS " + (Config.mostrarCPS ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleTeclas.isPressed()) {
            Config.mostrarTeclas = !Config.mostrarTeclas;
            enviarMensagem("Teclas " + (Config.mostrarTeclas ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleArmadura.isPressed()) {
            Config.mostrarStatusArmadura = !Config.mostrarStatusArmadura;
            enviarMensagem("Armadura " + (Config.mostrarStatusArmadura ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleAlcance.isPressed()) {
            Config.mostrarDisplayAlcance = !Config.mostrarDisplayAlcance;
            enviarMensagem("Alcance " + (Config.mostrarDisplayAlcance ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleCoordenadas.isPressed()) {
            Config.mostrarCoordenadas = !Config.mostrarCoordenadas;
            enviarMensagem("Coordenadas " + (Config.mostrarCoordenadas ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaTogglePing.isPressed()) {
            Config.mostrarPing = !Config.mostrarPing;
            enviarMensagem("Ping " + (Config.mostrarPing ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleHora.isPressed()) {
            Config.mostrarHora = !Config.mostrarHora;
            enviarMensagem("Hora " + (Config.mostrarHora ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleDirecao.isPressed()) {
            Config.mostrarDirecao = !Config.mostrarDirecao;
            enviarMensagem("Direção " + (Config.mostrarDirecao ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaTogglePotions.isPressed()) {
            Config.mostrarPotions = !Config.mostrarPotions;
            enviarMensagem("Poções " + (Config.mostrarPotions ? "§aativado" : "§cdesativado"));
        }
        
        if (teclaToggleTodos.isPressed()) {
            boolean todosAtivos = Config.mostrarFPS && Config.mostrarCPS && Config.mostrarTeclas && 
                                  Config.mostrarStatusArmadura && Config.mostrarDisplayAlcance && 
                                  Config.mostrarCoordenadas && Config.mostrarPing && Config.mostrarHora && 
                                  Config.mostrarDirecao && Config.mostrarPotions;
            
            boolean novoEstado = !todosAtivos;
            
            Config.mostrarFPS = novoEstado;
            Config.mostrarCPS = novoEstado;
            Config.mostrarTeclas = novoEstado;
            Config.mostrarStatusArmadura = novoEstado;
            Config.mostrarDisplayAlcance = novoEstado;
            Config.mostrarCoordenadas = novoEstado;
            Config.mostrarPing = novoEstado;
            Config.mostrarHora = novoEstado;
            Config.mostrarDirecao = novoEstado;
            Config.mostrarPotions = novoEstado;
            
            enviarMensagem(novoEstado ? "§aTodos os mods ativados" : "§cTodos os mods desativados");
        }
    }
    
    private void enviarMensagem(String mensagem) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§8[§bSeu Client§8] §f" + mensagem));
        }
    }
}