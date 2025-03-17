package de.jakob.lotm.pathways.impl.moon.impl.potions;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SunWater extends MysticalPotion{

    private double multiplier;

    public SunWater(String name, String colorPrefix, Color color, double multiplier) {
        super(name, colorPrefix, color);

        this.multiplier = multiplier;
    }

    @Override
    public void onDrink(LivingEntity entity) {
        World world = entity.getWorld();
        Location startLoc = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().normalize());
        Vector direction = entity.getEyeLocation().getDirection().normalize();

        world.playSound(startLoc, Sound.ENTITY_WITCH_THROW, 1, 1);
        world.playSound(startLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

        new BukkitRunnable() {

            double x = 0;

            @Override
            public void run() {
                if(x >= 30) {
                    cancel();
                    return;
                }

                Location newLoc = startLoc.clone().add(direction.clone().multiply(x)).subtract(0, Math.pow(x, 2) / 10, 0);
                ParticleSpawner.displayParticles(world, Particle.DUST, newLoc, 10, 0, 0, 0, 0, dustOptions, 100);

                if(newLoc.getBlock().getType().isSolid()) {
                    newLoc.add(0, .8, 0);
                    ParticleSpawner.displayParticles(world, Particle.END_ROD, newLoc, 60, 1.8, .25, 1.8, 0.15, 100);
                    ParticleSpawner.displayParticles(world, Particle.DUST, newLoc, 60, 1.8, .7, 1.8, 0.15, dustOptions, 100);
                    world.playSound(newLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                    world.playSound(newLoc, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);

                    for(Entity target : world.getNearbyEntities(newLoc, 2, 2, 2)) {
                        if(!(target instanceof LivingEntity))
                            continue;
                        if(target == entity)
                            continue;

                        if(target.getType() == EntityType.ARMOR_STAND)
                            continue;

                        target.setFireTicks(20 * 2);

                        ((LivingEntity) target).damage(17.5 * multiplier);
                        ((LivingEntity) target).setNoDamageTicks(0);
                    }

                    cancel();
                    return;
                }

                x+=.5;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }
}
