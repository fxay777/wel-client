package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;

public class ModAnimations extends BaseMod {
    public static ModAnimations instance;

    private final BooleanSetting blockHit;
    private final BooleanSetting oldItemSwitch;
    private final BooleanSetting oldBowEating;
    private final BooleanSetting oldSneaking;

    public ModAnimations() {
        super("animations", "1.7.10 Animations", "Restaura animações clássicas da versão 1.7.10 (Blockhit, Arco, Comer)", "Visual");
        instance = this;

        blockHit = new BooleanSetting("blockhit", "1.7 Blockhit (Espada Levantada)", true);
        oldItemSwitch = new BooleanSetting("oldSwitch", "Troca Suave de Itens 1.7", true);
        oldBowEating = new BooleanSetting("oldBow", "Animação de Arco e Comer 1.7", true);
        oldSneaking = new SneakSetting();

        addSetting(blockHit);
        addSetting(oldItemSwitch);
        addSetting(oldBowEating);
    }

    private static class SneakSetting extends BooleanSetting {
        public SneakSetting() {
            super("oldSneak", "Agachamento 1.7 Suave", true);
        }
    }

    public boolean isBlockHit() {
        return enabled && blockHit.isEnabled();
    }

    public boolean isOldItemSwitch() {
        return enabled && oldItemSwitch.isEnabled();
    }

    public boolean isOldBowEating() {
        return enabled && oldBowEating.isEnabled();
    }
}
