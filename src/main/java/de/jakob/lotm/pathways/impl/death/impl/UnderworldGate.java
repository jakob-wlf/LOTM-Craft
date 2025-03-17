package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.entity.spirit.*;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import de.jakob.lotm.util.pathways.DeathUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class UnderworldGate extends SelectableAbility {

    private final HashMap<Beyonder, ItemDisplay> casting = new HashMap<>();

    public UnderworldGate(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Tentacles", "Suction", "Spirits"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 70,
                1, 70,
                2, 70
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> castTentacleVersion(beyonder);
            case 1 -> castSuctionVersion(beyonder);
            case 2 -> castSpiritVersion(beyonder);
        }
    }

    private void castSpiritVersion(Beyonder beyonder) {
        casting.remove(beyonder);

        GateResponse gate = createGate(beyonder, true, 5);
        if(gate == null)
            return;

        Location loc = gate.location.clone().subtract(0, .5, 0);
        ItemDisplay itemDisplay = gate.itemDisplay;
        Vector gateDirection = gate.gateDirection;

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!itemDisplay.isValid()) {
                    cancel();
                    return;
                }

                if(!casting.containsKey(beyonder) || casting.get(beyonder) != itemDisplay) {
                    cancel();
                    return;
                }

                if(random.nextInt(25) != 0)
                    return;

                BeyonderSpirit spirit = switch(random.nextInt(6)) {
                    case 0, 1 -> VexSpirit.spawn(loc);
                    case 2, 3 -> WitherSpirit.spawn(loc);
                    case 4 -> CrawlerSpirit.spawn(loc);
                    case 5 -> SkeletonSpirit.spawn(loc);
                    default -> DefaultSpirit.spawn(loc);
                };

                spirit.setLifespan(50);
                
                spirit.getEntity().setVelocity(gateDirection);
                spirit.getEntity().addScoreboardTag("belongs_to_" + beyonder.getEntity().getUniqueId());

            }
        }.runTaskTimer(plugin, 15, 3);
    }

    private void castTentacleVersion(Beyonder beyonder) {
        casting.remove(beyonder);

        GateResponse gate = createGate(beyonder, true, 2.5);
        if(gate == null)
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = gate.location.clone();
        ItemDisplay itemDisplay = gate.itemDisplay;
        Vector gateDirection = gate.gateDirection;

        Location damageCenter = loc.clone().add(gateDirection.clone().multiply(2.4));

        List<Location[]> tentacleParticleLocations = new ArrayList<>();
        Location controlLoc = loc.clone().add(gateDirection.clone().multiply(-.5));

        for(int i = 0; i < 19; i++) {
            Location startLoc = loc.clone();
            Vector direction = gateDirection.clone().normalize().multiply(.1);
            Location[] locations = new Location[40];

            for(int j = 0; j < locations.length; j++) {
                if(startLoc.distance(loc) > 9)
                    break;

                locations[j] = startLoc.clone();

                direction.add(new Vector(random.nextDouble(.1) - .05, random.nextDouble(.075) - .0375, random.nextDouble(.1) - .05).normalize().multiply(.1));
                if(startLoc.clone().add(direction).distance(loc) > startLoc.clone().add(direction).distance(controlLoc))
                    direction = gateDirection.clone().normalize().multiply(.2);
                startLoc.add(direction);
            }

            tentacleParticleLocations.add(locations);
        }

        new BukkitRunnable() {

            int counter = 0;
            final Map<Integer, Double> tentaclePhases = new HashMap<>(); // Unique phase for each tentacle

            @Override
            public void run() {
                if (!itemDisplay.isValid()) {
                    cancel();
                    return;
                }

                if (!casting.containsKey(beyonder) || casting.get(beyonder) != itemDisplay) {
                    cancel();
                    return;
                }

                if(counter % 16 == 0) {
                    damageNearbyEntities(21, beyonder.getCurrentMultiplier(), entity, 2.95, damageCenter, entity.getWorld(), false, 0, 20, true);
                    addPotionEffectToNearbyEntities(entity, 2.95, damageCenter, entity.getWorld(), new PotionEffect(PotionEffectType.WITHER, 20, 1), new PotionEffect(PotionEffectType.SLOWNESS, 30, 13));
                }

                for (int i = 0; i < tentacleParticleLocations.size(); i++) {
                    double phase = tentaclePhases.computeIfAbsent(i, k -> Math.random() * Math.PI * 2); // Unique initial phase

                    double offsetX = 0.14 * Math.cos((counter / 10.0) + phase);
                    double offsetY = 0.1 * Math.cos((counter / 10.0) + phase);
                    double offsetZ = 0.14 * Math.sin((counter / 10.0) + phase);

                    for (Location originalLocation : tentacleParticleLocations.get(i)) {
                        if (originalLocation == null) continue;

                        Location movingLocation = originalLocation.clone().add(offsetX, offsetY, offsetZ);

                        ParticleSpawner.displayParticles(entity.getWorld(), Particle.DUST, movingLocation, 1, 0, 0, 0, 0,
                                new Particle.DustOptions(Color.fromRGB(80, 20, 20), 1.35f), 200);
                    }
                }

                counter += 2;
            }
        }.runTaskTimer(plugin, 15, 2);

    }

    private void castSuctionVersion(Beyonder beyonder) {
        casting.remove(beyonder);

        GateResponse gate = createGate(beyonder, true, 5);
        if(gate == null)
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = gate.location.clone().subtract(0, .5, 0);
        ItemDisplay itemDisplay = gate.itemDisplay;
        Vector gateDirection = gate.gateDirection;

        Location suckCenter = loc.clone().add(gateDirection.clone().multiply(7));

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!itemDisplay.isValid()) {
                    cancel();
                    return;
                }

                if(!casting.containsKey(beyonder) || casting.get(beyonder) != itemDisplay) {
                    cancel();
                    return;
                }

                List<LivingEntity> nearbyEntities = getNearbyLivingEntities(entity, 8, suckCenter, entity.getWorld()).stream().filter(e -> EntityUtil.mayDamage(e, entity)[1]).filter(e -> !e.getScoreboardTags().contains("spirit")).toList();
                for(LivingEntity target : nearbyEntities) {
                    Vector direction = loc.clone().toVector().subtract(target.getLocation().toVector()).normalize().multiply(.3);
                    if(target.getVelocity().length() < 2.2)
                        target.setVelocity(target.getVelocity().add(direction));
                    if(target.getLocation().distance(loc) < 1.6) {
                        teleportToNether(target);
                    }
                }

                for(int i = 0; i < 30; i++) {
                    Location particleLoc = suckCenter.clone().add(random.nextDouble(10) - 5, random.nextDouble(6.8) - 2, random.nextDouble(10) - 5);
                    Vector dir = loc.clone().toVector().subtract(particleLoc.toVector()).normalize();
                    ParticleSpawner.displayParticles(itemDisplay.getWorld(), Particle.SMOKE, particleLoc, 0, dir.getX(), dir.getY(), dir.getZ(), .43, 200);
                }

            }
        }.runTaskTimer(plugin, 15, 3);

    }

    private void teleportToNether(LivingEntity entity) {
        Location teleportLocation = createLocationInNether();
        if(teleportLocation == null) teleportLocation = new Location(entity.getWorld(), random.nextInt(1000) - 500, 295, random.nextInt(1000) - 500);
        World teleportWorld = teleportLocation.getWorld();

        if(teleportWorld == null) return;

        teleportWorld.loadChunk(teleportLocation.getChunk());

        entity.setFallDistance(0);
        entity.teleport(teleportLocation);
        entity.setFallDistance(0);
        entity.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20 * 5, 1));

        entity.setVelocity(new Vector(.75, .5, 0));
    }

    private Location netherLocation = null;
    private Location createLocationInNether() {
        if(netherLocation != null && !netherLocation.getBlock().getType().isSolid())
            return netherLocation;

        // Attempt to get the End world
        World endWorld = Bukkit.getWorld("world_nether");

        // If the world is not loaded, load it
        if (endWorld == null) {
            endWorld = Bukkit.createWorld(new WorldCreator("world_nether"));
        }

        // If the world is successfully loaded, create a Location
        if (endWorld != null) {
            Location location = new Location(endWorld, random.nextInt(200) - 100, 60, random.nextInt(200) - 100);
            while(location.getBlock().getType().isSolid()) {
                location.add(0, 1, 0);
            }
            netherLocation = location;
            return location;
        } else {
            return null;
        }
    }

    //TODO: Sounds

    private GateResponse createGate(Beyonder beyonder, boolean shouldBeFacingEnemy, double enemyDistance) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        LivingEntity target = getTargetEntity(entity, 15);

        boolean targetEnemy = target != null && shouldBeFacingEnemy;

        Location loc = targetEnemy ? target.getEyeLocation() : entity.getEyeLocation();

        Vector direction = targetEnemy ? VectorUtil.rotateAroundY(entity.getEyeLocation().getDirection().setY(0).normalize().multiply(.5), -90) : loc.getDirection().setY(0).normalize().multiply(.5);

        for(double i = 0; i < (targetEnemy ? enemyDistance : 7); i+=.5) {
            loc.add(direction);
            if(loc.getBlock().getType().isSolid())
                break;
        }

        ItemDisplay itemDisplay = DeathUtil.createDoor(loc.clone(), direction, 20 * 25, 12);

        if(itemDisplay == null)
            return null;

        casting.put(beyonder, itemDisplay);

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!itemDisplay.isValid()) {
                    casting.remove(beyonder);
                    cancel();
                    return;
                }

                if(!casting.containsKey(beyonder)) {
                    itemDisplay.remove();
                    cancel();
                    return;
                }

                if(casting.get(beyonder) != itemDisplay) {
                    itemDisplay.remove();
                    cancel();
                    return;
                }

                ParticleSpawner.displayParticles(world, Particle.SOUL, loc.clone().subtract(direction.clone().multiply(2)).subtract(0, 1.85, 0), 8, .8, .1, .8, 0, 200);
            }
        }.runTaskTimer(plugin, 0, 4);

        return new GateResponse(loc, itemDisplay, direction.clone().normalize().multiply(-1));
    }

    private record GateResponse(Location location, ItemDisplay itemDisplay, Vector gateDirection) {

    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.containsKey(beyonder) || random.nextInt(20) == 0;
    }
}
