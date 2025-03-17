package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

@NoArgsConstructor
public class RagingBlows extends Ability {
    public RagingBlows(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(12))
            return;

        World world = beyonder.getEntity().getWorld();
        LivingEntity entity = beyonder.getEntity();

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter >= 8) {
                    cancel();
                    return;
                }

                Location startLoc = VectorUtil.getRelativeLocation(entity.getEyeLocation(), random.nextDouble(1, 2), random.nextDouble(-1.5, 1.5), random.nextDouble(-.5, .5));

                world.spawnParticle(Particle.POOF, startLoc, 3, 0, 0, 0, .25);
                world.spawnParticle(Particle.CLOUD, startLoc, 10, 0, 0, 0, .25);
                world.spawnParticle(Particle.CRIT, startLoc, 15, 0, 0, 0, .25);
                world.playSound(startLoc, Sound.ENTITY_GENERIC_EXPLODE, .25f, 1f);

                damageNearbyEntities(5, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2, startLoc, world, false, 0, 0);

                counter++;
            }
        }.runTaskTimer(plugin, 0, 6);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 2.5;
    }
}
