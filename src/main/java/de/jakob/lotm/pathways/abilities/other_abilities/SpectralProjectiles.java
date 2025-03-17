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
public class SpectralProjectiles extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public SpectralProjectiles(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 25);
        World world = entity.getWorld();

        for(int i = 0; i < 8; i++) {
            Location startLoc = loc.clone().add(random.nextDouble(-6, 6), random.nextDouble(6), random.nextDouble(-6, 6));
            runTaskWithDuration(1, 5, () -> ParticleSpawner.displayParticles(world, Particle.EFFECT, startLoc, 20, .1, .1, .1, 0, 200), null);

            Bukkit.getScheduler().runTaskLater(plugin,() -> launchParticleProjectile(startLoc, loc.clone().toVector().subtract(startLoc.toVector()).normalize(), Particle.EFFECT, null, 25, 3.6, 1, 11, entity, 11, .3), 5);
            Bukkit.getScheduler().runTaskLater(plugin, () -> casting.remove(beyonder), 30);
        }
    }


}
