package com.battleclient.core;

import com.battleclient.mods.BaseMod;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class ModManager {
    private static ModManager instance;
    private final Map<String, BaseMod> mods = new LinkedHashMap<>();

    public static ModManager getInstance() {
        if (instance == null) {
            instance = new ModManager();
        }
        return instance;
    }

    public void registerMod(BaseMod mod) {
        mods.put(mod.getId().toLowerCase(), mod);
    }

    public Collection<BaseMod> getMods() {
        return mods.values();
    }

    public BaseMod getMod(String id) {
        return mods.get(id.toLowerCase());
    }

    public <T extends BaseMod> T getModByClass(Class<T> clazz) {
        for (BaseMod mod : mods.values()) {
            if (clazz.isInstance(mod)) {
                return clazz.cast(mod);
            }
        }
        return null;
    }

    public List<BaseMod> getModsByCategory(String category) {
        List<BaseMod> list = new ArrayList<>();
        for (BaseMod mod : mods.values()) {
            if (category.equalsIgnoreCase("ALL") || mod.getCategory().equalsIgnoreCase(category)) {
                list.add(mod);
            }
        }
        return list;
    }

    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        for (BaseMod mod : mods.values()) {
            if (mod.isEnabled() && mod.isHUD()) {
                try {
                    mod.onRender(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRenderWorld(RenderWorldLastEvent event) {
        for (BaseMod mod : mods.values()) {
            if (mod.isEnabled()) {
                try {
                    mod.onWorldRender(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onTick(TickEvent.ClientTickEvent event) {
        for (BaseMod mod : mods.values()) {
            if (mod.isEnabled()) {
                try {
                    mod.onTick(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onKey(InputEvent.KeyInputEvent event) {
        for (BaseMod mod : mods.values()) {
            if (mod.isEnabled()) {
                try {
                    mod.onKey(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onMouse(InputEvent.MouseInputEvent event) {
        for (BaseMod mod : mods.values()) {
            if (mod.isEnabled()) {
                try {
                    mod.onMouse(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
