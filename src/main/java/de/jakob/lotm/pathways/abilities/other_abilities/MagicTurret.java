package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class MagicTurret extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public MagicTurret(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        Location loc = entity.getLocation().clone().add(random.nextDouble(-4, 4), random.nextDouble(4), random.nextDouble(-4, 4));
        LivingEntity target = getTargetEntity(entity, 20, EntityType.ALLAY);

        runTaskWithDuration(2, 20 * 8, () -> {
            if(target.getWorld() != loc.getWorld())
                return;
            ParticleSpawner.displayParticles(world, Particle.WITCH, loc, 25, .05, .05, .05, 0, 200);

            Vector direction = target.getEyeLocation().clone().subtract(0, .2, 0).toVector().subtract(loc.toVector());
            if(random.nextInt(14) == 0) {
                launchParticleProjectile(loc, direction, Particle.SMOKE, null, 23, 11, 1, 9, entity, 22, .3);
            }
        }, null);
    }
}
