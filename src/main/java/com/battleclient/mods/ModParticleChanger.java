package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ModeSetting;
import com.battleclient.core.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModParticleChanger extends BaseMod {
    public static ModParticleChanger instance;

    private final ModeSetting particleType;
    private final NumberSetting multiplier;
    private final BooleanSetting alwaysCrit;

    public ModParticleChanger() {
        super("particlechanger", "Particle Changer", "Multiplica particulas de ataques (Criticos, Fogo, Coracoes)", "Visual");
        instance = this;

        particleType = new ModeSetting("type", "Tipo de Particula", 0, "Critico", "Magia (Enchant)", "Coracoes", "Fogo");
        multiplier = new NumberSetting("multiplier", "Multiplicador de Particulas", 2.0, 1.0, 5.0, 1.0);
        alwaysCrit = new BooleanSetting("alwaysCrit", "Sempre Gerar Critico", true);

        addSetting(particleType);
        addSetting(multiplier);
        addSetting(alwaysCrit);
    }

    public void onAttack(Entity target) {
        if (!enabled || mc.theWorld == null || target == null) return;

        EnumParticleTypes type = EnumParticleTypes.CRIT;
        if (particleType.is("Magia (Enchant)")) type = EnumParticleTypes.CRIT_MAGIC;
        else if (particleType.is("Coracoes")) type = EnumParticleTypes.HEART;
        else if (particleType.is("Fogo")) type = EnumParticleTypes.FLAME;

        int count = multiplier.getIntValue() * 5;
        for (int i = 0; i < count; i++) {
            double rx = (Math.random() - 0.5) * target.width;
            double ry = Math.random() * target.height;
            double rz = (Math.random() - 0.5) * target.width;
            mc.theWorld.spawnParticle(type, target.posX + rx, target.posY + ry, target.posZ + rz, 0, 0.1, 0);
        }
    }
}
