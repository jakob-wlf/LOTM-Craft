package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.LocationProvider;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

@NoArgsConstructor
public class WaterMastery extends SelectableAbility {

    final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(30, 120, 255), 2.75f);
    final Particle.DustOptions whiteDust = new Particle.DustOptions(Color.fromRGB(220, 220, 255), 2.75f);

    private final Set<Beyonder> castingVortex = new HashSet<>();
    private final Set<Beyonder> castingGeysers = new HashSet<>();

    public WaterMastery(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {"Water Front", "Water Vortex", "Geysers"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 200,
                1, 210,
                2, 260
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> createWaterFront(beyonder);
            case 1 -> createWaterVortex(beyonder);
            case 2 -> launchGeysers(beyonder);
        }
    }

    private void launchGeysers(Beyonder beyonder) {
        if(castingGeysers.contains(beyonder))
            return;

        castingGeysers.add(beyonder);
        Bukkit.getScheduler().runTaskLater(plugin, () -> castingGeysers.remove(beyonder), 30);

        LivingEntity livingEntity = beyonder.getEntity();
        World world = livingEntity.getWorld();

        Location targetLocation = getTargetLocation(livingEntity, 30).subtract(0, .5, 0);

        for(int i = 0; i < 9; i++) {
            Location geyserLoc = i == 0 ? targetLocation.clone() : targetLocation.clone().add(random.nextDouble(-11, 11), 0, random.nextDouble(-11, 11));
            damageNearbyEntities(31, beyonder.getCurrentMultiplier(), livingEntity, 2, geyserLoc, world);
            for(LivingEntity targetEntity : getNearbyLivingEntities(livingEntity, 2, geyserLoc, world))
                targetEntity.setVelocity(new Vector(0, 2, 0));

            ParticleSpawner.displayParticles(world, Particle.DUST, geyserLoc, 20, 1, .8, 1, 0, dust, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, geyserLoc, 20, 1, .8, 1, 0, whiteDust, 200);

            Location cloudLoc = geyserLoc.clone();

            runTaskWithDuration(0, 15, () -> {
                geyserLoc.add(0, 1, 0);
                world.playSound(geyserLoc, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.6f, 1f));
                world.playSound(geyserLoc, Sound.ENTITY_DROWNED_SWIM, 1, random.nextFloat(.6f, .8f));

                ParticleSpawner.displayParticles(world, Particle.CLOUD, cloudLoc, 8, 1, .1, 1, 0.3, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, geyserLoc, 10, .1, .8, .1, 0, dust, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, geyserLoc, 10, .1, .8, .1, 0, whiteDust, 200);
            }, null);
        }
    }

    private void createWaterVortex(Beyonder beyonder) {
        if(castingVortex.contains(beyonder))
            return;

        castingVortex.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        Location targetLoc = getTargetLocation(entity, 30);
        world.playSound(targetLoc, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.6f, 1f));
        world.playSound(targetLoc, Sound.ENTITY_DROWNED_SWIM, 1, random.nextFloat(.6f, .8f));

        UUID locationUUID = UUID.randomUUID();
        LocationProvider.setLocation(locationUUID, targetLoc);

        BukkitTask vortex1 = ParticleUtil.spawnParticleTornado(Particle.FALLING_WATER, null, 4.5, 1.25, 8, 20 * 15, 30, 0, locationUUID, 0);
        BukkitTask vortex2 = ParticleUtil.spawnParticleTornado(Particle.DUST, dust, 4.5, 1.25, 8, 20 * 15, 30, 0, locationUUID, 0);
        BukkitTask vortex3 = ParticleUtil.spawnParticleTornado(Particle.FALLING_WATER, null, 4.5, 1.25, 8, 20 * 15, 30, 0, locationUUID, 5);
        BukkitTask vortex4 = ParticleUtil.spawnParticleTornado(Particle.DUST, dust, 4.5, 1.25, 8, 20 * 15, 30, 0, locationUUID, 5);
        BukkitTask vortex5 = ParticleUtil.spawnParticleTornado(Particle.CLOUD, null, 4.5, 1.25, 8, 20 * 12, 30, 0, locationUUID, 0);


        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {

                if(counter > 20 * 15) {
                    LocationProvider.removeLocation(locationUUID);
                    castingVortex.remove(beyonder);
                    cancel();
                    return;
                }

                world.playSound(targetLoc, Sound.ENTITY_DROWNED_SWIM, 1, random.nextFloat(.6f, .8f));

                if(counter % 15 == 0) {
                    damageNearbyEntities(20, beyonder.getCurrentMultiplier(), entity, 2.5, targetLoc, world, false, 0);
                    if(targetLoc.getBlock().getType() == Material.WATER) {
                        for(LivingEntity livingEntity : getNearbyLivingEntities(entity, 10, targetLoc, world)) {
                            Vector dir = targetLoc.toVector().subtract(livingEntity.getLocation().toVector()).normalize().multiply(.5);
                            livingEntity.setVelocity(livingEntity.getVelocity().add(dir));
                        }
                    }
                }

                counter+= 5;
            }
        }.runTaskTimer(plugin, 10, 5);
    }

    private void createWaterFront(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Vector direction = entity.getEyeLocation().getDirection();

        direction.setY(0);
        direction.normalize();

        Location loc = entity.getEyeLocation().add(direction).add(direction);

        Vector perpVector = VectorUtil.rotateAroundY(direction, 90);

        World world = entity.getWorld();

        world.playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.6f, 1f));
        world.playSound(loc, Sound.ENTITY_DROWNED_SWIM, 1, random.nextFloat(.6f, .8f));

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                world.spawnParticle(Particle.DUST, loc, 25, 6, 6, 6, 0, dust);
                world.spawnParticle(Particle.DUST, loc, 25, 6, 6, 6, 0, whiteDust);

                ParticleSpawner.displayParticles(world, Particle.FALLING_WATER, loc, 25, 6, 6, 6, 0, 150);
                ParticleSpawner.displayParticles(world, Particle.DUST, loc, 25, 6, 6, 6, 0, dust, 150);
                ParticleSpawner.displayParticles(world, Particle.DUST, loc, 25, 6, 6, 6, 0, whiteDust,150);

                if(beyonder.isGriefingEnabled()) {
                    for(int i = -3; i < 3; i++) {
                        for(int j = -8; j < 8; j++) {
                            Location waterLoc = loc.clone().add(0, i, 0).add(perpVector.clone().multiply(j));
                            if(!waterLoc.getBlock().getType().isSolid() && waterLoc.clone().subtract(0, 1, 0).getBlock().getType().isSolid() && random.nextInt(4) == 0)
                                waterLoc.getBlock().setType(Material.WATER);
                        }
                    }
                }

                damageNearbyEntities(16, beyonder.getCurrentMultiplier(), entity, 9, loc, world, false, 0);

                loc.add(direction);

                if(counter > 300)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 0);
    }
}
