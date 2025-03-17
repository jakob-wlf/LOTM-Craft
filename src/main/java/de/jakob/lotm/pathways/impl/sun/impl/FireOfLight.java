package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;

@NoArgsConstructor
public class FireOfLight extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2f);

    public FireOfLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(17))
            return;

        final Location location = getTargetLocation(beyonder.getEntity(), 10);

        location.getBlock().setType(Material.LIGHT);
        LOTM.getInstance().getBlocksToRemove().add(location);

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            LOTM.getInstance().getBlocksToRemove().remove(location);
            location.getBlock().setType(Material.AIR);
        }, 20);

        beyonder.getEntity().getWorld().spawnParticle(Particle.FLAME, location, 100, 0.1, 0.1, 0.1, 0.075);
        beyonder.getEntity().getWorld().spawnParticle(Particle.DUST, location, 85, .6, .6, .6, 0.1, dust);
        beyonder.getEntity().getWorld().spawnParticle(Particle.END_ROD, location, 25, 0.1, 0.1, 0.1, 0.075);
        beyonder.getEntity().getWorld().spawnParticle(Particle.SMOKE, location, 5, 0.1, 0.1, 0.1, 0.1);

        beyonder.getEntity().getWorld().getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
            if(entity == beyonder.getEntity())
                return;

            entity.setFireTicks(20 * 5);
            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(14 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            }
        });

        beyonder.getEntity().getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, .7f, .7f);

        if (!location.getBlock().getType().isSolid() && !location.getBlock().getRelative(0, -1, 0).getType().isSolid() && beyonder.isGriefingEnabled() && random.nextBoolean()) {
            location.getBlock().getRelative(0, -1, 0).setType(Material.FIRE);
        }
    }
}
