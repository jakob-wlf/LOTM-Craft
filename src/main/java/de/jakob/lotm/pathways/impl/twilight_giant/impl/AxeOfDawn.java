package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class AxeOfDawn extends Ability {

    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 2f);
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 1f);

    private final Set<Beyonder> onHitCooldown = new HashSet<>();

    public AxeOfDawn(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        hasHitAbility = true;
        showAbilityIcon = false;
    }

    public AxeOfDawn() {
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
        if (!beyonder.removeSpirituality(10))
            return;

        final LivingEntity entity = beyonder.getEntity();
        final Location location = entity.getLocation().add(0, .25, 0);
        final World world = entity.getWorld();

        world.spawnParticle(Particle.DUST, location, 45, 1, 0, 1, 0, dustOptions2);
        world.spawnParticle(Particle.END_ROD, location, 8, 1, 0, 1, 0);


        world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, .4f, .1f);
        for (LivingEntity target : getNearbyLivingEntities(entity, 2f, location, world)) {
            target.damage(14 * beyonder.getCurrentMultiplier());
            if (target instanceof Mob mob)
                mob.setTarget(entity);
            target.setVelocity(new Vector(0, .7, 0));
        }
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 2.5;
    }

}
