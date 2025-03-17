package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

@NoArgsConstructor
public class Freezing extends Ability {
    public Freezing(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(12))
            return;

        final Location location = beyonder.getEntity().getEyeLocation().add(beyonder.getEntity().getEyeLocation().getDirection().multiply(1.5));

        beyonder.getEntity().getWorld().spawnParticle(Particle.SNOWFLAKE, location, 10, 0.1, 0.1, 0.1, .1);
        beyonder.getEntity().getWorld().spawnParticle(Particle.ITEM_SNOWBALL, location, 10, 0.1, 0.1, 0.1, 0.1);

        beyonder.getEntity().getWorld().getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
            if(entity == beyonder.getEntity())
                return;

            entity.setFreezeTicks(20 * 5);
            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(8 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            }
        });

        beyonder.getEntity().getWorld().playSound(location, Sound.BLOCK_SNOW_BREAK, 1, 1);

        if (!location.getBlock().getType().isSolid() && location.getBlock().getRelative(0, -1, 0).getType().isSolid() && beyonder.isGriefingEnabled() && random.nextBoolean()) {
            location.getBlock().setType(Material.SNOW);
        }
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 4;
    }

}
