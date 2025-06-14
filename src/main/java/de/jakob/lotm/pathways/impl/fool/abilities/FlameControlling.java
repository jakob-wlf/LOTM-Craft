package de.jakob.lotm.pathways.impl.fool.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class FlameControlling extends Ability {

    public FlameControlling(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        spirituality = 20;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        World world = beyonder.getEntity().getWorld();

        Vector dir = getDirectionNormalized(beyonder.getEntity(), 25);
        Location startLoc = beyonder.getEntity().getEyeLocation().subtract(0, .1, 0).add(dir);

        world.playSound(startLoc, Sound.ENTITY_BLAZE_SHOOT, 5, 1);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                ParticleSpawner.displayParticles(Particle.FLAME, startLoc, 35, .15, .15, .15, 0, 200);
                ParticleSpawner.displayParticles(Particle.SMOKE, startLoc, 16, .15, .15, .15, 0, 200);
                startLoc.add(dir);

                if(damageNearbyEntities(14, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 1, startLoc, world, true, 20 * 3)) {
                    cancel();
                    return;
                }

                if (startLoc.getBlock().getType().isSolid()) {
                    if (beyonder.isGriefingEnabled())
                        startLoc.clone().subtract(dir).getBlock().setType(Material.FIRE);

                    cancel();
                    return;
                }

                if(counter >= 100) {
                    cancel();
                    return;
                }

                counter++;
            }

        }.runTaskTimer(plugin, 0, 1);
    }
}
