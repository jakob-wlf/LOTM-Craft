package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.LocationProvider;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class SwordOfDawn extends Ability {

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 1f);

    private final ArrayList<Beyonder> currentlyCasting = new ArrayList<>();

    private final Set<Beyonder> onHitCooldown = new HashSet<>();

    public SwordOfDawn(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        hasHitAbility = true;
        showAbilityIcon = false;
    }

    public SwordOfDawn() {
        hasHitAbility = true;
        showAbilityIcon = false;
    }

    @Override
    public void leftClick(Beyonder beyonder) {
        useWeapon(beyonder);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        useWeapon(beyonder);
    }

    @Override
    public void onHit(Beyonder beyonder){
        useWeapon(beyonder);
    }

    public void useWeapon(Beyonder beyonder) {
        if(onHitCooldown.contains(beyonder) || !beyonder.removeSpirituality(10))
            return;

        final LivingEntity entity = beyonder.getEntity();
        final Location location = entity.getLocation().add(0, 1.35, 0).add(entity.getEyeLocation().getDirection().normalize().multiply(1.2));
        final World world = entity.getWorld();
        final Vector dir = entity.getEyeLocation().getDirection().normalize();


        ParticleUtil.spawnQuarterCircleArc(location, dir, Particle.DUST, dustOptions, 30);
        ParticleUtil.spawnQuarterCircleArc(location, dir, Particle.END_ROD, null, 2);

        world.playSound(location, Sound.ITEM_MACE_SMASH_AIR, .3f, .85f);

        for(LivingEntity target : getNearbyLivingEntities(entity, 2f, location, world)) {
            target.damage(12 * beyonder.getCurrentMultiplier());
            if(target instanceof Mob mob)
                mob.setTarget(entity);
            target.setVelocity(dir.clone().setY(.5).multiply(1.2));
        }

        onHitCooldown.add(beyonder);

        Bukkit.getScheduler().runTaskLater(plugin, () -> onHitCooldown.remove(beyonder), 10);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(currentlyCasting.contains(beyonder) || !beyonder.removeSpirituality(20))
            return;

        LivingEntity beyonderEntity = beyonder.getEntity();

        if(beyonderEntity instanceof Player player) {
            player.getInventory().remove(player.getInventory().getItemInMainHand());
        }

        LivingEntity entity = getTargetEntity(beyonderEntity, 20);

        Location loc = beyonderEntity.getLocation().add(beyonderEntity.getLocation().getDirection().normalize().multiply(2));

        beyonderEntity.getWorld().playSound(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        spawnFallingSword(loc);

        UUID locationUUID = UUID.randomUUID();

        LocationProvider.setLocation(locationUUID, loc);

        new BukkitRunnable() {

            @Override
            public void run() {
                BukkitTask tornadoTask = ParticleUtil.spawnParticleTornado(Particle.DUST, dustOptions, 9, .4, 4, 20 * 20, 80, .8, locationUUID, 0);
                BukkitTask tornadoTask2 = ParticleUtil.spawnParticleTornado(Particle.DUST, dustOptions, 9, .4, 4, 20 * 20, 80, .8, locationUUID, 5);

                currentlyCasting.add(beyonder);

                new BukkitRunnable() {

                    Location currentLoc = loc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
                    int counter = 0;

                    @Override
                    public void run() {

                        if(counter > 20 * 20) {
                            LocationProvider.removeLocation(locationUUID);
                            currentlyCasting.remove(beyonder);
                            cancel();
                            return;
                        }

                        if(entity != null) {
                            Vector dir = entity.getLocation().toVector().subtract(loc.clone().toVector()).normalize().multiply(.8);
                            loc.add(dir);
                        }
                        else {
                            Vector dir = currentLoc.toVector().subtract(loc.toVector()).normalize().multiply(.8);
                            loc.add(dir);

                            if(loc.distance(currentLoc) < 2)
                                currentLoc = loc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
                        }

                        beyonderEntity.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, .5f);
                        LocationProvider.setLocation(locationUUID, loc);

                        if(counter % 10 == 0) {
                            if(!beyonder.removeSpirituality(3)) {
                                cancel();
                                tornadoTask.cancel();
                                tornadoTask2.cancel();
                                currentlyCasting.remove(beyonder);
                                return;
                            }
                            damageNearbyEntities(12, beyonder.getCurrentMultiplier(), beyonderEntity, 2.5, loc, beyonderEntity.getWorld(), false, 0, 20);
                        }

                        counter+= 5;
                    }
                }.runTaskTimer(plugin, 10, 5);
            }
        }.runTaskLater(plugin, 40);
    }


    public void spawnFallingSword(Location location) {
        // Ensure world is not null
        World world = location.getWorld();
        if (world == null) return;

        // Create the sword ItemStack
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        List<String> tags = new ArrayList<>(component.getStrings());
        tags.add("twilight_giant_pathway");
        component.setStrings(tags);

        meta.setCustomModelDataComponent(component);
        sword.setItemMeta(meta);

        // Spawn the ItemDisplay 1 block above the location
        Location spawnLocation = location.clone().add(0, 2.5, 0);
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(sword);

        // -------------------------
        // 1) Define the 4x4 matrix
        //    (Matches your summon command's "transformation" array)
        // -------------------------
        Matrix4f matrix = new Matrix4f()
                // row-major layout:
                .m00(0.7071f).m01(0.7071f).m02(0.0f).m03(0.0f)
                .m10(0.7071f).m11(-0.7071f).m12(0.0f).m13(0.0f)
                .m20(0.0f).m21(0.0f).m22(-1.0f).m23(0.0f)
                .m30(0.0f).m31(0.0f).m32(0.0f).m33(1.0f);

        // -------------------------
        // 2) Decompose that matrix
        //    into translation, rotation, scale
        // -------------------------
        Vector3f translation = new Vector3f();
        Quaternionf rotation = new Quaternionf();
        Vector3f scale = new Vector3f();
        decomposeMatrix(matrix, translation, rotation, scale);

        // Multiply scale by 1.5, if you want the sword larger
        scale.mul(1.5f);

        // -------------------------
        // 3) Build the Transformation object
        //    and assign it to the ItemDisplay
        // -------------------------
        Transformation transformation = new Transformation(translation, rotation, scale, new Quaternionf());
        itemDisplay.setTransformation(transformation);

        // -------------------------
        // 4) Animate it falling over 10 ticks, then remove
        // -------------------------
        final int totalTicks = 40;
        final double startY = spawnLocation.getY();
        final double endY = location.getY();
        final double distance = startY - endY;

        new BukkitRunnable() {
            int tickCount = 0;

            @Override
            public void run() {
                if (!itemDisplay.isValid()) {
                    cancel();
                    return;
                }

                tickCount++;
                double progress = (double) tickCount / totalTicks;
                double newY = startY - distance * progress;

                Location currentLocation = itemDisplay.getLocation().clone();

                if(tickCount < totalTicks / 1.5f) {
                    currentLocation.setY(newY);
                    itemDisplay.teleport(currentLocation);
                }

                if(tickCount == Math.round(totalTicks / 1.5)) {
                    world.playSound(currentLocation, Sound.BLOCK_BELL_RESONATE, 1, 1);
                }

                if (tickCount >= totalTicks - 3) {
                    world.spawnParticle(Particle.DUST, currentLocation,  35, .5, 2, .5, dustOptions);
                    world.spawnParticle(Particle.END_ROD, currentLocation,  12, .5, 2, .5, 0);
                    itemDisplay.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Decomposes a 4x4 matrix into translation (outTrans),
     * rotation (outRot), and scale (outScale).
     * <p>
     * The resulting rotation is "pure" (no leftover scaling),
     * and outScale contains any uniform or non-uniform scale factors.
     */
    private static void decomposeMatrix(Matrix4f m,
                                        Vector3f outTrans,
                                        Quaternionf outRot,
                                        Vector3f outScale) {
        // 1) Translation = last column
        m.getTranslation(outTrans);

        // 2) Extract the upper-left 3×3 for rotation+scale
        Matrix3f mat3 = new Matrix3f();
        m.get3x3(mat3);

        // 3) Compute scale = length of each 3×3 column
        float scaleX = (float) mat3.getColumn(0, new Vector3f()).length();
        float scaleY = (float) mat3.getColumn(1, new Vector3f()).length();
        float scaleZ = (float) mat3.getColumn(2, new Vector3f()).length();

        outScale.set(scaleX, scaleY, scaleZ);

        // 4) Remove scale from mat3 to isolate pure rotation
        if (scaleX != 0) {
            mat3.m00(mat3.m00() / scaleX);
            mat3.m10(mat3.m10() / scaleX);
            mat3.m20(mat3.m20() / scaleX);
        }
        if (scaleY != 0) {
            mat3.m01(mat3.m01() / scaleY);
            mat3.m11(mat3.m11() / scaleY);
            mat3.m21(mat3.m21() / scaleY);
        }
        if (scaleZ != 0) {
            mat3.m02(mat3.m02() / scaleZ);
            mat3.m12(mat3.m12() / scaleZ);
            mat3.m22(mat3.m22() / scaleZ);
        }

        // 5) Convert the remaining pure rotation matrix into a quaternion
        outRot.setFromUnnormalized(mat3);
    }


}
