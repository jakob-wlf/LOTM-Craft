package de.jakob.lotm.pathways.impl.moon.impl.potions;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireBreathPotion extends MysticalPotion{

    private final double multiplier;

    public FireBreathPotion(String name, String colorPrefix, Color color, double multiplier) {
        super(name, colorPrefix, color);
        this.multiplier = multiplier;
    }

    @Override
    public void onDrink(LivingEntity entity) {
        World world = entity.getWorld();

        world.playSound(entity.getEyeLocation(), Sound.ENTITY_WITCH_DRINK, 1, 1);
        world.playSound(entity.getEyeLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {

                if(counter >= 20 * 6) {
                    cancel();
                    return;
                }

                Vector direction = entity.getEyeLocation().getDirection().normalize();
                Location location = entity.getEyeLocation().add(direction.clone().multiply(2.5));

                if(counter % 20 == 0) {
                    world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                    for(Entity target : world.getNearbyEntities(location, 3, 3, 3)) {
                        if(!(target instanceof LivingEntity))
                            continue;
                        if(target == entity)
                            continue;

                        if(target.getType() == EntityType.ARMOR_STAND)
                            continue;

                        target.setFireTicks(20 * 3);

                        ((LivingEntity) target).damage(13 * multiplier);
                    }
                }

                ParticleUtil.drawLine(location, direction, Particle.FLAME, .5, world, null, 25, .4, 2);
                ParticleUtil.drawLine(location, direction, Particle.SMOKE, .5, world, null, 10, .4, 2);
                ParticleUtil.drawLine(location, direction, Particle.DUST, .5, world, dustOptions, 10, .4, 2);

                counter+=2;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 2);
    }
}
