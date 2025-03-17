package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class HolyLight extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1.7f);

    public HolyLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(25))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 12);

        entity.getWorld().playSound(targetLoc, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);

        targetLoc.add(0, 14, 0);

        List<Block> lights = new ArrayList<>();

        runTaskWithDuration(0, 18, () -> {
            if(!targetLoc.getBlock().getType().isSolid()) {
                targetLoc.getBlock().setType(Material.LIGHT);
                LOTM.getInstance().getBlocksToRemove().add(targetLoc);
                lights.add(targetLoc.getBlock());
            }

            ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 1.1, Particle.DUST, dust, 30);
            ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 1.1, Particle.FIREWORK, null, 14);

            damageNearbyEntities(14, beyonder.getCurrentMultiplier(), entity, 1, targetLoc, entity.getWorld(), false, 0, 14);

            targetLoc.subtract(0, 1, 0);
        }, () -> lights.forEach(l -> {
            LOTM.getInstance().getBlocksToRemove().remove(l.getLocation());
            l.setType(Material.AIR);
        }));
    }
}
