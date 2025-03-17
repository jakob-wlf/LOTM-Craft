package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class ShadowShard extends Ability {

    public ShadowShard(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    //TODO: Add sounds

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Vector direction = getTargetLocation(entity, 8).toVector().subtract(entity.getEyeLocation().toVector()).normalize();
        Location loc = entity.getEyeLocation().add(direction);

        launchParticleProjectile(loc, direction, Particle.SMOKE, null, 3, 16, 1, 10, entity, 11, .3);
    }
}
