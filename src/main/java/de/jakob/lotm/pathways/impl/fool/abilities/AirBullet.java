package de.jakob.lotm.pathways.impl.fool.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

@NoArgsConstructor
public class AirBullet extends Ability {

    private final double[] radiusPerSequence = new double[] {
            2, 2, 1.75, 1.5, 1, .45, .3, .15, .15, .15,
    };

    private final HashMap<Beyonder, Integer> selectedSequence = new HashMap<>();

    public AirBullet(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
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

        world.playSound(startLoc, Sound.ENTITY_SNOWBALL_THROW, 5, 1);

        new BukkitRunnable() {

            final double circlePoints = 20 + 10 * ((8 - beyonder.getCurrentSequence()));
            double radius = getRadius(beyonder);

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

                    ParticleSpawner.displayParticles(world, Particle.EFFECT, startLoc, 0, 0, 0, 0, 0, 128);
                    startLoc.subtract(vec);
                }
                circlePointOffset += increment / 3;
                if (circlePointOffset >= increment) {
                    circlePointOffset = 0;
                }
                startLoc.add(dir);

                if(damageNearbyEntities(14, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 1, startLoc, world, false, 0, 15)) {
                    cancel();
                    return;
                }

                if (startLoc.getBlock().getType().isSolid()) {
                    if (beyonder.isGriefingEnabled()) {
                        int sequence = selectedSequence.getOrDefault(beyonder, beyonder.getCurrentSequence());
                        world.createExplosion(startLoc, (int) (Math.round(Math.pow(8 - sequence, 1.25))));
                    }

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

    private double getRadius(Beyonder beyonder) {
        if(beyonder.getCurrentSequence() >= 4)
            return radiusPerSequence[beyonder.getCurrentSequence()];

        if(selectedSequence.containsKey(beyonder)) {
            int sequence = selectedSequence.get(beyonder);
            if(sequence < 0 || sequence >= radiusPerSequence.length)
                return radiusPerSequence[beyonder.getCurrentSequence()];
            return radiusPerSequence[sequence];
        } else {
            return radiusPerSequence[beyonder.getCurrentSequence()];
        }
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(beyonder.getCurrentSequence() >= 4)
            return;

        if(!selectedSequence.containsKey(beyonder)) {
            selectedSequence.put(beyonder, beyonder.getCurrentSequence());
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(pathway.getColorPrefix() + "Selected Sequence: §f" + selectedSequence.get(beyonder)));
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(beyonder.getCurrentSequence() >= 4)
            return;

        if(!selectedSequence.containsKey(beyonder)) {
            selectedSequence.put(beyonder, beyonder.getCurrentSequence());
        }

        int currentSequence = selectedSequence.get(beyonder);
        currentSequence--;
        if(currentSequence < beyonder.getCurrentSequence())
            currentSequence = 7;

        selectedSequence.replace(beyonder, currentSequence);
    }
}
