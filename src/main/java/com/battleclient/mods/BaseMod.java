package com.battleclient.mods;

import com.battleclient.core.settings.ModSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMod {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final String category; // "HUD", "Visual", "Gameplay", "Utility"

    protected boolean enabled;
    protected int x = 10;
    protected int y = 10;
    protected int width = 60;
    protected int height = 16;

    protected final List<ModSetting<?>> settings = new ArrayList<>();

    public BaseMod(String id, String displayName, String description, String category) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.enabled = true;
    }

    public BaseMod(String id, String displayName, String description, String category, boolean defaultEnabled) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.enabled = defaultEnabled;
    }

    public void addSetting(ModSetting<?> setting) {
        this.settings.add(setting);
    }

    public List<ModSetting<?>> getSettings() {
        return settings;
    }

    public ModSetting<?> getSetting(String id) {
        for (ModSetting<?> s : settings) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public void toggle() {
        setEnabled(!this.enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHUD() {
        return "HUD".equalsIgnoreCase(category);
    }

    // Lifecycle methods
    public void onEnable() {}
    public void onDisable() {}
    public void onRender(RenderGameOverlayEvent.Post event) {}
    public void onWorldRender(RenderWorldLastEvent event) {}
    public void onTick(TickEvent.ClientTickEvent event) {}
    public void onKey(InputEvent.KeyInputEvent event) {}
    public void onMouse(InputEvent.MouseInputEvent event) {}

    // GUI/Editor Helper methods
    public void drawHUDBox(int x, int y, int w, int h) {
        // Fundo preto semi-transparente elegante (#000000 com alpha 180)
        Gui.drawRect(x, y, x + w, y + h, 0xB0000000);
        // Borda suave
        Gui.drawRect(x, y, x + w, y + 1, 0x30FFFFFF);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0x20000000);
    }

    public void drawHUDBoxTheme(int x, int y, int w, int h) {
        // Fundo preto com borda azul forte (#0055FF)
        Gui.drawRect(x, y, x + w, y + h, 0xC0000000);
        Gui.drawRect(x, y, x + w, y + 1, 0xFF0055FF);
        Gui.drawRect(x, y, x + 1, y + h, 0xFF0055FF);
        Gui.drawRect(x + w - 1, y, x + w, y + h, 0xFF0055FF);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0xFF0055FF);
    }

    public void drawDummy(int x, int y) {
        drawHUDBoxTheme(x, y, width, height);
        mc.fontRendererObj.drawStringWithShadow(displayName, x + 4, y + (height - 8) / 2, 0xFF00AAFF);
    }
}
