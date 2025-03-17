package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.pathways.DoorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

@NoArgsConstructor
public class Exile extends Ability {

    private final HashMap<Beyonder, ItemDisplay[]> currentlyCasting = new HashMap<>();

    private final DoorUtil.DoorType[] doorTypes = new DoorUtil.DoorType[] {
            DoorUtil.DoorType.CRIMSON,
            DoorUtil.DoorType.IRON,
            DoorUtil.DoorType.WOODEN,
            DoorUtil.DoorType.WARPED
    };

    public Exile(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(200))
            return;

        final LivingEntity target = getTargetEntity(beyonder.getEntity(), 20);

        final Location location = target == null ? getTargetBlock(beyonder.getEntity(), 20).getLocation() : target.getEyeLocation();

        World world = beyonder.getEntity().getWorld();

        final int doorAmount = 15;
        final int duration = 20 * 10;
        final ItemDisplay[] itemDisplays = new ItemDisplay[doorAmount];

        summonDoors(beyonder, world, itemDisplays, location);

        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                counter += 2;

                if (counter >= duration) {
                    currentlyCasting.remove(beyonder);
                }

                if(!currentlyCasting.containsKey(beyonder) || currentlyCasting.get(beyonder) != itemDisplays) {
                    for(ItemDisplay itemDisplay : itemDisplays) {
                        if(itemDisplay != null) {
                            itemDisplay.remove();
                            LOTM.getInstance().getEntitiesToRemove().remove(itemDisplay);
                        }
                    }
                    cancel();
                    return;
                }

                for(LivingEntity entity : getNearbyLivingEntities(beyonder.getEntity(), 6, location, world, EntityType.ARMOR_STAND)) {
                    if(entity.getScoreboardTags().contains("exiled"))
                        continue;

                    Beyonder beyonderEntity = LOTM.getInstance().getBeyonder(entity.getUniqueId());

                    if(beyonderEntity != null && beyonderEntity.getCurrentSequence() < beyonder.getCurrentSequence()) {
                        createExplosion(beyonder, world, itemDisplays, entity.getEyeLocation());
                        cancel();
                        return;
                    }

                    teleportEntity(world, entity, beyonder);

                    returnEntity(beyonderEntity, entity, beyonder, location);
                }
            }
        }.runTaskTimer(plugin, 15, 2);

    }

    private void summonDoors(Beyonder beyonder, World world, ItemDisplay[] itemDisplays, Location location) {
        currentlyCasting.put(beyonder, itemDisplays);
        world.playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, .1f);
        world.playSound(beyonder.getEntity(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, .1f);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;
                if(counter >= 15) {
                    cancel();
                    return;
                }

                final Location tempLoc = location.clone().add(random.nextDouble() * 8 - 4, random.nextDouble() * 5, random.nextDouble() * 8 - 4);
                final Vector tempDirection = location.clone().subtract(tempLoc).toVector().normalize();

                itemDisplays[counter] = DoorUtil.createDoor(
                        doorTypes[random.nextInt(doorTypes.length)],
                        tempLoc,
                        random.nextDouble(.75, 1.3),
                        tempDirection,
                        300,
                        true,
                        false
                );

                LOTM.getInstance().getEntitiesToRemove().add(itemDisplays[counter]);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void createExplosion(Beyonder beyonder, World world, ItemDisplay[] itemDisplays, Location location) {
        world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, .1f);
        world.playSound(beyonder.getEntity(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, .1f);
        for(ItemDisplay itemDisplay : itemDisplays) {
            if(itemDisplay != null && itemDisplay.isValid()) {
                itemDisplay.getWorld().spawnParticle(Particle.PORTAL, itemDisplay.getLocation(), 50, 0, 0, 0, 2);
                itemDisplay.getWorld().spawnParticle(Particle.PORTAL, itemDisplay.getLocation(), 20, 0, 0, 0, .1);
                itemDisplay.remove();
            }
        }
        currentlyCasting.remove(beyonder);
    }

    private void teleportEntity(World world, LivingEntity entity, Beyonder beyonder) {
        Location teleportLocation = createLocationInEnd(random.nextInt(1000) - 500, 195, random.nextInt(1000) - 500);
        if(teleportLocation == null) teleportLocation = new Location(world, random.nextInt(1000) - 500, 295, random.nextInt(1000) - 500);
        World teleportWorld = teleportLocation.getWorld();

        if(teleportWorld == null) return;

        int breakoutCounter = 40;
        while(teleportLocation.getBlock().getType().isSolid() && breakoutCounter > 0) {
            teleportLocation.add(0, 1, 0);
            breakoutCounter--;
        }

        teleportWorld.loadChunk(teleportLocation.getChunk());

        entity.addScoreboardTag("exiled");
        DoorUtil.displayDefaultTeleportParticles(world, entity.getEyeLocation());
        entity.setFallDistance(0);
        entity.teleport(teleportLocation);
        entity.setFallDistance(0);
        entity.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20 * 5, 1));

        world.playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .1f);
        world.playSound(beyonder.getEntity(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .1f);
        teleportWorld.playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .1f);

        entity.setVelocity(new Vector(.75, .5, 0));
        DoorUtil.displayDefaultTeleportParticles(teleportWorld, entity.getEyeLocation());
    }

    private void returnEntity(Beyonder beyonderEntity, LivingEntity entity, Beyonder beyonder, Location returnLocation) {
        if(beyonderEntity != null && beyonderEntity.getCurrentSequence() < 6 && beyonderEntity.getCurrentSequence() - beyonder.getCurrentSequence() <= 2) {

            int exileDuration = switch(beyonderEntity.getCurrentSequence() - beyonder.getCurrentSequence()) {
                case 0 -> 20 * 5;
                case 1 -> 20 * 20;
                default -> 20 * 40;
            };

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(entity.isValid()) {
                        DoorUtil.displayDefaultTeleportParticles(entity.getWorld(), entity.getEyeLocation());
                        entity.teleport(returnLocation);
                        DoorUtil.displayDefaultTeleportParticles(entity.getWorld(), entity.getEyeLocation());
                        entity.setVelocity(new Vector(.75, .5, 0));
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .1f);
                        entity.setVisibleByDefault(true);
                        entity.setInvisible(false);

                        Vector entityBackDirection = entity.getLocation().getDirection().multiply(-1).setY(0).normalize();
                        Location doorLocation = entity.getLocation().add(entityBackDirection.multiply(1.25));
                        DoorUtil.createDoor(
                                DoorUtil.DoorType.WARPED,
                                doorLocation,
                                1f,
                                entityBackDirection,
                                20 * 4,
                                true,
                                true
                        );

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            entity.removeScoreboardTag("exiled");
                        }, 20 * 20);
                    }
                }
            }.runTaskLater(plugin, exileDuration);
        }
    }

    private Location createLocationInEnd(double x, double y, double z) {
        // Attempt to get the End world
        World endWorld = Bukkit.getWorld("world_the_end");

        // If the world is not loaded, load it
        if (endWorld == null) {
            endWorld = Bukkit.createWorld(new WorldCreator("world_the_end"));
        }

        // If the world is successfully loaded, create a Location
        if (endWorld != null) {
            return new Location(endWorld, x, y, z);
        } else {
            return null;
        }
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        if(beyonder.getCurrentTarget() != null) {
            return !beyonder.getCurrentTarget().getScoreboardTags().contains("exiled");
        }
        return false;
    }
}
