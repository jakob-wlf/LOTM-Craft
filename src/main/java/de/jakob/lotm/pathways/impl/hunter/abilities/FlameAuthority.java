package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.*;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor
public class FlameAuthority extends SelectableAbility {

    private final HashSet<Beyonder> castingFireRain = new HashSet<>();

    public FlameAuthority(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {"Meteor", "Rain of Fire", "Fireball", "Flame Pillar"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 350,
                1, 400,
                2, 285,
                3, 400
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> castMeteor(beyonder);
            case 1 -> castFireRain(beyonder);
            case 2 -> launchFireball(beyonder);
            case 3 -> castFlamePillar(beyonder);
        }
    }

    private void castFlamePillar(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        Location loc = getTargetLocation(entity, 25).subtract(0, 2, 0);

        World world = loc.getWorld();
        if(world == null)
            return;

        UUID locationUUID = UUID.randomUUID();

        LocationProvider.setLocation(locationUUID, loc);

        BukkitTask tornadoTask = ParticleUtil.spawnParticleTornado(Particle.FLAME, null, 20, 4, 4, 20 * 20, 150, 0, .5, locationUUID, 0);
        BukkitTask tornadoTask2 = ParticleUtil.spawnParticleTornado(Particle.FLAME, null, 20, 4, 4, 20 * 20, 150, 0, .5, locationUUID, 5);
        BukkitTask tornadoTask3 = ParticleUtil.spawnParticleTornado(Particle.FLAME, null, 20, 4, 4, 20 * 20, 150, 0, .5, locationUUID, 10);
        BukkitTask tornadoTask4 = ParticleUtil.spawnParticleTornado(Particle.FLAME, null, 20, 4, 4, 20 * 20, 150, 0, .5, locationUUID, 15);

        runTaskWithDuration(10, 20 * 12, () -> {
            damageNearbyEntities(50, 1, entity, 5, loc, world, true, 20 * 2, 10, false);
            world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 5f, .85f);
        }, () -> {
            tornadoTask.cancel();
            tornadoTask2.cancel();
            tornadoTask3.cancel();
            tornadoTask4.cancel();
        });
    }

    private void launchFireball(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Vector direction = getDirectionNormalized(entity, 30).multiply(1.5);
        Location loc = entity.getEyeLocation().add(direction);

        World world = loc.getWorld();

        if(world == null)
            return;

        world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);

        ItemDisplay fireball = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        fireball.setItemStack(new ItemStack(Material.FIRE_CHARGE));
        fireball.setGravity(false);

        Transformation transformation = fireball.getTransformation();
        transformation.getScale().set(2.2);
        fireball.setTransformation(transformation);


        AtomicBoolean hasHit = new AtomicBoolean(false);

        runTaskWithDuration(1, 20 * 8, () -> {
            if(hasHit.get())
                return;

            if(damageNearbyEntities(55, beyonder.getCurrentMultiplier(), entity, 3, loc, world, true, 20 * 8, 10, true)) {
                world.createExplosion(loc, 7, beyonder.isGriefingEnabled(), beyonder.isGriefingEnabled(), entity);

                fireball.remove();
                hasHit.set(true);
                return;
            }

            if(loc.getBlock().getType().isSolid()) {
                loc.subtract(direction);
                damageNearbyEntities(55, beyonder.getCurrentMultiplier(), entity, 3, loc, world, true, 20 * 8, 10, true);
                world.createExplosion(loc, 7, beyonder.isGriefingEnabled(), beyonder.isGriefingEnabled(), entity);

                if(!beyonder.isGriefingEnabled()) {
                    fireball.remove();
                    hasHit.set(true);
                    return;
                }
            }

            fireball.teleport(loc);
            ParticleSpawner.displayParticles(world, Particle.FLAME, loc, 180, .6, .6, .6, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.SMOKE, loc, 90, .6, .6, .6, 0, 200);

            damageNearbyEntities(40, beyonder.getCurrentMultiplier(), entity, 3, loc, world, true, 20 * 8);

            if(beyonder.isGriefingEnabled()) {
                List<Block> blocks = BlockUtil.getBlocksInCircleRadius(loc.getBlock(), 4, false);
                blocks.forEach(b -> {
                    if(!b.getType().isSolid()) {
                        if(random.nextInt(4) == 0)
                            b.setType(Material.FIRE);
                    }
                    else {
                        if(random.nextInt(4) == 0)
                            b.setType(Material.BASALT);
                    }
                });
            }

            loc.add(direction);
        }, fireball::remove);
    }

    private void castFireRain(Beyonder beyonder) {
        if(castingFireRain.contains(beyonder))
            return;

        castingFireRain.add(beyonder);
        LivingEntity entity = beyonder.getEntity();

        Location targetLocation = getTargetLocation(entity, 30);

        World world = targetLocation.getWorld();
        if(world == null)
            return;


        runTaskWithDuration(4, 20 * 20, () -> {
            for(int i = 0; i < 13; i++) {
                Location loc = targetLocation.clone().add(random.nextDouble(-26, 26), 30, random.nextDouble(-26, 26));

                world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);

                AtomicBoolean canceled = new AtomicBoolean(false);
                runTaskWithDuration(1, 20 * 2, () -> {
                    if(canceled.get())
                        return;

                    if(loc.getBlock().getType().isSolid()) {
                        canceled.set(true);
                        loc.add(0, 1, 0);
                        damageNearbyEntities(55, beyonder.getCurrentMultiplier(), entity, 3.75, loc, world, true, 20 * 6, 0, true);
                        world.createExplosion(loc, 3.5f, beyonder.isGriefingEnabled(), beyonder.isGriefingEnabled());
                        world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
                        return;
                    }

                    ParticleSpawner.displayParticles(world, Particle.FLAME, loc, 20, .2, .2, .2, 0, 200);
                    ParticleSpawner.displayParticles(world, Particle.SMOKE, loc, 10, .2, .2, .2, 0, 200);
                    ParticleSpawner.displayParticles(world, Particle.ASH, loc, 12, .2, .2, .2, 0, 200);

                    loc.subtract(0, 1, 0);
                }, null);
            }
        }, () -> castingFireRain.remove(beyonder));


    }

    private void castMeteor(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        Location targetLocation = getTargetLocation(entity, 30);

        Vector offsetDirection = VectorUtil.rotateAroundYPositive90(entity.getLocation().getDirection().setY(0)).normalize();
        Location startLocation = targetLocation.clone().add(offsetDirection.multiply(16)).add(0, 25, 0);
        Vector direction = targetLocation.clone().toVector().subtract(startLocation.clone().toVector()).normalize().multiply(2);

        World world = startLocation.getWorld();
        if(world == null)
            return;

        ItemDisplay meteor = (ItemDisplay) world.spawnEntity(startLocation, EntityType.ITEM_DISPLAY);
        meteor.setItemStack(new ItemStack(Material.MAGMA_BLOCK));
        Transformation transformation = meteor.getTransformation();
        transformation.getScale().set(3);
        transformation.getLeftRotation().set((new Quaternionf(random.nextFloat(-1, 1), random.nextFloat(-1, 1), random.nextFloat(-1, 1), random.nextFloat() * Math.PI * 2)).normalize());
        meteor.setTransformation(transformation);

        meteor.setGravity(false);

        AtomicBoolean hasHit = new AtomicBoolean(false);

        runTaskWithDuration(1, 20 * 5, () -> {
            if(hasHit.get())
                return;
            if(startLocation.getBlock().getType().isSolid()) {
                startLocation.subtract(direction);
                hasHit.set(true);
                world.createExplosion(startLocation, 29, beyonder.isGriefingEnabled(), beyonder.isGriefingEnabled(), entity);
                damageNearbyEntities(65, beyonder.getCurrentMultiplier(), entity, 18, startLocation, world, true, 20 * 8, 0, true);
                ParticleSpawner.displayParticles(world, Particle.FLAME, startLocation, 900, 7, .75, 7, .1, 300);
                ParticleSpawner.displayParticles(world, Particle.ASH, startLocation, 900, 7, .75, 7, .1, 300);
                ParticleSpawner.displayParticles(world, Particle.EXPLOSION, startLocation, 50, 7, .75, 7, .1, 300);
                meteor.remove();
                return;
            }

            meteor.teleport(new Location(world, startLocation.getX(), startLocation.getY(), startLocation.getZ(), meteor.getLocation().getYaw(), meteor.getLocation().getPitch()));
            ParticleSpawner.displayParticles(world, Particle.FLAME, startLocation, 400, 1.6, 1.6, 1.6, 0, 300);
            ParticleSpawner.displayParticles(world, Particle.SMOKE, startLocation, 100, 1.6, 1.6, 1.6, 0, 300);
            ParticleSpawner.displayParticles(world, Particle.LAVA, startLocation, 100, 1.6, 1.6, 1.6, 0, 300);
            ParticleSpawner.displayParticles(world, Particle.ASH, startLocation, 100, 1.6, 1.6, 1.6, 0, 300);
            ParticleSpawner.displayParticles(world, Particle.EXPLOSION, startLocation, 3, 1.6, 1.6, 1.6, 0, 300);

            startLocation.add(direction);
        }, meteor::remove);

    }
}
