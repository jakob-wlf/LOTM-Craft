package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;

@NoArgsConstructor
public class Roar extends Ability {

    public Roar(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        hasCooldown = true;
        cooldownTicks = 15;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        Location startLoc = beyonder.getEntity().getEyeLocation();
        World world = beyonder.getEntity().getWorld();

        Vector dir = getDirectionNormalized(beyonder.getEntity(), 25);

        world.playSound(startLoc, Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
        world.playSound(startLoc, Sound.ENTITY_RAVAGER_ROAR, 5, 1);

        new BukkitRunnable() {

            final double circlePoints = 100;
            double radius = .1;

            final double pitch = (startLoc.getPitch() + 90.0F) * 0.017453292F;
            final double yaw = -startLoc.getYaw() * 0.017453292F;

            final double increment = (2 * Math.PI) / circlePoints;
            double circlePointOffset = 0;

            int counter = 0;

            @Override
            public void run() {

                //Particle effects
                //Calls rotateAroundAxis() functions from VectorUtils class
                for (int i = 0; i < circlePoints; i++) {
                    double angle = i * increment + circlePointOffset;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Vector vec = new Vector(x, 0, z);
                    vec = VectorUtil.rotateAroundX(vec, pitch, false);
                    vec = VectorUtil.rotateAroundY(vec, yaw, false);
                    startLoc.add(vec);

                    ParticleSpawner.displayParticles(world, Particle.EFFECT, startLoc, 0, 0, 0, 0, 0, 128);
                    startLoc.subtract(vec);
                }
                circlePointOffset += increment / 3;
                if (circlePointOffset >= increment) {
                    circlePointOffset = 0;
                }
                startLoc.add(dir);
                radius += .075;

                damageNearbyEntities(25, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 5, startLoc, world, false, 0, 15);


                counter++;

                if (startLoc.getBlock().getType().isSolid() || counter >= 100) {
                    if (beyonder.isGriefingEnabled())
                        world.createExplosion(startLoc, (int) (radius * 1.75f));

                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 1);
    }
}
