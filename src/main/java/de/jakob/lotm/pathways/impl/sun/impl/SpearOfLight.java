package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class SpearOfLight extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), .7f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2.5f);

    public SpearOfLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(375))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 40);
        Location location = entity.getEyeLocation().clone().add(entity.getEyeLocation().getDirection().normalize().clone().multiply(.5)).add(VectorUtil.rotateAroundY(entity.getEyeLocation().getDirection().normalize(), 90).multiply(2.5)).add(0, 1, 0);
        Vector direction = targetLoc.clone().toVector().subtract(location.toVector()).normalize().multiply(1.8);

        runTaskWithDuration(2, 8, () -> ParticleUtil.createSpear(location, direction, 6, Particle.END_ROD, null), null);

        if(location.getWorld() == null)
            return;

        new BukkitRunnable() {

            int counter = 0;


            @Override
            public void run() {
                if(damageNearbyEntities(65, beyonder.getCurrentMultiplier(), entity, 2.5, location, location.getWorld())) {
                    runTaskWithDuration(2, 20 * 15, () -> {
                        addPotionEffectToNearbyEntities(entity, 3, location, location.getWorld(), new PotionEffect(PotionEffectType.SLOWNESS, 20, 10, false, false, false));
                        ParticleUtil.createParticleSphere(location, 3, 9, Particle.END_ROD);
                    }, null);

                    cancel();
                    return;
                }
                if(counter > 20 * 8 || location.getBlock().getType().isSolid()) {
                    damageNearbyEntities(39.5, beyonder.getCurrentMultiplier(), entity, 4, location, location.getWorld());
                    cancel();
                    return;
                }

                ParticleUtil.createSpear(location, direction, 6, Particle.END_ROD, null);

                location.add(direction);
                counter++;
            }
        }.runTaskTimer(plugin, 8, 1);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) > 15;
    }
}
