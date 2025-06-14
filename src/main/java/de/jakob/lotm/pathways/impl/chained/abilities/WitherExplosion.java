package de.jakob.lotm.pathways.impl.chained.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;

@NoArgsConstructor
public class WitherExplosion extends Ability {

    public WitherExplosion(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20 * 2;
        spirituality = 35;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 5);

        World world = entity.getWorld();
        world.playSound(targetLoc, Sound.ENTITY_WITHER_SHOOT, .2f, .2f);
        world.playSound(targetLoc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
        world.playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        ParticleSpawner.displayParticles(Particle.LARGE_SMOKE, targetLoc, 500, 1, 1, 1, 0.2, 180);
        ParticleSpawner.displayParticles(Particle.EXPLOSION, targetLoc, 15, .25, .25, .25, 0, 180);
        ParticleSpawner.displayParticles(Particle.SOUL, targetLoc, 100, 1, 1, 1, 0.1, 180);
    }
}
