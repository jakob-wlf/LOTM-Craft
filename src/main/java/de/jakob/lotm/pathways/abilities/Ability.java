package de.jakob.lotm.pathways.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.MathUtil;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
public abstract class Ability {

    protected Pathway pathway;
    protected int sequence;
    protected AbilityType abilityType;
    protected String name;
    protected Material material;
    protected String description;
    protected String id;

    protected boolean canBeCopied = true;
    protected boolean canBeUSedByNonPlayer = true;
    @Getter
    protected boolean hasHitAbility = false;
    protected boolean showAbilityIcon = true;

    protected final Random random = new Random();
    protected final LOTM plugin = LOTM.getInstance();

    protected String customModelData;

    protected boolean hasCooldown = false;
    protected int cooldownTicks = 20;

    protected final HashSet<Beyonder> onCooldown = new HashSet<>();

    public Ability(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        this.pathway = pathway;
        this.sequence = sequence;
        this.abilityType = abilityType;
        this.name = name;
        this.material = material;
        this.description = description;
        this.id = id;
    }

    public Ability(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id, boolean canBeCopied, boolean canBeUSedByNonPlayer, boolean showAbilityIcon) {
        this.pathway = pathway;
        this.sequence = sequence;
        this.abilityType = abilityType;
        this.name = name;
        this.material = material;
        this.description = description;
        this.id = id;
        this.canBeCopied = canBeCopied;
        this.canBeUSedByNonPlayer = canBeUSedByNonPlayer;
        this.showAbilityIcon = showAbilityIcon;
    }

    public Ability() {

    }

    public void takeOverValues(Ability ability) {
        this.pathway = ability.pathway;
        this.sequence = ability.sequence;
        this.abilityType = ability.abilityType;
        this.name = ability.name;
        this.material = ability.material;
        this.description = ability.description;
        this.id = ability.id;
        this.canBeCopied = ability.canBeCopied;
        this.canBeUSedByNonPlayer = ability.canBeUSedByNonPlayer;
        this.showAbilityIcon = ability.showAbilityIcon;
    }

    public void rightClick(Beyonder beyonder) {
        prepareAbility(beyonder);
    }

    public void leftClick(Beyonder beyonder) {
        prepareAbility(beyonder);
    }

    public void useAbility(Beyonder beyonder) {

    }

    public void sneakToggle(Beyonder beyonder) {

    }

    public void sneakRightClick(Beyonder beyonder) {
        prepareAbility(beyonder);
    }

    public void sneakLeftClick(Beyonder beyonder) {
        prepareAbility(beyonder);
    }

    public void onHit(Beyonder beyonder) {

    }

    public void onHold(Beyonder beyonder, Player player) {

    }

    public void onSwitchOutItem(Beyonder beyonder, Player player) {

    }

    protected void playSound(Beyonder beyonder, Sound sound, float volume, float pitch) {
        playSound(beyonder, sound, volume, pitch, true);
    }

    protected void playSound(Beyonder beyonder, Sound sound, float volume, float pitch, boolean playForAll) {
        Entity entity = beyonder.getEntity();

        if(!playForAll) {
            if(entity instanceof Player player)
                player.playSound(entity, sound, volume, pitch);
            return;
        }

        if(entity == null)
            return;

        entity.getWorld().playSound(entity.getLocation(), sound, volume, pitch);
    }

    public void restrictMovement(LivingEntity entity, Location location, double radius) {
        if(location.getWorld() == null)
            return;

        getNearbyLivingEntities(entity, radius, location, location.getWorld()).forEach(t -> t.setVelocity(new Vector(0, 0, 0)));
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);

        if(!(this instanceof PassiveAbility))
            item.addUnsafeEnchantment(Enchantment.MULTISHOT, 10);

        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        String displayName = this instanceof PassiveAbility ? pathway.getColorPrefix() + name + " §7(Passive)" : pathway.getColorPrefix() + name;
        meta.setDisplayName(displayName);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        List<String> tags = new ArrayList<>(component.getStrings());
        tags.add(Objects.requireNonNullElseGet(customModelData, () -> pathway.getName() + "_pathway"));
        component.setStrings(tags);

        meta.setCustomModelDataComponent(component);
        if(pathway != null) {
            meta.setLore(List.of(
                    "§7Pathway: " + pathway.getColorPrefix() + pathway.getDisplayName(),
                    "§7Sequence: " + pathway.getColorPrefix() + sequence
            ));
        }

