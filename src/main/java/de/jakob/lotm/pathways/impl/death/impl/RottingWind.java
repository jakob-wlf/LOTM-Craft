package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class RottingWind extends Ability {

    public RottingWind(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
        hasCooldown = true;
        cooldownTicks = 20 * 4;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(120))
            return;

        LivingEntity entity = beyonder.getEntity();
        Vector direction = entity.getEyeLocation().getDirection();

        direction.setY(0);
        direction.normalize();

        Location loc = entity.getEyeLocation().add(direction).add(direction);

        Vector perpVector = VectorUtil.rotateAroundY(direction, 90);

        World world = entity.getWorld();

        world.playSound(loc, Sound.ENTITY_WITHER_BREAK_BLOCK, 2, random.nextFloat(.6f, 1f));

        List<Location> locs = new ArrayList<>();

        runTaskWithDuration(0, 20 * 4, () -> {
            locs.add(loc.clone());

            for(Location location : locs) {
                for(int i = 0; i < 1; i++)  {
                    if(random.nextBoolean()) {
                        Location particleLoc = location.clone().add(random.nextDouble(-5, 5), random.nextDouble(-8, 8), random.nextDouble(-5, 5)).add(perpVector.clone().multiply(random.nextDouble(-8, 8)));
                        ParticleSpawner.displayParticles(world, Particle.SOUL, particleLoc, 0, direction.getX(), direction.getY(), direction.getZ(), .25, 200);
                        particleLoc = location.clone().add(random.nextDouble(-5, 5), random.nextDouble(-8, 8), random.nextDouble(-1, 1)).add(perpVector.clone().multiply(random.nextDouble(-8, 8)));
                        ParticleSpawner.displayParticles(world, Particle.LARGE_SMOKE, particleLoc, 0, direction.getX(), direction.getY(), direction.getZ(), .25, 200);
                    }
                }
            }

            if(beyonder.isGriefingEnabled()) {
                BlockUtil.getBlocksInCircleRadius(loc.getBlock(), 8, true).forEach(b -> {
                    if(random.nextInt(20) == 0 && b.getType().isSolid())
                        b.setType(Material.SOUL_SAND);
                });
            }

            damageNearbyEntities(39.5, beyonder.getCurrentMultiplier(), entity, 6, loc, world, false, 0);
            addPotionEffectToNearbyEntities(entity, 6, loc, world, new PotionEffect(PotionEffectType.WITHER, 20 * 5, 5, false, false, false));

            loc.add(direction.clone().multiply(.5));
        }, null);
    }
}
