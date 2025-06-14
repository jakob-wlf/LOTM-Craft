package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class SunHalo extends ToggleableAbility {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1f);

    public SunHalo(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        tickDelay = 4;
    }

    @Override
    protected void impl(Beyonder beyonder) {
        if (!beyonder.removeSpirituality(4)) {
            casting.remove(beyonder);
            return;
        }

        List<LivingEntity> affected = new ArrayList<>(beyonder.getEntity().getNearbyEntities(15, 8, 15).stream().filter(e -> EntityUtil.areOnTheSameTeam(e, beyonder.getEntity())).filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e).toList());
        affected.add(beyonder.getEntity());

        LivingEntity entity = beyonder.getEntity();
        ParticleUtil.drawCircle(entity.getEyeLocation().clone().add(0, .35, 0), new Vector(0, 1, 0), .55, Particle.DUST, dust, 20);

        for (LivingEntity e : affected) {
            e.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 10, 2, false, false, false));
            e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 3, false, false, false));
            e.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 3, false, false, false));

            Beyonder b = LOTM.getInstance().getBeyonder(e.getUniqueId());
            if (b != null) {
                b.addMultiplierModifier(1.15, 1);
            }
        }

    }
}
