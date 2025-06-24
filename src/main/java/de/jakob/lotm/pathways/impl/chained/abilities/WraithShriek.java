package de.jakob.lotm.pathways.impl.chained.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class WraithShriek extends Ability {

    public WraithShriek(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 15;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        Location startLoc = beyonder.getEntity().getEyeLocation();
        World world = beyonder.getEntity().getWorld();

        Vector dir = getDirectionNormalized(beyonder.getEntity(), 25);

        world.playSound(startLoc, Sound.ENTITY_GHAST_WARN, 1, random.nextFloat(1.4f, 1.9f));

        new BukkitRunnable() {

            final double circlePoints = 100;
            double radius = .125;

            final double pitch = (startLoc.getPitch() + 90.0F) * 0.017453292F;
            final double yaw = -startLoc.getYaw() * 0.017453292F;

            final double increment = (2 * Math.PI) / circlePoints;
            double circlePointOffset = 0;

            int counter = 0;

            @Override
            public void run() {

                for (int i = 0; i < circlePoints; i++) {
                    double angle = i * increment + circlePointOffset;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Vector vec = new Vector(x, 0, z);
                    vec = VectorUtil.rotateAroundX(vec, pitch, false);
                    vec = VectorUtil.rotateAroundY(vec, yaw, false);
                    startLoc.add(vec);

                    ParticleSpawner.displayParticles(world, Particle.SMOKE, startLoc, 0, 0, 0, 0, 0, 128);
                    startLoc.subtract(vec);
                }
                circlePointOffset += increment / 3;
                if (circlePointOffset >= increment) {
                    circlePointOffset = 0;
                }
                startLoc.add(dir);
                radius += .08;

                counter++;

                if (counter >= 40 || damageNearbyEntities(23, beyonder.getCurrentMultiplier(), beyonder.getEntity(), radius * 1.3, startLoc, world, false, 0, 15)) {
                    addPotionEffectToNearbyEntities(beyonder.getEntity(), radius * 1.3, startLoc, world, new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 2, false, false, false), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, 2, false, false, false));
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 1);
    }
}
