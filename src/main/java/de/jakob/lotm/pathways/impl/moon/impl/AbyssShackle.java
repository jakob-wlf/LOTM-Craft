package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
public class AbyssShackle extends Ability {

    private final Set<UUID> restrainedEntities = new HashSet<>();
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(20, 20, 20), 1);

    public AbyssShackle(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        LivingEntity target = getTargetEntity(entity, 5);

        if(target == null) {
            return;
        }

        if(restrainedEntities.contains(target.getUniqueId())) {
            return;
        }

        if(!beyonder.removeSpirituality(20)) {
            return;
        }

        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, .1f);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, .8f);

        restrainedEntities.add(target.getUniqueId());

        runTaskWithDuration(3, 20 * 5, () -> {
            if(!target.isValid()) {
                return;
            }

            target.setVelocity(new Vector(0, 0, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 10));

            Location loc1 = target.getLocation().add(1.5, 0, 0);
            Location loc2 = target.getLocation().add(-1.5, 0, 0);
            Location loc3 = target.getLocation().add(0, 0, 1.5);
            Location loc4 = target.getLocation().add(0, 0, -1.5);

            ParticleUtil.drawLine(target.getEyeLocation().subtract(0, .3, 0), loc1, Particle.DUST, .3, target.getWorld(), dustOptions, 5, 0);
            ParticleUtil.drawLine(target.getEyeLocation().subtract(0, .3, 0), loc2, Particle.DUST, .3, target.getWorld(), dustOptions, 5, 0);
            ParticleUtil.drawLine(target.getEyeLocation().subtract(0, .3, 0), loc3, Particle.DUST, .3, target.getWorld(), dustOptions, 5, 0);
            ParticleUtil.drawLine(target.getEyeLocation().subtract(0, .3, 0), loc4, Particle.DUST, .3, target.getWorld(), dustOptions, 5, 0);


        }, () -> {
            restrainedEntities.remove(target.getUniqueId());
        });
    }

}