        item.setItemMeta(meta);

        return item;
    }

    protected boolean damageNearbyEntities(
            double damage,
            double multiplier,
            @Nullable LivingEntity caster,
            double radius,
            @NotNull Location location,
            @NotNull World world
    ) {
        return damageNearbyEntities(damage, multiplier, caster, radius, location, world, false, 0);
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
            LivingEntity... exclude
    ) {
        return damageNearbyEntities(damage, multiplier, caster, radius, location, world, fire, fireticks, 10, false, exclude);
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
            LivingEntity... exclude
    ) {
        return damageNearbyEntities(damage, multiplier, caster, radius, location, world, fire, fireticks, cooldownTicks, false, exclude);
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
        return damageNearbyEntities(damage, multiplier, caster, radius, location, world, fire,fireticks, cooldownTicks, ignoreCooldown, false, exclude);
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
            boolean throughBlocks,
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

        outerloop: for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity))
                continue;
            if (entity == caster)
                continue;

            if (List.of(exclude).contains(entity) || entity.getType() == EntityType.ARMOR_STAND || (entity.getScoreboardTags().contains("spirit") && caster != null && caster.getScoreboardTags().stream().noneMatch(tag -> tag.startsWith("see_spirits"))))
                continue;

            if (caster != null && !EntityUtil.mayDamage(entity, caster)[0])
                continue;

            if (fire)
                entity.setFireTicks(fireticks);

            if(!throughBlocks) {
                Vector rayDir = location.clone().toVector().subtract(((LivingEntity) entity).getEyeLocation().clone().toVector()).normalize().multiply(.75);
                Location rayStart = ((LivingEntity) entity).getEyeLocation().clone().add(0, -.5, 0);

                int breakoutCounter = 100;
                while(breakoutCounter > 0 && rayStart.distance(location) > 1) {
                    if(rayStart.getBlock().getType().isSolid())
                        continue outerloop;

                    rayStart.add(rayDir);

                    breakoutCounter--;
                }
            }

            if (((LivingEntity) entity).getNoDamageTicks() == 0 || ignoreCooldown) {
                ((LivingEntity) entity).damage(damage * multiplier, caster);
                ((LivingEntity) entity).setNoDamageTicks(cooldownTicks);

                damaged = true;
            }
        }

        return damaged;
    }

    protected boolean damageInDirection(
            double damage,
            double multiplier,
            @Nullable LivingEntity caster,
            double range,          // maximum distance along the direction vector
            double offset,         // allowed deviation from the line
            @NotNull Location location,
            @NotNull World world,
            boolean fire,
            int fireticks,
            int cooldownTicks,
            boolean ignoreCooldown,
            @NotNull Vector direction,
            LivingEntity... exclude
    ) {
        boolean damaged = false;
        if(location.getWorld() != world)
            world = location.getWorld();

        if(world == null)
            return false;

        // Check for valid starting location
        if (Double.isNaN(location.getX()) || Double.isInfinite(location.getX()) ||
                Double.isNaN(location.getY()) || Double.isInfinite(location.getY()) ||
                Double.isNaN(location.getZ()) || Double.isInfinite(location.getZ())) {
            return false;
        }

        // Normalize the direction vector once for further calculations.
        Vector normDir = direction.clone().normalize();

        // Get nearby entities within a box that definitely covers our range and offset.
        // We use range for the forward distance and offset for lateral deviation.
        double searchRadius = Math.max(range, offset);
        for(Entity entity : world.getNearbyEntities(location, searchRadius, searchRadius, searchRadius)) {
            if(!(entity instanceof LivingEntity))
                continue;
            if(entity == caster)
                continue;
            if(List.of(exclude).contains(entity) ||
                    entity.getType() == EntityType.ARMOR_STAND ||
                    (entity.getScoreboardTags().contains("spirit") && caster != null &&
                            caster.getScoreboardTags().stream().noneMatch(tag -> tag.startsWith("see_spirits"))))
                continue;
            if(caster != null && !EntityUtil.mayDamage(entity, caster)[0])
                continue;

            // Calculate the vector from starting location to the entity's location.
            // Using entity.getLocation() for simplicity; adjust if you need a more precise position.
            Vector toEntity = entity.getLocation().toVector().subtract(location.toVector());

            // Projection of toEntity onto our normalized direction vector
            double dot = toEntity.dot(normDir);
            // Only consider entities in front of the starting location and within the specified range.
            if(dot < 0 || dot > range)
                continue;

            // Calculate the perpendicular distance from the entity to the line.
            Vector projection = normDir.clone().multiply(dot);
            double perpendicularDistance = toEntity.clone().subtract(projection).length();
            if(perpendicularDistance > offset)
                continue;

            // Apply fire effect if enabled.
            if(fire)
                entity.setFireTicks(fireticks);

            // Damage the entity if it is ready to be damaged.
            LivingEntity living = (LivingEntity) entity;
            if(living.getNoDamageTicks() == 0 || ignoreCooldown) {
                living.damage(damage * multiplier, caster);
                living.setNoDamageTicks(cooldownTicks);
                damaged = true;
            }
        }
        return damaged;
    }


    protected void launchParticleProjectile(Location loc, Vector direction, Particle particle, Particle.DustOptions dustOptions, double maxDistance, double damage, double multiplier, double speed, LivingEntity damager, int particleAmount, double size) {
        launchParticleProjectile(loc, direction, particle, dustOptions, maxDistance, damage, multiplier, speed, damager, particleAmount, 0, size);
    }

    protected void launchParticleProjectile(Location loc, Vector direction, Particle particle, Particle.DustOptions dustOptions, double maxDistance, double damage, double multiplier, double speed, LivingEntity damager, int particleAmount, int fireticks, double size) {
        launchParticleProjectile(loc, direction, particle, dustOptions, maxDistance, damage, multiplier, speed, damager, particleAmount, fireticks, size, 0);
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

                damageNearbyEntities(damage, multiplier, damager, size * 1.4f, startLoc, startLoc.getWorld(), fireticks != 0, fireticks);

                startLoc.add(direction);
                if(randomness > 0)
                    direction.add(new Vector(random.nextDouble(2 * randomness) - randomness, random.nextDouble(2 * randomness) - randomness, random.nextDouble(2 * randomness) - randomness));
                counter ++;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    protected Vector getDirectionNormalized(LivingEntity entity, int maxMobDetectionDistance) {
        Entity target = getTargetEntity(entity, maxMobDetectionDistance);

        Location targetLoc = target != null ? target.getLocation() : getTargetBlock(entity, 15).getLocation();

        return targetLoc.toVector().subtract(entity.getLocation().toVector()).normalize();
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
        }.runTaskTimer(plugin, 0, Math.max(1, period));
    }

    protected List<LivingEntity> getNearbyLivingEntities(@Nullable LivingEntity exclude, double radius, @NotNull Location location, @NotNull World world) {
        return getNearbyLivingEntities(exclude, radius, location, world, new EntityType[0]);
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

    protected LivingEntity getTargetEntity(LivingEntity entity, double radius, EntityType... exclude) {
        return getTargetEntity(entity, radius, false, 1.5, exclude);
    }

    protected LivingEntity getTargetEntity(LivingEntity entity, double radius, double deviation, EntityType... exclude) {
        return getTargetEntity(entity, radius, false, deviation, exclude);
    }

    //TODO: Exclude spectator and creative players
    protected LivingEntity getTargetEntity(LivingEntity entity, double radius, boolean hasToHaveLineOfSight, double deviation, EntityType... exclude) {
        return getTargetEntity(entity, radius, hasToHaveLineOfSight, deviation, false, exclude);
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

    protected Block getTargetBlock(LivingEntity entity, double radius) {
        return getTargetBlock(entity, radius, false);
    }

    protected Location getTargetLocation(LivingEntity entity, double radius) {
        LivingEntity target = getTargetEntity(entity, radius);
        return target != null ? target.getEyeLocation() : getLocationLookedAt(entity, radius, true);
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


    protected Block getTargetBlock(LivingEntity entity, double radius, boolean oneBlockBefore) {
        Location eyeLocation = entity.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        for (double i = 0; i <= radius; i+=.25) {
            Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(i));
            Block block = currentLocation.getBlock();

            if (block.getType().isSolid()) {
                return oneBlockBefore ? currentLocation.clone().subtract(direction.clone()).getBlock() : block;
            }
        }
        return eyeLocation.add(direction.multiply(radius)).getBlock();
    }

    public Ability copy(AbilityType abilityType) {
        try {
            Ability ability = this.getClass().getDeclaredConstructor().newInstance();
            ability.takeOverValues(this);
            ability.takeOverValues(this);
            ability.setAbilityType(abilityType);
            ability.setCanBeCopied(false);
            return ability;
        } catch (Exception e) {
            return null;
        }
    }

    protected List<LivingEntity> getNearbyEntitiesInCubeWithCenter(@Nullable LivingEntity exclude, Location center, int radius, World world) {
        // Define the bounding box
        BoundingBox box = new BoundingBox(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius
        );

        // Get nearby entities and filter them
        return world.getNearbyEntities(box).stream()
                .filter(entity -> entity instanceof LivingEntity) // Ensure it's a LivingEntity
                .map(entity -> (LivingEntity) entity) // Cast to LivingEntity
                .filter(entity -> entity != exclude) // Exclude the specified entity if any
                .toList();
    }

    public void prepareAbility(Beyonder beyonder) {
        if(onCooldown.contains(beyonder) && hasCooldown)
            return;

        if(hasCooldown) {
            onCooldown.add(beyonder);
            Bukkit.getScheduler().runTaskLater(plugin, () -> onCooldown.remove(beyonder), cooldownTicks);
        }

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getLocation();

        Entity noAbilityMarker = world.getNearbyEntities(location, 30, 20, 30).stream().filter(e -> e.getType() == EntityType.MARKER).filter(e -> {
            if(e.getScoreboardTags().stream().noneMatch(s -> s.equalsIgnoreCase("no_abilities")))
                return false;

            String radiusString = e.getScoreboardTags().stream().filter(s -> s.startsWith("radius_")).map(s -> s.replace("radius_", "")).findFirst().orElse(null);

            if(radiusString == null || !MathUtil.isInteger(radiusString))
                return false;

            String sequenceString = e.getScoreboardTags().stream().filter(s -> s.startsWith("sequence_")).map(s -> s.replace("sequence_", "")).findFirst().orElse(null);
            if(sequenceString == null || !MathUtil.isInteger(sequenceString))
                return false;

            int radius = Integer.parseInt(radiusString);
            int sequence = Integer.parseInt(sequenceString);

            if(e.getLocation().distance(location) > radius)
                return false;

            if(sequence > beyonder.getCurrentSequence())
                return false;

            String uuid = e.getScoreboardTags().stream().filter(s -> s.startsWith("exclude_")).map(s -> s.replace("exclude_", "")).findFirst().orElse(null);
            if(uuid == null || beyonder.getUuid().toString().equalsIgnoreCase(uuid))
                return false;

            return true;

        }).findFirst().orElse(null);

        if(noAbilityMarker != null)
            return;

        if(canBeCopied) {
            Marker marker = (Marker) world.spawnEntity(location, EntityType.MARKER);
            marker.setInvulnerable(true);
            marker.getScoreboardTags().add("ability_cast_" + entity.getUniqueId());
            LOTM.getInstance().getAbilitiesBeingUsed().put(marker.getUniqueId(), this);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                LOTM.getInstance().getAbilitiesBeingUsed().remove(marker.getUniqueId());
                marker.remove();
            }, 20 * 3);
        }

        beyonder.digestPotion(sequence);

        if(abilityType == AbilityType.RECORDED) {
            if(entity instanceof Player player)
                player.getInventory().remove(getItem());

            beyonder.removeAbility(this);
        }

        useAbility(beyonder);
    }

    public String getDescription() {
        return pathway != null ? pathway.getColorPrefix() + name + " §7- " + description : description;
    }

    public boolean shouldUseAbility(Beyonder beyonder) {
        if(hasCooldown) {
            return !onCooldown.contains(beyonder);
        }
        return true;
    }

    public boolean canBeCopied() {
        return canBeCopied;
    }

    public boolean canBeUSedByNonPlayer() {
        return canBeUSedByNonPlayer;
    }

    public boolean showAbilityIcon() {
        return showAbilityIcon;
    }

    public void tick(Beyonder beyonder) {

    }

    public void setAbilityType(AbilityType abilityType) {
        this.abilityType = abilityType;
    }

    public void setCanBeCopied(boolean canBeCopied) {
        this.canBeCopied = canBeCopied;
    }

    public boolean hasHitAbility() {
        return hasHitAbility;
    }
}
