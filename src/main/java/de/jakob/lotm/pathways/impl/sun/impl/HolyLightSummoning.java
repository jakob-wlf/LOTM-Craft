package de.jakob.lotm.pathways.impl.sun.impl;

import com.google.common.util.concurrent.AtomicDouble;
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
public class HolyLightSummoning extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1.75f);


    public HolyLightSummoning(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(26))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 12);

        entity.getWorld().playSound(targetLoc, Sound.BLOCK_BEACON_ACTIVATE, 3, 1);
        entity.getWorld().playSound(targetLoc, Sound.ENTITY_BLAZE_SHOOT, 1, .5f);

        targetLoc.add(0, 13, 0);

        List<Block> lights = new ArrayList<>();

        runTaskWithDuration(0, 22, () -> {
            if(targetLoc.getBlock().getType().isSolid()) {
                if(!targetLoc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {

                    Location loc = targetLoc.clone().add(0, 1, 0);
                    AtomicDouble i = new AtomicDouble(2);

                    runTaskWithDuration(1, 8, () -> {
                        ParticleUtil.drawCircle(loc, new Vector(0, 1, 0), i.get(), Particle.DUST, dust, 18);
                        ParticleUtil.drawCircle(loc, new Vector(0, 1, 0), i.get(), Particle.FLAME, dust, 25);
                        ParticleUtil.drawCircle(loc, new Vector(0, 1, 0), i.get(), Particle.FIREWORK, null, 22);

                        i.addAndGet(.5);
                    }, null);
                }
               return;
            }

            targetLoc.getBlock().setType(Material.LIGHT);
            LOTM.getInstance().getBlocksToRemove().add(targetLoc);
            lights.add(targetLoc.getBlock());

            ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 1.2, Particle.DUST, dust, 35);
            ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 1.2, Particle.FIREWORK, null, 22);

            ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 1.5, Particle.END_ROD, dust, 19);
            ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 1.5, Particle.FLAME, null, 22);

            damageNearbyEntities(18.5, beyonder.getCurrentMultiplier(), entity, 1, targetLoc, entity.getWorld(), false, 0, 14);

            targetLoc.subtract(0, 1, 0);
        }, () -> lights.forEach(l -> {
            LOTM.getInstance().getBlocksToRemove().remove(l.getLocation());
            l.setType(Material.AIR);
        }));

    }
}
