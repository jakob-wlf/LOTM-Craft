package de.jakob.lotm.pathways.impl.hermit.abilities.scrolls;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public abstract class MysticalScroll implements Listener {

    protected String name;
    protected String colorPrefix;
    @Getter
    protected Color color;

    protected final Particle.DustOptions dustOptions;
    private final int id = (new Random()).nextInt(999999);

    protected final Random random;


    public MysticalScroll(String name, String colorPrefix, Color color) {
        this.name = name;
        this.colorPrefix = colorPrefix;
        this.color = color;

        dustOptions = new Particle.DustOptions(color, 2);
        random = new Random();

        LOTM.getInstance().registerListener(this);
    }

    protected void launchParticleProjectile(Location loc, Vector direction, Particle particle, Particle.DustOptions dustOptions, double maxDistance, double damage, double multiplier, double speed, LivingEntity damager, int particleAmount, int fireticks, double size, double randomness, Particle... additionalParticles) {
        Location startLoc = loc.clone();

        if(startLoc.getWorld() == null)
            return;

        direction.normalize().multiply((1/20f) * speed);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                ParticleSpawner.displayParticles(startLoc.getWorld(), particle, startLoc, particleAmount, (size / 3f), (size / 3f), (size / 3f), 0, dustOptions, 200);
                for(Particle p : additionalParticles) {
                    ParticleSpawner.displayParticles(startLoc.getWorld(), p, startLoc, particleAmount, (size / 3f), (size / 3f), (size / 3f), 0, dustOptions, 200);
                }

                if(((counter / 20f) * speed) > maxDistance) {
                    cancel();
                    return;
                }

                damageNearbyEntities(damage, multiplier, damager, size * 1.4f, startLoc, startLoc.getWorld(), fireticks != 0, fireticks, 10, true);

                startLoc.add(direction);
                if(randomness > 0)
                    direction.add(new Vector(random.nextDouble(2 * randomness) - randomness, random.nextDouble(2 * randomness) - randomness, random.nextDouble(2 * randomness) - randomness));
                counter ++;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    protected LivingEntity getTargetEntity(LivingEntity entity, double radius, boolean hasToHaveLineOfSight, double deviation, boolean allowSameTeam, EntityType... exclude) {
        Beyonder beyonder = LOTM.getInstance().getBeyonder(entity.getUniqueId());
        if(beyonder == null)
            return null;

        if(!(beyonder instanceof BeyonderPlayer) && !hasToHaveLineOfSight) {
            if(beyonder.getCurrentTarget() != null && beyonder.getCurrentTarget().getType() != EntityType.ALLAY && beyonder.getCurrentTarget().getLocation().distance(entity.getLocation()) <= radius && EntityUtil.mayDamage(beyonder.getCurrentTarget(), entity)[1])
                return beyonder.getCurrentTarget();
        }

        Location eyeLocation = entity.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        for (int i = 1; i <= radius; i++) {
            Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(i));

            if (currentLocation.getBlock().getType().isSolid()) {
                return null;
            }


            if(currentLocation.getWorld() == null)
                return null;
            for (Entity nearbyEntity : currentLocation.getWorld().getNearbyEntities(currentLocation, deviation, deviation, deviation)) {
                if (
                        nearbyEntity instanceof LivingEntity && (EntityUtil.mayDamage(nearbyEntity, entity)[1] || allowSameTeam) &&
                                nearbyEntity.getType() != EntityType.ARMOR_STAND &&
                                (!nearbyEntity.getScoreboardTags().contains("spirit") || entity.getScoreboardTags().stream().anyMatch(tag -> tag.startsWith("see_spirits"))) &&
                                !nearbyEntity.equals(entity) && Stream.of(exclude).noneMatch(e-> e == nearbyEntity.getType())
                ) {
                    return (LivingEntity) nearbyEntity;
                }
            }
        }

        return null;
    }

    protected boolean addPotionEffectToNearbyEntities(LivingEntity caster, double radius, Location location, World world, PotionEffect... potionEffects) {
        boolean entityFound = false;

        if(world != location.getWorld())
            return false;

        for(Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if(!(entity instanceof LivingEntity))
                continue;
            if(entity == caster)
                continue;

            if(entity.getType() == EntityType.ARMOR_STAND || !EntityUtil.mayDamage(entity, caster)[0])
                continue;

            ((LivingEntity) entity).addPotionEffects(List.of(potionEffects));

            if(entity instanceof Mob mob)
                mob.setTarget(caster);

            entityFound = true;
        }

        return entityFound;
    }

    protected List<LivingEntity> getNearbyLivingEntities(@Nullable LivingEntity exclude, double radius, @NotNull Location location, @NotNull World world, EntityType... excludedEntityTypes) {
        List<LivingEntity> entities = new ArrayList<>();

        if(location.getWorld() != world)
            world = location.getWorld();

        if(world == null)
            return entities;

        for(Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity livingEntity) || entity.getType() == EntityType.ARMOR_STAND)
                continue;
            if (entity == exclude)
                continue;

            if (Stream.of(excludedEntityTypes).anyMatch(e -> e == livingEntity.getType()))
                continue;

            entities.add(livingEntity);
        }

        return entities;
    }

    protected Location getLocationLookedAt(LivingEntity entity, double radius, boolean oneBlockBefore) {
        Location eyeLocation = entity.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        for (double i = 0; i <= radius; i+=.25) {
            Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(i));
            Block block = currentLocation.getBlock();

            if (block.getType().isSolid()) {
                return oneBlockBefore ? currentLocation.clone().subtract(direction.clone()) : currentLocation;
            }
        }
        return eyeLocation.add(direction.multiply(radius));
    }

    protected Location getTargetLocation(LivingEntity entity, double radius) {
        LivingEntity target = getTargetEntity(entity, radius, false, 1, false);
        return target != null ? target.getEyeLocation() : getLocationLookedAt(entity, radius, true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!ItemsUtil.isSimilar(event.getItem(), getItem())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Remove only one instance of the item
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && ItemsUtil.isSimilar(contents[i], item)) {
                player.getInventory().setItem(i, null);
                break; // Exit loop after removing one instance
            }
        }

        LOTM.getInstance().removeListener(this);
        onUse(player);
    }

    public abstract void onUse(LivingEntity entity);

    public ItemStack getItem() {
        ItemStack scroll = new ItemStack(Material.PAPER);
        ItemMeta meta = scroll.getItemMeta();
        if(meta == null) return scroll;

        meta.setDisplayName(colorPrefix + "§l" + name);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(id);
        scroll.setItemMeta(meta);
        return scroll;
    }

    protected boolean damageNearbyEntities(
            double damage,
            double multiplier,
            @Nullable LivingEntity caster,
            double radius,
            @NotNull Location location,
            @NotNull World world,
            boolean fire,
            int fireticks,
            int cooldownTicks,
            boolean ignoreCooldown,
            LivingEntity... exclude
    ) {
        boolean damaged = false;
        if(location.getWorld() != world)
            world = location.getWorld();

        if(world == null)
            return false;

        if (Double.isNaN(location.getX()) || Double.isInfinite(location.getX()) ||
                Double.isNaN(location.getY()) || Double.isInfinite(location.getY()) ||
                Double.isNaN(location.getZ()) || Double.isInfinite(location.getZ())) {
            return false;
        }

        outerloop: for(Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if(!(entity instanceof LivingEntity))
                continue;
            if(entity == caster)
                continue;

            if(List.of(exclude).contains(entity) || entity.getType() == EntityType.ARMOR_STAND || (entity.getScoreboardTags().contains("spirit") && caster != null && caster.getScoreboardTags().stream().noneMatch(tag -> tag.startsWith("see_spirits"))))
                continue;

            if(caster != null && !EntityUtil.mayDamage(entity, caster)[0])
                continue;

            if(fire)
                entity.setFireTicks(fireticks);


            if(((LivingEntity) entity).getNoDamageTicks() == 0 || ignoreCooldown) {
                ((LivingEntity) entity).damage(damage * multiplier, caster);
                ((LivingEntity) entity).setNoDamageTicks(cooldownTicks);

                damaged = true;
            }
        }

        return damaged;
    }

    protected void runTaskWithDuration(int period, double duration, Runnable task, Runnable onFinish) {
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= duration) {
                    if (onFinish != null) {
                        onFinish.run();
                    }
                    cancel();
                    return;
                }

                if (task != null) {
                    task.run();
                }

                counter += (period <= 0) ? 1 : period;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, Math.max(1, period));
    }

}
