package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class EtherealFlame extends Ability {

    private final Set<Beyonder> cooldown = new HashSet<>();

    public EtherealFlame(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    //TODO: Add sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(cooldown.contains(beyonder))
            return;

        cooldown.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().setY(0).normalize().multiply(2));
        Location targetLoc = getTargetLocation(entity, 25);
        World world = entity.getWorld();

        for(int i = 0; i < 8; i++) {
            Location startLoc = loc.clone().add(random.nextDouble(-6, 6), random.nextDouble(6), random.nextDouble(-6, 6));
            runTaskWithDuration(1, 5, () -> {
                ParticleSpawner.displayParticles(world, Particle.SOUL, startLoc, 8, .1, .1, .1, 0, 200);
                ParticleSpawner.displayParticles(world, Particle.SOUL_FIRE_FLAME, startLoc, 8, .1, .1, .1, 0, 200);
            }, null);

            Bukkit.getScheduler().runTaskLater(plugin,() -> launchParticleProjectile(startLoc, targetLoc.clone().toVector().subtract(startLoc.toVector()).normalize(), Particle.SOUL, null, 40, 16, 1, 9, entity, 3, 0, .3, .015, Particle.SOUL_FIRE_FLAME), 5);
            Bukkit.getScheduler().runTaskLater(plugin, () -> cooldown.remove(beyonder), 30);
        }
    }
}
