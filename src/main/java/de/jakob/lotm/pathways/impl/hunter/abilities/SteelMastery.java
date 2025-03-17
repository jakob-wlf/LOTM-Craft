package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@NoArgsConstructor
public class SteelMastery extends Ability {
    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Steel Chains", "Steel Whip", "Steel Spikes"};
    private final HashMap<Integer, Integer> spiritualityCost = new HashMap<>(Map.of(
            0, 195,
            1, 150,
            2, 200
    ));

    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(100, 100, 100), 1.85f);

    private final Random random= new Random();

    public SteelMastery(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        LivingEntity entity = beyonder.getEntity();

        int selectedAbility = selectedAbilities.get(beyonder);

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(entity, selectedAbility, beyonder.getCurrentMultiplier());
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 1);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > abilities.length - 1)
            selectedAbilities.replace(beyonder, 0);
    }

    private void castAbility(LivingEntity entity, int ability, double multiplier) {
        switch(ability) {
            case 0 -> launchSteelChains(entity, multiplier);
            case 1 -> createSteelWhip(entity, multiplier);
            case 2 -> launchSteelSpikes(entity, multiplier);
        }
    }

    private void launchSteelSpikes(LivingEntity livingEntity, double multiplier) {
        World world = livingEntity.getWorld();

        Location destination = getTargetBlock(livingEntity, 75).getLocation();

        Location[] locations = new Location[7];
        for(int i = 0; i < locations.length; i++) {
            locations[i] = destination.clone().add(random.nextInt(-6, 6), 0, random.nextInt(-6, 6));
            if(!locations[i].clone().subtract(0, 1, 0).getBlock().getType().isSolid())
                locations[i] = locations[i].subtract(0, 1, 0);
        }

        locations[0] = destination.clone();

        int[] heights = new int[locations.length];
        for(int i = 0; i < heights.length; i++) {
            heights[i] = random.nextInt(3) + 2;
        }

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                for(int i = 0; i < locations.length; i++) {
                    int height = heights[i];

                    ParticleUtil.createSpike(
                            locations[i],
                            height,
                            1.5,
                            Particle.DUST,
                            dustOptions,
                            0.1,
                            0.1,
                            0.1,
                            .8
                    );

                }

                if(counter > 75)
                    cancel();
            }

        }.runTaskTimer(LOTM.getInstance(), 0, 2);

        for(Location location : locations) {
            world.createExplosion(location, 4, false, false);

            damageNearbyEntities(15, multiplier, livingEntity, 2.2, location, world, false, 0);

            for(LivingEntity entity : getNearbyLivingEntities(livingEntity, 2.2, location, world)) {
                entity.setVelocity(entity.getVelocity().setY(1.25));
            }
        }
    }

    private void createSteelWhip(LivingEntity livingEntity, double multiplier) {
        Vector direction = livingEntity.getEyeLocation().getDirection().normalize();
        Location location = livingEntity.getEyeLocation().add(direction);
        World world = livingEntity.getWorld();

        world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .2f);

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
                    world.spawnParticle(Particle.DUST, particleLocation, 4, 0.05, 0.05, 0.05, 0, dustOptions);

                    if(damageNearbyEntities(13, multiplier, livingEntity, 2, particleLocation, world, false, 0)) {
                        cancel();
                        return;
                    }

                    t += 0.2;
                }

                // Stop the effect after 10 blocks
                if (t > 24) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick
    }

    private void displaySwordAtLocation(Location location, Vector direction, int time, int length, double stepSize, Particle.DustOptions dustOptions) {
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                ParticleUtil.createParticleSword(location, direction, length, Particle.DUST, stepSize, dustOptions);

                if(counter > time)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void launchSteelChains(LivingEntity livingEntity, double multiplier) {
        LivingEntity target = getTargetEntity(livingEntity, (int) Math.round(15 * multiplier));

        if(target == null)
            return;

        int time = (int) Math.round(20 * multiplier);

        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, time, 6);
        target.addPotionEffect(slowness);

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(60, 60, 60), 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                if(!target.isValid()) {
                    cancel();
                    return;
                }

                Location targetLocation = target.getEyeLocation().subtract(0, .6, 0);
                target.setVelocity(new Vector(0, 0, 0));
                ParticleUtil.drawLine(targetLocation.clone().add(4.5, -3.5, 0), targetLocation, Particle.DUST, 0.2, livingEntity.getWorld(), dustOptions, 1, 0);
                ParticleUtil.drawLine(targetLocation.clone().add(-4.5, -3.5, 0), targetLocation, Particle.DUST, 0.2, livingEntity.getWorld(), dustOptions, 1, 0);
                ParticleUtil.drawLine(targetLocation.clone().add(0, -3.5, 4.5), targetLocation, Particle.DUST, 0.2, livingEntity.getWorld(), dustOptions, 1, 0);
                ParticleUtil.drawLine(targetLocation.clone().add(0, -3.5, -4.5), targetLocation, Particle.DUST, 0.2, livingEntity.getWorld(), dustOptions, 1, 0);

                if (counter > time) {
                    cancel();
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 2);
    }


    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8" + abilities[selectedAbilities.get(beyonder)]));
    }
}
