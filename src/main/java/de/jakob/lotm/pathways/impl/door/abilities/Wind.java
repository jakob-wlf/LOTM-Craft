package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

@NoArgsConstructor
public class Wind extends Ability {

    private final HashMap<Beyonder, Location> castingLocations = new HashMap<>();

    public Wind(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(12))
            return;

        final Location location = beyonder.getEntity().getEyeLocation();
        final World world = beyonder.getEntity().getWorld();
        final Vector dir = location.getDirection().normalize();

        castingLocations.put(beyonder, location);

        new BukkitRunnable() {

            int timer = 0;

            @Override
            public void run() {
                timer += 2;

                if(castingLocations.get(beyonder) != location) {
                    cancel();
                    return;
                }

                if(timer > 20 * 10 || !castingLocations.containsKey(beyonder))  {
                    castingLocations.remove(beyonder);
                    cancel();
                    return;
                }

                for(int i = 0; i < 8; i++) {
                    Location tempLoc = location.clone().add(0, 1.5, 0).add(random.nextInt(10) - 5, random.nextInt(6) - 3, random.nextInt(10) - 5);
                    world.spawnParticle(Particle.CLOUD, tempLoc, 0, dir.getX(), dir.getY(), dir.getZ(), .5);
                }

                world.playSound(location, Sound.ENTITY_BREEZE_IDLE_GROUND, .2f, .3f);

                for(Entity entity : world.getNearbyEntities(location, 6, 6, 6)) {
                    if(entity instanceof LivingEntity && entity != beyonder.getEntity()) {
                        entity.setVelocity(dir.clone().multiply(.3));
                    }
                }

            }
        }.runTaskTimer(plugin, 0, 2);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return (beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 4);
    }
}
