package de.jakob.lotm.pathways.impl.sun.impl;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DivinePurification extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2.2f);


    public DivinePurification(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        cooldownTicks = 25;
        hasCooldown = true;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(450))
            return;

        LivingEntity entity = beyonder.getEntity();

        for(int j = 0; j < 21; j++) {

            Location targetLoc = entity.getEyeLocation().clone().add(random.nextDouble(-22, 22), 0, random.nextDouble(-22, 22));

            entity.getWorld().playSound(targetLoc, Sound.BLOCK_BEACON_ACTIVATE, 3, 1);
            entity.getWorld().playSound(targetLoc, Sound.ENTITY_BLAZE_SHOOT, 1, .5f);

            targetLoc.add(0, 13, 0);

            List<Block> lights = new ArrayList<>();

            runTaskWithDuration(0, 22, () -> {
                if(targetLoc.getBlock().getType().isSolid())
                    return;

                targetLoc.getBlock().setType(Material.LIGHT);
                LOTM.getInstance().getBlocksToRemove().add(targetLoc);
                lights.add(targetLoc.getBlock());

                ParticleSpawner.displayParticles(entity.getWorld(), Particle.END_ROD, targetLoc, 20, .75, .75, .75, 0, 200);

                ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 2.2, Particle.DUST, dust, 40);
                ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 2.2, Particle.FIREWORK, null, 30);

                ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 2.7, Particle.END_ROD, dust, 24);
                ParticleUtil.drawCircle(targetLoc, new Vector(0, 1, 0), 2.7, Particle.FLAME, null, 30);

                damageNearbyEntities(72, beyonder.getCurrentMultiplier(), entity, 9, targetLoc, entity.getWorld(), false, 0, 14);

                targetLoc.subtract(0, 1, 0);
            }, () -> lights.forEach(l -> {
                LOTM.getInstance().getBlocksToRemove().remove(l.getLocation());
                l.setType(Material.AIR);
            }));
        }

    }
}
