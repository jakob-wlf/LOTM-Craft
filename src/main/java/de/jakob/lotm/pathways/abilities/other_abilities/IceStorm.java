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
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class IceStorm extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public IceStorm(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }


    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 14);
        World world = entity.getWorld();

        casting.add(beyonder);

        runTaskWithDuration(7, 20 * 5,  () -> {
            ParticleSpawner.displayParticles(world, Particle.SNOWFLAKE, loc.clone().add(0, 3.5, 0), 200, 2, 1.25, 2, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.CLOUD, loc.clone().add(0, 5.5, 0), 200, 2, 0, 2, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.ITEM_SNOWBALL, loc, 70, 2, 2, 2, 0, 200);

            damageNearbyEntities(3.4, beyonder.getCurrentMultiplier(), entity, 1.5, loc, world);
            addPotionEffectToNearbyEntities(entity, 1.5, loc, world, new PotionEffect(PotionEffectType.SLOWNESS, 20, 10));

        },() -> {
            casting.remove(beyonder);
        });
    }
}
