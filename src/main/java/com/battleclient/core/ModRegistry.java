package com.battleclient.core;

import com.battleclient.mods.*;

public class ModRegistry {

    public static void init() {
        ModManager mm = ModManager.getInstance();

        // --- HUD MODS ---
        mm.registerMod(new ModFPS());
        mm.registerMod(new ModCPS());
        mm.registerMod(new ModPing());
        mm.registerMod(new ModCoordinates());
        mm.registerMod(new ModKeystrokes());
        mm.registerMod(new ModArmorStatus());
        mm.registerMod(new ModPotionEffects());
        mm.registerMod(new ModReachDisplay());
        mm.registerMod(new ModDirectionHUD());
        mm.registerMod(new ModServerAddress());
        mm.registerMod(new ModPackDisplay());
        mm.registerMod(new ModScoreboard());
        mm.registerMod(new ModTitles());
        mm.registerMod(new ModBossBar());
        mm.registerMod(new ModChat());
        mm.registerMod(new ModTabEditor());
        mm.registerMod(new ModCrosshair());

        // --- VISUAL & EFFECTS MODS ---
        mm.registerMod(new ModAnimations());
        mm.registerMod(new ModMotionBlur());
        mm.registerMod(new ModHitColor());
        mm.registerMod(new ModMenuBlur());
        mm.registerMod(new ModItemPhysics());
        mm.registerMod(new Mod3DSkins());
        mm.registerMod(new ModGlintColorizer());
        mm.registerMod(new ModBlockOutline());
        mm.registerMod(new Mod2DItems());
        mm.registerMod(new ModHitBox());
        mm.registerMod(new ModLighting());
        mm.registerMod(new ModShinyPots());
        mm.registerMod(new ModHurtCam());
        mm.registerMod(new ModDamageTint());
        mm.registerMod(new ModParticleChanger());
        mm.registerMod(new ModWeatherChanger());
        mm.registerMod(new ModTimeChanger());
        mm.registerMod(new ModNametags());

        // --- GAMEPLAY & UTILITY MODS ---
        mm.registerMod(new ModToggleSprint());
        mm.registerMod(new ModZoom());
        mm.registerMod(new ModFreelook());
        mm.registerMod(new ModFOVChanger());
        mm.registerMod(new ModScrollableTooltips());
        mm.registerMod(new ModNickHider());
        mm.registerMod(new ModWaypoints());
        mm.registerMod(new ModTNTCountdown());
        mm.registerMod(new ModScreenshotUploader());
        mm.registerMod(new ModAutoTextHotKey());
        mm.registerMod(new ModPackOrganizer());
    }
}
