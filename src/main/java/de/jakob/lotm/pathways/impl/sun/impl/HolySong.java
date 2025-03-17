package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class HolySong extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1f);

    public HolySong(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        if(!beyonder.removeSpirituality(20))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();

        if(entity instanceof Player player)
            player.playSound(player, Sound.MUSIC_DISC_PIGSTEP, 1, 1);

        runTaskWithDuration(5, 20 * 8, () -> {
            List<LivingEntity> allies = getNearbyLivingEntities(entity, 8, entity.getLocation(), entity.getWorld()).stream().filter(e -> EntityUtil.areOnTheSameTeam(e, entity)).toList();

            for(LivingEntity ally : allies) {
                ParticleSpawner.displayParticles(ally.getWorld(), Particle.DUST, ally.getEyeLocation().subtract(0, .35, 0), 8, .2, .75, .1, 0, dust, 200);
                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1, false, false, false));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1, false, false, false));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20, 1, false, false, false));
            }

            ParticleSpawner.displayParticles(entity.getWorld(), Particle.DUST, entity.getEyeLocation().subtract(0, .35, 0), 8, .2, .75, .1, 0, dust, 200);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1, false, false, false));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1, false, false, false));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20, 1, false, false, false));
            ParticleSpawner.displayParticles(entity.getWorld(), Particle.NOTE, entity.getEyeLocation(), 15, 6.5, 2, 6.5, 0, 200);
        }, () -> {
            casting.remove(beyonder);
            if(entity instanceof Player player)
                player.stopSound(Sound.MUSIC_DISC_PIGSTEP);
        });
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }
}
