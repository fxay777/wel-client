package com.battleclient.core;

import com.battleclient.core.settings.ColorSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.ModSetting;
import com.battleclient.core.settings.NumberSetting;
import com.battleclient.mods.BaseMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;

    public static void init() {
        File dir = new File(Minecraft.getMinecraft().mcDataDir, "battleclient");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        configFile = new File(dir, "config.json");
        carregar();
    }

    public static void salvar() {
        if (configFile == null) return;

        try {
            JsonObject root = new JsonObject();
            JsonObject modsObj = new JsonObject();

            for (BaseMod mod : ModManager.getInstance().getMods()) {
                JsonObject modJson = new JsonObject();
                modJson.addProperty("enabled", mod.isEnabled());
                modJson.addProperty("x", mod.getX());
                modJson.addProperty("y", mod.getY());

                JsonObject settingsJson = new JsonObject();
                for (ModSetting<?> setting : mod.getSettings()) {
                    if (setting instanceof NumberSetting) {
                        settingsJson.addProperty(setting.getId(), ((NumberSetting) setting).getValue());
                    } else if (setting instanceof ColorSetting) {
                        settingsJson.addProperty(setting.getId(), ((ColorSetting) setting).getValue());
                    } else if (setting instanceof ModeSetting) {
                        settingsJson.addProperty(setting.getId(), ((ModeSetting) setting).getValue());
                    } else {
                        settingsJson.addProperty(setting.getId(), String.valueOf(setting.getValue()));
                    }
                }
                modJson.add("settings", settingsJson);
                modsObj.add(mod.getId(), modJson);
            }

            root.add("mods", modsObj);

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void carregar() {
        if (configFile == null || !configFile.exists()) return;

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            if (root.has("mods")) {
                JsonObject modsObj = root.getAsJsonObject("mods");
                for (BaseMod mod : ModManager.getInstance().getMods()) {
                    if (modsObj.has(mod.getId())) {
                        JsonObject modJson = modsObj.getAsJsonObject(mod.getId());
                        if (modJson.has("enabled")) {
                            mod.setEnabled(modJson.get("enabled").getAsBoolean());
                        }
                        if (modJson.has("x")) {
                            mod.setX(modJson.get("x").getAsInt());
                        }
                        if (modJson.has("y")) {
                            mod.setY(modJson.get("y").getAsInt());
                        }

                        if (modJson.has("settings")) {
                            JsonObject settingsJson = modJson.getAsJsonObject("settings");
                            for (ModSetting<?> setting : mod.getSettings()) {
                                if (settingsJson.has(setting.getId())) {
                                    if (setting instanceof NumberSetting) {
                                        ((NumberSetting) setting).setValue(settingsJson.get(setting.getId()).getAsDouble());
                                    } else if (setting instanceof ColorSetting) {
                                        ((ColorSetting) setting).setValue(settingsJson.get(setting.getId()).getAsInt());
                                    } else if (setting instanceof ModeSetting) {
                                        ((ModeSetting) setting).setValue(settingsJson.get(setting.getId()).getAsString());
                                    } else if (setting.getValue() instanceof Boolean) {
                                        ((ModSetting<Boolean>) setting).setValue(settingsJson.get(setting.getId()).getAsBoolean());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
