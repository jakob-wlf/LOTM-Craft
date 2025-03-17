package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.LocationProvider;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class Tornado extends Ability {

    private final HashMap<Beyonder, Integer> tornadoCount = new HashMap<>();

    public Tornado(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(tornadoCount.containsKey(beyonder) && tornadoCount.get(beyonder) >= 3)
            return;

        if(!tornadoCount.containsKey(beyonder))
            tornadoCount.put(beyonder, 1);
        else
            tornadoCount.replace(beyonder, tornadoCount.get(beyonder) + 1);

        LivingEntity entity = beyonder.getEntity();
        LivingEntity targetEntity = getTargetEntity(entity, 25);

        Location loc = getTargetLocation(entity, 30);
        World world = entity.getWorld();

        UUID locationUUID = UUID.randomUUID();

        LocationProvider.setLocation(locationUUID, loc);

        BukkitTask tornadoTask = ParticleUtil.spawnParticleTornado(Particle.CLOUD, null, 13.5, .2, 5.5, 20 * 20, 60, 0, locationUUID, 0);
        BukkitTask tornadoTask2 = ParticleUtil.spawnParticleTornado(Particle.CLOUD, null, 13.5, .2, 5.5, 20 * 20, 60, 0, locationUUID, 5);

        new BukkitRunnable() {

            Location currentLoc = loc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
            int counter = 0;

            @Override
            public void run() {

                if(counter > 20 * 20) {
                    LocationProvider.removeLocation(locationUUID);
                    tornadoCount.replace(beyonder, tornadoCount.get(beyonder) - 1);
                    cancel();
                    return;
                }

                if(targetEntity != null) {
                    Vector dir = targetEntity.getLocation().toVector().subtract(loc.clone().toVector()).normalize().multiply(.8);
                    loc.add(dir);
                }
                else {
                    Vector dir = currentLoc.toVector().subtract(loc.toVector()).normalize().multiply(.8);
                    loc.add(dir);

                    if(loc.distance(currentLoc) < 2)
                        currentLoc = loc.clone().add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
                }

                world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, .5f);
                LocationProvider.setLocation(locationUUID, loc);

                if(counter % 10 == 0) {
                    if(!beyonder.removeSpirituality(3)) {
                        cancel();
                        tornadoTask.cancel();
                        tornadoTask2.cancel();
                        tornadoCount.replace(beyonder, tornadoCount.get(beyonder) - 1);
                        return;
                    }
                    damageNearbyEntities(20, beyonder.getCurrentMultiplier(), entity, 2.5, loc, world, false, 0, 20);
                }

                counter+= 5;
            }
        }.runTaskTimer(plugin, 10, 5);
    }
}
