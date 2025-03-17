package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
public class Trap extends Ability {

    private final HashMap<Beyonder, List<Location>> trapLocations = new HashMap<>();
    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(195, 35, 8), 1.25f);

    public Trap(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(20))
            return;

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getLocation().add(0, .25, 0);

        world.playSound(location, Sound.BLOCK_TRIPWIRE_ATTACH, 1, .8f);
        if(!trapLocations.containsKey(beyonder))
            trapLocations.put(beyonder, new ArrayList<>());
        trapLocations.get(beyonder).add(location);
        new BukkitRunnable() {

            int counter = 0;
            final int detail = 40;
            final double radius = .75;

            @Override
            public void run() {
                if(counter >= 20 * 60) {
                    trapLocations.get(beyonder).remove(location);
                    cancel();
                    return;
                }

                if(entity instanceof Player player) {
                    double increment = (2 * Math.PI) / detail; // Higher detail -> smoother circle

                    for (int i = 0; i < detail; i++) {
                        double angle = i * increment;
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);
                        Location particleLocation = location.clone().add(x, 0, z);
                        player.spawnParticle(Particle.DUST, particleLocation, 2, .1, 0, .1, 0, dust);
                    }
                }

                if(damageNearbyEntities(8, beyonder.getCurrentMultiplier(), entity, 1.8, location, world)) {
                    world.createExplosion(location, 1.75f, beyonder.isGriefingEnabled(), beyonder.isGriefingEnabled());

                    trapLocations.get(beyonder).remove(location);
                    cancel();
                    return;
                }

                counter+=10;
            }
        }.runTaskTimer(plugin, 0, 10);;
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        if(beyonder.getCurrentSequence() > 7)
            return true;

        return random.nextInt(10) == 0;
    }
}
