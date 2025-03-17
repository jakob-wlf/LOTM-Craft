package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class ScytheOfDawn extends Ability {
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 2f);
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 1f);

    private final Set<Beyonder> onHitCooldown = new HashSet<>();

    public ScytheOfDawn(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        hasHitAbility = true;
        showAbilityIcon = false;
    }

    public ScytheOfDawn() {
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

    public void useAbility(Beyonder beyonder) {

        final LivingEntity entity = beyonder.getEntity();
        final Location location = entity.getEyeLocation().add(0, .25, 0);
        final World world = entity.getWorld();

        Location targetLoc = getTargetLocation(entity, 20);
        final Vector direction = targetLoc.toVector().subtract(location.toVector()).normalize().multiply(.75);
        final Vector right = VectorUtil.rotateAroundY(direction, 90).normalize();

        world.playSound(location, Sound.ENTITY_WITHER_SHOOT, .3f, .4f);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 4) {
                    cancel();
                    return;
                }

                ParticleUtil.drawLine(location.clone().add(right.clone().multiply(1.8)), location.clone().add(right.clone().multiply(-1.8)), Particle.DUST, .2, world, dustOptions, 1, 0);

                for(LivingEntity target : getNearbyLivingEntities(entity, 2f, location, world)) {
                    target.damage(7 * beyonder.getCurrentMultiplier());
                    if(target instanceof Mob mob)
                        mob.setTarget(entity);
                    target.setVelocity(direction.clone().multiply(.8));
                }

                location.add(direction);

                counter += 2;
            }

        }.runTaskTimer(plugin, 0, 2);
    }
}
