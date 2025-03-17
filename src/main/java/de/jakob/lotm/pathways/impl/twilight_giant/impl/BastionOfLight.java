package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;

@NoArgsConstructor
public class BastionOfLight extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(237, 231, 213), 2.2f);
    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 2.2f);

    public BastionOfLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        if(!beyonder.removeSpirituality(55))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location loc = entity.getLocation();

        playSound(beyonder, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        playSound(beyonder, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!entity.isValid()) {
                    casting.remove(beyonder);
                }
                if(!casting.contains(beyonder)) {
                    cancel();
                    return;
                }

                ParticleSpawner.displayParticles(world, Particle.DUST, loc.clone().add(0, 2, 0), 60, .3, 6, .3, 0, dust, 300);
                ParticleSpawner.displayParticles(world, Particle.DUST, loc.clone().add(0, 2, 0), 60, .3, 6, .3, 0, dust2, 300);

                ParticleUtil.drawCircle(loc.clone().add(0, 1, 0), new Vector(0, 1, 0), 14, Particle.DUST, dust, 80);
                ParticleUtil.drawCircle(loc.clone().add(0, 1, 0), new Vector(0, 1, 0), 7, Particle.DUST, dust, 80);

                if(entity.getLocation().distance(loc) < 15) {
                    beyonder.addMultiplierModifier(1.2, 10);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 3, false, false, false));
                }

            }
        }.runTaskTimer(plugin, 0, 6);
    }
}
