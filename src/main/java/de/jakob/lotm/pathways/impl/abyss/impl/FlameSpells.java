package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class FlameSpells extends SelectableAbility {

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(168, 48, 35), 1f);
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(204, 123, 53), 1f);
    private final Particle.DustOptions dustOptions3 = new Particle.DustOptions(Color.fromRGB(232, 188, 84), 1f);
    private final Particle.DustOptions dustOptions4 = new Particle.DustOptions(Color.fromRGB(178, 232, 84), 1f);

    public FlameSpells(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Poisonous Flame Whip", "Sulfur Fireball", "Volcanic Eruption"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 23,
                1, 23,
                2, 30
        ));
    }


    //TODO: Lethal Poison Steam
    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> castPoisonousFlameWhip(beyonder);
            case 1 -> castSulfurFireball(beyonder);
            case 2 -> castVolcanicEruption(beyonder);
        }
    }

    private void castVolcanicEruption(Beyonder beyonder) {
        Location location = beyonder.getEntity().getLocation();
        World world = beyonder.getEntity().getWorld();

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        ParticleSpawner.displayParticles(world, Particle.EXPLOSION, location, 5, 2, 0.2, 2, 0.1, 200);

        runTaskWithDuration(1, 15, () -> {
            ParticleSpawner.displayParticles(world, Particle.FLAME, location, 30, 2, 1.3, 2, 0.1, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, location, 50, 2, 1.3, 2, 0, dustOptions, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, location, 50, 2, 1.3, 2, 0, dustOptions2, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, location, 50, 2, 1.3, 2, 0, dustOptions3, 200);

            if(damageNearbyEntities(12, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2, location, world, true, 30)) {
                addPotionEffectToNearbyEntities(beyonder.getEntity(), 2, location, world, PotionEffectType.POISON.createEffect(20, 1));
                return;
            }

            if(beyonder.isGriefingEnabled()) {
                for(Block block : BlockUtil.getBlocksInCircleRadius(location.getBlock(), 5, false)) {
                    if(random.nextInt(22) == 0 && !block.getType().isSolid()) {
                        block.setType(Material.FIRE);
                    }
                    else if(random.nextInt(22) == 0 && block.getType().isSolid()) {
                        FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation().add(0, 1.5, 0), block.getBlockData());
                        Vector direction = location.toVector().subtract(block.getLocation().toVector()).setY(-1).normalize().multiply(-.8);
                        fallingBlock.setVelocity(direction);
                        block.setType(Material.AIR);
                    }
                }
            }

            location.add(0, .15, 0);
        }, null);
    }

    private void castSulfurFireball(Beyonder beyonder) {
        Location targetLocation = getTargetLocation(beyonder.getEntity(), 40);
        Location location = beyonder.getEntity().getEyeLocation().add(beyonder.getEntity().getLocation().getDirection()).add(random.nextDouble(-1.5, 1.5), random.nextDouble(.5, 1.5), random.nextDouble(-1.5, 1.5));
        Vector direction = targetLocation.toVector().subtract(location.toVector()).normalize().multiply(1.2);
        World world = beyonder.getEntity().getWorld();

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter >= 20 * 8) {
                    cancel();
                    return;
                }

                ParticleSpawner.displayParticles(world, Particle.FLAME, location, 2, 0.2, 0.2, 0.2, 0, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, location, 10, 0.2, 0.2, 0.2, 0, dustOptions, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, location, 10, 0.2, 0.2, 0.2, 0, dustOptions2, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, location, 10, 0.2, 0.2, 0.2, 0, dustOptions3, 200);

                if(damageNearbyEntities(19, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2, location, world, true, 30)) {
                    addPotionEffectToNearbyEntities(beyonder.getEntity(), 2, location, world, PotionEffectType.POISON.createEffect(20, 1));
                    cancel();
                    return;
                }

                if(location.getBlock().getType().isSolid()) {
                    if(beyonder.isGriefingEnabled())
                        world.createExplosion(location, 1, true, false);
                    cancel();
                    return;
                }

                location.add(direction);

                counter++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void castPoisonousFlameWhip(Beyonder beyonder) {
        Vector direction = beyonder.getEntity().getEyeLocation().getDirection().normalize();
        Location location = beyonder.getEntity().getEyeLocation().add(direction);
        World world = beyonder.getEntity().getWorld();

        world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .2f);
        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

        new BukkitRunnable() {
            double t = 0; // Time variable for sine wave

            @Override
            public void run() {
                // Compute the point on the sine wave for the given t
                for(int i = 0; i < 4; i++) {
                    double x = t; // Move forward in the player's looking direction
                    double y = Math.sin(t * 2); // Sine wave pattern for Y-axis
                    double z = Math.cos(t * 1.5) * 0.5; // Slight tilt with cosine wave for Z-axis

                    // Create a local vector for the sine wave position
                    Vector wavePoint = new Vector(x, y, z);

                    // Rotate the vector to match the player's direction
                    VectorUtil.rotateAroundDirection(wavePoint, direction);

                    // Add the player's location to shift the wave to the correct starting point
                    Location particleLocation = location.clone().add(wavePoint);

                    // Spawn the particle at the calculated location
                    ParticleSpawner.displayParticles(world, Particle.FLAME, particleLocation, 1, 0.05, 0.05, 0.05, 0, 200);
                    ParticleSpawner.displayParticles(world, Particle.DUST, particleLocation, 4, 0.05, 0.05, 0.05, 0, dustOptions2, 200);
                    ParticleSpawner.displayParticles(world, Particle.DUST, particleLocation, 4, 0.05, 0.05, 0.05, 0, dustOptions4, 200);

                    if(damageNearbyEntities(17, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2, particleLocation, world, true, 30)) {
                        addPotionEffectToNearbyEntities(beyonder.getEntity(), 2, particleLocation, world, PotionEffectType.POISON.createEffect(20, 1));
                        cancel();
                        return;
                    }

                    t += 0.2;
                }

                // Stop the effect after 10 blocks
                if (t > 10) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick
    }
}
