package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class ToxicSmoke extends Ability {

    Set<Beyonder> casting = new HashSet<>();
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(35, 168, 102), 2f);

    public ToxicSmoke(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder) || !beyonder.removeSpirituality(22))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().normalize().multiply(1.5));
        World world = entity.getWorld();

        //TODO: Add sound
        casting.add(beyonder);
        runTaskWithDuration(6, 20 * 5, () -> {
            damageNearbyEntities(5, beyonder.getCurrentMultiplier(), entity, 4, location, world);
            ParticleSpawner.displayParticles(world, Particle.LARGE_SMOKE, location, 120, 3, 3, 3, 0, 160);
            ParticleSpawner.displayParticles(world, Particle.DUST, location, 120, 3, 3, 3, 0, dustOptions, 160);
            addPotionEffectToNearbyEntities(entity, 4, location, world, PotionEffectType.POISON.createEffect(30, 1));
            addPotionEffectToNearbyEntities(entity, 4, location, world, PotionEffectType.BLINDNESS.createEffect(10, 0));
        }, () -> casting.remove(beyonder));

    }
}
