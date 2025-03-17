package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

@NoArgsConstructor
public class ElectricShock extends Ability {
    public ElectricShock(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(10))
            return;

        final LivingEntity target = getTargetEntity(beyonder.getEntity(), 12);

        final Location location = target == null ? getTargetBlock(beyonder.getEntity(), 12).getLocation() : target.getEyeLocation();
        final Location start = beyonder.getEntity().getEyeLocation().add(beyonder.getEntity().getEyeLocation().getDirection().multiply(.75));

        ParticleUtil.drawLine(start, location, Particle.ELECTRIC_SPARK, .3, beyonder.getEntity().getWorld(), null, 1, 0.05);

        beyonder.getEntity().getWorld().getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
            if(entity == beyonder.getEntity())
                return;

            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(5 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            }

            entity.setVelocity(beyonder.getEntity().getLocation().getDirection().normalize().multiply(1.2));
        });

        beyonder.getEntity().getWorld().playSound(location, Sound.BLOCK_COPPER_GRATE_HIT, 1, 1);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getCurrentSequence() >= 5;
    }
}
