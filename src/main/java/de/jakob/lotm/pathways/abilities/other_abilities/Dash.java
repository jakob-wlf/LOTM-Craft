package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class Dash extends Ability {
    public Dash(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location target = getTargetLocation(entity, 20);
        Vector direction = target.toVector().subtract(entity.getLocation().toVector()).normalize();

        entity.setVelocity(direction.multiply(2.5));
        runTaskWithDuration(0, 28, () -> damageNearbyEntities(8, beyonder.getCurrentMultiplier(), entity, 1.1, entity.getLocation(), entity.getWorld()), null);
    }
}
