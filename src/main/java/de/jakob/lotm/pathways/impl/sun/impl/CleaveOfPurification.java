package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class CleaveOfPurification extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2f);

    public CleaveOfPurification(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(21))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().normalize().multiply(1.5));
        Vector direction = entity.getEyeLocation().getDirection().normalize();
        World world = entity.getWorld();

        location.getBlock().setType(Material.LIGHT);
        LOTM.getInstance().getBlocksToRemove().add(location);

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            LOTM.getInstance().getBlocksToRemove().remove(location);
            location.getBlock().setType(Material.AIR);
        }, 20);

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        damageNearbyEntities(13, beyonder.getCurrentMultiplier(), entity, 2, location, world, false, 0);

        runTaskWithDuration(3, 6, () -> {
            ParticleUtil.drawLine(location, direction, Particle.DUST, .5, world, dust, 32, .4, 2);
            ParticleUtil.drawLine(location, direction, Particle.END_ROD, .5, world, null, 8, .4, 2);
            ParticleUtil.drawLine(location, direction, Particle.FIREWORK, .5, world, null, 12, .4, 2);
        }, null);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 2;
    }
}
