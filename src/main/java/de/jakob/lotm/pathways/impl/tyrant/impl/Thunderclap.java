package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

@NoArgsConstructor
public class Thunderclap extends Ability {

    public Thunderclap(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(110))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getEyeLocation();
        World world = entity.getWorld();

        List<LivingEntity> affectedEntities = getNearbyLivingEntities(entity, 30, location, world);

        for(LivingEntity target : affectedEntities) {
            Vector direction = target.getLocation().toVector().subtract(location.toVector()).setY(.75).normalize();
            target.setVelocity(direction.multiply(5));
            target.damage(30 * beyonder.getCurrentMultiplier(), entity);
            if(target instanceof Player player)
                player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 6, 1);
        }

        if(entity instanceof Player player)
            player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 6, 1);

        ParticleSpawner.displayParticles(world, Particle.CLOUD, location, 700, .8, .8, .8, 0.35, 128);
    }
}
