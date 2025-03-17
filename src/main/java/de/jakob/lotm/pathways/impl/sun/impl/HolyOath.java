package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;

@NoArgsConstructor
public class HolyOath extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1.75f);

    public HolyOath(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            return;
        }

        if(!beyonder.removeSpirituality(25))
            return;

        if(beyonder.getEntity() instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
        }

        runTaskWithDuration(2, 20 * 14, () -> {
            if(beyonder.getEntity() == null || !beyonder.getEntity().isValid()) {
                return;
            }

            beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 10, 2, false, false, false));
            beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 3, false, false, false));
            beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 3, false, false, false));

            ParticleSpawner.displayParticles(beyonder.getEntity().getWorld(), Particle.DUST, beyonder.getEntity().getLocation().add(0, .5, 0), 10, .2, .75, .2, 0, dust, 200);
            ParticleSpawner.displayParticles(beyonder.getEntity().getWorld(), Particle.END_ROD, beyonder.getEntity().getLocation().add(0, .5, 0), 5, .2, .75, .2, 0, 200);

            beyonder.addMultiplierModifier(1.15, 1);
        }, () -> casting.remove(beyonder));
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }
}
