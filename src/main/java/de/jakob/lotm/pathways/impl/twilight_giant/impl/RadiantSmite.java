package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class RadiantSmite extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 1.5f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(255, 222, 210), 1.5f);

    private final Particle.DustOptions dust3 = new Particle.DustOptions(Color.fromRGB(255, 176, 92), .8f);

    public RadiantSmite(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(24))
            return;

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        LivingEntity target = getTargetEntity(entity, 16);
        Location loc = target != null ? target.getEyeLocation() : getTargetLocation(entity, 16).add(0, 2.2, 0);

        Vector direction1 = new Vector(.1, 0, 1);
        Vector direction2 = new Vector(1, 0, 0);
        Vector direction3 = new Vector(0, 1, 0);

        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2, .8f);
        world.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 2, 1);
        world.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 2, 1);
        world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, .6f, .8f);
        world.playSound(loc, Sound.BLOCK_ANVIL_LAND, .3f, 1);

        ParticleSpawner.displayParticles(world, Particle.END_ROD, loc, 80, .2, .75, .2, .25, 200);

        Bukkit.getScheduler().runTaskLater(plugin, () -> damageNearbyEntities(18, beyonder.getCurrentMultiplier(), entity, 1, loc, world), 3);

        runTaskWithDuration(2, 30, () -> {
            ParticleUtil.drawCircle(loc.clone().add(0, 3.25, 0), direction1, .7, Particle.DUST, dust3, 40);
            ParticleUtil.drawCircle(loc.clone().add(0, 3.25, 0), direction2, .7, Particle.DUST, dust3, 40);
            ParticleUtil.drawCircle(loc.clone().add(0, 3.25, 0), direction3, .7, Particle.DUST, dust3, 40);

            ParticleUtil.drawCircle(loc.clone().subtract(0, 1.2, 0), direction3, 1, Particle.DUST, dust, 40);
            ParticleUtil.drawCircle(loc.clone().subtract(0, 1.2, 0), direction3, 1.5, Particle.DUST, dust, 40);

            ParticleSpawner.displayParticles(world, Particle.ENCHANT, loc, 45, .2, .6, .2, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, loc, 45, .2, .6, .2, 0, dust, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, loc, 30, .2, .6, .2, 0, dust2, 200);

            addPotionEffectToNearbyEntities(entity, 1, loc, world, new PotionEffect(PotionEffectType.SLOWNESS, 20, 10, false, false, false));

        }, null);
    }
}
