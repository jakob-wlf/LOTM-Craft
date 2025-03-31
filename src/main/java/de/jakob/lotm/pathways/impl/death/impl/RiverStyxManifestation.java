package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class RiverStyxManifestation extends Ability {

    private final HashSet<LivingEntity> affectedEntities = new HashSet<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(0, 0, 40), 10f);

    public RiverStyxManifestation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20 * 30;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(400))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 25);

        Vector vector = VectorUtil.rotateAroundY(entity.getEyeLocation().getDirection(), 90);

        World world = targetLoc.getWorld();

        if(world == null)
            return;

        List<Location> riverLocations = getLocationsAboveGround(targetLoc, vector, 30);

        runTaskWithDuration(4, 12 * 30, () -> {
            riverLocations.forEach(l -> {
                ParticleSpawner.displayParticles(world, Particle.DUST, l, 10, 1.5, .4, 1.5, 0, dust, 200);

                getNearbyLivingEntities(entity, 6, l, world).forEach(e -> {
                    if(affectedEntities.contains(e))
                        return;

                    Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> affectedEntities.add(e), 20);
                });
            });


            affectedEntities.removeIf(e -> {
                Location entityLoc = e.getLocation();
                return riverLocations.stream().noneMatch(l -> l.distanceSquared(entityLoc) <= (4));
            });

            affectedEntities.forEach(e -> {
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 5, false, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 5, false, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 5, false, false, false));

                Beyonder targetBeyonder = LOTM.getInstance().getBeyonder(e.getUniqueId());
                if(targetBeyonder != null) {
                    targetBeyonder.addMultiplierModifier(.1, 4);
                    targetBeyonder.removeSpirituality(40);
                }

                ParticleSpawner.displayParticles(world, Particle.SOUL, e.getLocation().add(0, e.getHeight() / 2f, 0), 4, .3, e.getHeight() / 2f, .3, 0, 200);
            });


        }, null);
    }

    public List<Location> getLocationsAboveGround(Location start, Vector direction, int steps) {
        List<Location> locationsAboveGround = new ArrayList<>();
        World world = start.getWorld();

        if (world == null) return locationsAboveGround;

        direction.normalize();

        // Traverse in both directions
        for (int i = -steps; i <= steps; i++) {
            Location current = start.clone().add(direction.clone().multiply(i));

            // Find the highest solid block at this x,z location
            int highestY = world.getHighestBlockYAt(current);
            Location locationAbove = new Location(world, current.getBlockX(), highestY + 1, current.getBlockZ());

            locationsAboveGround.add(locationAbove);
        }

        return locationsAboveGround;
    }
}
