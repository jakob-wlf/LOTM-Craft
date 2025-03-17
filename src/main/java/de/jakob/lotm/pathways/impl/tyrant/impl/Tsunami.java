package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;

@NoArgsConstructor
public class Tsunami extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(30, 120, 255), 2.75f);
    final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(10, 70, 120), 2.75f);
    final Particle.DustOptions whiteDust = new Particle.DustOptions(Color.fromRGB(240, 240, 255), 2.75f);

    private final double[] tsunamiOffsets = new double[] {
        0, .1, .1, .1, .2, .4, 1.2, 1.9, 3.1, 4.5, 5.6
    };

    public Tsunami(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        castAbility(beyonder, false);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        castAbility(beyonder, true);
    }

    @Override
    public void sneakRightClick(Beyonder beyonder) {
        castAbility(beyonder, true);
    }

    private void castAbility(Beyonder beyonder, boolean sealing) {
        if(casting.contains(beyonder)) {
            return;
        }

        int spiritualityRemoved = sealing ? 220 : 165;
        if(!beyonder.removeSpirituality(spiritualityRemoved))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location targetLoc = getTargetLocation(entity, 35);
        Location startLoc = entity.getLocation().add(entity.getLocation().getDirection().setY(0).normalize().multiply(2));
        Vector direction = targetLoc.toVector().subtract(startLoc.toVector()).setY(0).normalize();
        Vector sideVector = VectorUtil.rotateAroundY(direction.clone(), 90).normalize();

        world.playSound(startLoc, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.6f, 1f));
        world.playSound(startLoc, Sound.ENTITY_DROWNED_SWIM, 1, random.nextFloat(.6f, .8f));

        runTaskWithDuration(2, 20 * 5, () -> {

            for(int i = -1; i < 10; i++) {
                for(int j = - 8; j < 9; j++) {
                    double currentOffset = tsunamiOffsets[i + 1];

                    Location loc = startLoc.clone().add(sideVector.clone().multiply(j)).add(0, i, 0).add(direction.clone().multiply(currentOffset));
                    ParticleSpawner.displayParticles(world, Particle.DUST, loc, 1, .2, .2, .2, 0, dust, 250);
                    ParticleSpawner.displayParticles(world, Particle.DUST, loc, 1, .2, .2, .2, 0, dust2, 250);
                    if(i >= 6)
                        ParticleSpawner.displayParticles(world, Particle.DUST, loc, 2, .2, .2, .2, 0, whiteDust, 250);

                    damageNearbyEntities(30, beyonder.getCurrentMultiplier(), entity, 1.3, loc, world);
                    for(LivingEntity target : getNearbyLivingEntities(entity, 1.3, loc, world)) {
                        if(!sealing)
                            target.getVelocity().add(direction.clone().setY(.5).normalize().multiply(1.4));
                        else {
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 8, 12, false, false, false));
                        }
                    }
                }
            }

            startLoc.add(direction.clone());

        }, () -> casting.remove(beyonder));
    }
}
