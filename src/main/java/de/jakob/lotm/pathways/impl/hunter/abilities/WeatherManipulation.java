package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.LocationProvider;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.pathways.TyrantUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

@NoArgsConstructor
public class WeatherManipulation extends SelectableAbility {

    private final HashMap<Beyonder, Integer> tornadoCount = new HashMap<>();
    private final HashSet<Beyonder> castingBlizzard = new HashSet<>();
    private final HashSet<Beyonder> castingDrought = new HashSet<>();
    private final HashSet<Beyonder> castingStorm = new HashSet<>();

    private final Particle.DustOptions droughtDust = new Particle.DustOptions(Color.fromRGB(212, 116, 72), 5f);
    final Particle.DustOptions rainDust = new Particle.DustOptions(Color.fromRGB(30, 120, 255), 1.25f);


    public WeatherManipulation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {"Tornado", "Blizzard", "Drought", "Storm"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 300,
                1, 300,
                2, 300,
                3, 300
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> castTornado(beyonder);
            case 1 -> castBlizzard(beyonder);
            case 2 -> castDrought(beyonder);
            case 3 -> castStorm(beyonder);
        }
    }

    private void castStorm(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 25).add(0, 1, 0);

        World world = targetLoc.getWorld();
        if(world == null)
            return;

        if(castingStorm.contains(beyonder))
            return;

        castingStorm.add(beyonder);

        Bukkit.getScheduler().runTaskLater(plugin, () -> castingStorm.remove(beyonder), 20 * 20);

        List<Block> blocks = BlockUtil.getBlocksInCircleRadius(targetLoc.getBlock(), 25, true);

        runTaskWithDuration(5, 20 * 25, () -> {
            world.playSound(targetLoc, Sound.WEATHER_RAIN, 10, 1);

            ParticleSpawner.displayParticles(world, Particle.FALLING_WATER, targetLoc, 2100, 21, 14, 21, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, targetLoc, 2100, 21, 14, 21, 0, rainDust, 200);
            ParticleSpawner.displayParticles(world, Particle.CLOUD, targetLoc.clone().add(0, 16, 0), 2100, 21, 0, 21, 0, 200);

            damageNearbyEntities(45, beyonder.getCurrentMultiplier(), entity, 22, targetLoc, world);

            if(random.nextInt(5) == 0) {
                Location location = targetLoc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
                TyrantUtil.strikeLightning(location, beyonder.isGriefingEnabled(), TyrantUtil.blueDustBig, TyrantUtil.blueDust2, 4);
                damageNearbyEntities(29, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 8, location, beyonder.getEntity().getWorld(), false, 0);
            }

            if(beyonder.isGriefingEnabled()) {
                List<Block> possibleBlocks = blocks.stream().filter(b -> !b.getRelative(0, 1, 0).getType().isSolid()).toList();
                for(int i = 0; i < 17; i++)
                    possibleBlocks.get(random.nextInt(possibleBlocks.size())).setType(Material.AIR);

            }
        }, null);
    }

    private void castBlizzard(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 25).add(0, 1, 0);

        World world = targetLoc.getWorld();
        if(world == null)
            return;

        if(castingBlizzard.contains(beyonder))
            return;

        castingBlizzard.add(beyonder);

        Bukkit.getScheduler().runTaskLater(plugin, () -> castingBlizzard.remove(beyonder), 20 * 20);

        runTaskWithDuration(5, 20 * 20, () -> {
            ParticleSpawner.displayParticles(world, Particle.SNOWFLAKE, targetLoc, 1500, 18, 7, 18, 0.1, 200);
            damageNearbyEntities(38, beyonder.getCurrentMultiplier(), entity, 20, targetLoc, world);
            addPotionEffectToNearbyEntities(entity, 20, targetLoc, world, new PotionEffect(PotionEffectType.SLOWNESS, 20, 4, false, false, false));
        }, null);

    }

    private void castDrought(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 25).add(0, 1, 0);

        World world = targetLoc.getWorld();
        if(world == null)
            return;

        if(castingDrought.contains(beyonder))
            return;

        castingDrought.add(beyonder);

        Bukkit.getScheduler().runTaskLater(plugin, () -> castingDrought.remove(beyonder), 20 * 20);

        runTaskWithDuration(5, 20 * 20, () -> {
            ParticleSpawner.displayParticles(world, Particle.DUST, targetLoc, 1000, 18, 4, 18, 0, droughtDust, 200);
            ParticleSpawner.displayParticles(world, Particle.FLAME, targetLoc, 1000, 18, 4, 18, 0, 200);
            damageNearbyEntities(38, beyonder.getCurrentMultiplier(), entity, 20, targetLoc, world, true, 20 * 5);
            addPotionEffectToNearbyEntities(entity, 20, targetLoc, world, new PotionEffect(PotionEffectType.SLOWNESS, 20, 4, false, false, false));
        }, null);

    }

    private void castTornado(Beyonder beyonder) {
        if(tornadoCount.containsKey(beyonder) && tornadoCount.get(beyonder) >= 3)
            return;

        if(!tornadoCount.containsKey(beyonder))
            tornadoCount.put(beyonder, 1);
        else
            tornadoCount.replace(beyonder, tornadoCount.get(beyonder) + 1);

        LivingEntity entity = beyonder.getEntity();
        LivingEntity targetEntity = getTargetEntity(entity, 25);

        Location loc = getTargetLocation(entity, 30);
        World world = entity.getWorld();

        UUID locationUUID = UUID.randomUUID();

        LocationProvider.setLocation(locationUUID, loc);

        BukkitTask tornadoTask = ParticleUtil.spawnParticleTornado(Particle.CLOUD, null, 13.5, .2, 5.5, 20 * 20, 60, 0, locationUUID, 0);
        BukkitTask tornadoTask2 = ParticleUtil.spawnParticleTornado(Particle.CLOUD, null, 13.5, .2, 5.5, 20 * 20, 60, 0, locationUUID, 5);

        new BukkitRunnable() {

            Location currentLoc = loc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
            int counter = 0;

            @Override
            public void run() {

                if(counter > 20 * 20) {
                    LocationProvider.removeLocation(locationUUID);
                    tornadoCount.replace(beyonder, tornadoCount.get(beyonder) - 1);
                    cancel();
                    return;
                }

                if(targetEntity != null) {
                    Vector dir = targetEntity.getLocation().toVector().subtract(loc.clone().toVector()).normalize().multiply(.8);
                    loc.add(dir);
                }
                else {
                    Vector dir = currentLoc.toVector().subtract(loc.toVector()).normalize().multiply(.8);
                    loc.add(dir);

                    if(loc.distance(currentLoc) < 2)
                        currentLoc = loc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
                }

                world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, .5f);
                LocationProvider.setLocation(locationUUID, loc);

                if(counter % 10 == 0) {
                    if(!beyonder.removeSpirituality(3)) {
                        cancel();
                        tornadoTask.cancel();
                        tornadoTask2.cancel();
                        tornadoCount.replace(beyonder, tornadoCount.get(beyonder) - 1);
                        return;
                    }
                    damageNearbyEntities(35, beyonder.getCurrentMultiplier(), entity, 2.5, loc, world, false, 0, 20);
                }

                counter+= 5;
            }
        }.runTaskTimer(plugin, 10, 5);
    }
}
