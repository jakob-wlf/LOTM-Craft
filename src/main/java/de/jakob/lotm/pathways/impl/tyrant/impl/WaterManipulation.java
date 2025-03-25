package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@NoArgsConstructor
public class WaterManipulation extends Ability {
    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Water Whip", "Aqueous Light", "Water Bolt", "Corrosive Rain", "Water Surge"};
    private final HashMap<Integer, Integer> spiritualityCost = new HashMap<>(Map.of(
            0, 15,
            1, 10,
            2, 20,
            3, 25,
            4, 30
    ));
    private final Set<Beyonder> castingCorrosiveRain = new HashSet<>();

    final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(30, 120, 255), 1.25f);

    private final Random random = new Random();

    public WaterManipulation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder) && beyonder instanceof BeyonderPlayer)
            selectedAbilities.put(beyonder, 0);

        int selectedAbility = beyonder instanceof BeyonderPlayer ? selectedAbilities.get(beyonder) : random.nextInt(abilities.length);
        if(selectedAbility == 1 && !(beyonder instanceof BeyonderPlayer))
            selectedAbility = 0;

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(beyonder, selectedAbility);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > abilities.length - 1 || (beyonder.getCurrentSequence() > 5 && selectedAbilities.get(beyonder) > 2))
            selectedAbilities.replace(beyonder, 0);
    }

    private void castAbility(Beyonder beyonder, int ability) {
        switch(ability) {
            case 0 -> castWaterWhip(beyonder);
            case 1 -> castAqueousLight(beyonder);
            case 2 -> castWaterBolt(beyonder);
            case 3 -> castCorrosiveRain(beyonder);
            case 4 -> castWaterSurge(beyonder);
        }
    }

    private void castWaterSurge(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location startLocation = entity.getLocation();

        World world = entity.getWorld();

        world.playSound(startLocation, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.55f, .8f));
        world.playSound(startLocation, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.55f, .8f));
        world.playSound(startLocation, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.55f, .8f));

        new BukkitRunnable() {
            double radius = 2;
            @Override
            public void run() {

                double height = 1f / (.5 * (radius - 2));

                Location loc = startLocation.clone();
                loc.setY(loc.getY() + height);

                ParticleUtil.drawParticleCircle(loc, radius, Particle.FALLING_WATER, null, 45, .5, 14);
                if(beyonder.isGriefingEnabled())
                    for(Block block : BlockUtil.getPassableBlocksInCircle(loc, radius, 50)) {
                        if(block.getRelative(0, -1, 0).getType().isSolid() && random.nextInt(14) == 0)
                            block.setType(Material.WATER);
                    }

                for(LivingEntity target : getNearbyLivingEntities(entity, radius, startLocation, entity.getWorld())) {
                    if(target.getWorld() != entity.getWorld())
                        continue;

                    double distance = target.getLocation().distance(entity.getLocation());
                    if(Math.sqrt((distance - radius) * (distance - radius)) <= 1.5) {
                        target.damage(18 * beyonder.getCurrentMultiplier(), entity);
                        target.setNoDamageTicks(14);
                        world.playSound(target.getEyeLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, 1, random.nextFloat(.6f, 1f));
                    }
                }

                radius += .5;
                if(radius > 15)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void castCorrosiveRain(Beyonder beyonder) {
        if(castingCorrosiveRain.contains(beyonder))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getEyeLocation();
        World world = entity.getWorld();

        castingCorrosiveRain.add(beyonder);

        runTaskWithDuration(7, 20 * 16,  () -> {
            world.playSound(loc, Sound.WEATHER_RAIN, 1, 1);

            ParticleSpawner.displayParticles(world, Particle.FALLING_WATER, loc, 500, 7, 7, 7, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.CLOUD, loc.clone().add(0, 9, 0), 500, 7, 0, 7, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, loc, 500, 7, 7, 7, 0, dust, 200);
            ParticleSpawner.displayParticles(world, Particle.SNEEZE, loc, 200, 7, 7, 7, 0, 200);

            damageNearbyEntities(3.75, beyonder.getCurrentMultiplier(), entity, 10, loc, world);

        },() -> {
            castingCorrosiveRain.remove(beyonder);
        });
    }

    private void castWaterBolt(Beyonder beyonder) {
        Entity target = getTargetEntity(beyonder.getEntity(), 15);

        Location targetLoc = target != null ? target.getLocation() : getTargetBlock(beyonder.getEntity(), 15).getLocation();
        Location loc = beyonder.getEntity().getEyeLocation();

        World world = beyonder.getEntity().getWorld();

        Vector dir = targetLoc.toVector().subtract(beyonder.getEntity().getLocation().toVector()).normalize().multiply(.8);

        world.playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 1, 1);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if(counter >= 30 || beyonder.getEntity() == null || beyonder.getEntity().isDead()) {
                    cancel();
                    return;
                }

                world.spawnParticle(Particle.DUST, loc, 10, 0.25, 0.25, 0.25, dust);
                world.spawnParticle(Particle.BUBBLE, loc, 5, 0.25, 0.25, 0.25, 0);

                if(damageNearbyEntities(14, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2, loc, world, false, 0)) {
                    cancel();
                    return;
                }

                if(loc.getBlock().getType().isSolid()) {
                    if(beyonder.isGriefingEnabled()) {
                        loc.subtract(dir).getBlock().setType(Material.WATER);
                    }
                    cancel();
                    return;
                }

                loc.add(dir);

                counter++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void castWaterWhip(Beyonder beyonder) {
        Vector direction = beyonder.getEntity().getEyeLocation().getDirection().normalize();
        Location location = beyonder.getEntity().getEyeLocation().add(direction);
        World world = beyonder.getEntity().getWorld();

        world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .2f);
        world.playSound(location, Sound.ENTITY_PLAYER_SPLASH, 1, 1);

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
                    world.spawnParticle(Particle.DRIPPING_DRIPSTONE_WATER, particleLocation, 4, 0.05, 0.05, 0.05, 0);

                    if(damageNearbyEntities(13, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2, particleLocation, world, false, 0)) {
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

    private void castAqueousLight(Beyonder beyonder) {
        Location loc = getTargetBlock(beyonder.getEntity(), 10, true).getLocation();

        loc.getBlock().setType(Material.LIGHT);
        loc.add(0.5, 0.5, 0.5);

        final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(143, 255, 244), 1.75f);

        LOTM.getInstance().getBlocksToRemove().add(loc);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {

                if (loc.getWorld() == null)
                    return;

                counter++;
                double x = Math.cos(counter);
                double z = Math.sin(counter);
                double y = Math.sin(counter);
                if (random.nextBoolean())
                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0, 0, 0, 0);
                if (random.nextBoolean())
                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY() + y, loc.getZ(), 1, 0, 0, 0, 0);
                y = Math.cos(counter);
                if (random.nextBoolean())
                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX(), loc.getY() + y, loc.getZ() + z, 1, 0, 0, 0, 0);

                loc.getWorld().spawnParticle(Particle.DUST, loc, 10, 0.25, 0.25, 0.25, dust);

                if (counter >= 15 * 20) {
                    loc.getBlock().setType(Material.AIR);
                    LOTM.getInstance().getBlocksToRemove().remove(loc);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);

    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§9" + abilities[selectedAbilities.get(beyonder)]));
    }
}
