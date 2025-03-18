package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class WallOfLight extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2.3f);

    public WallOfLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeUSedByNonPlayer = false;
    }

    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(220))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = getLocationLookedAt(entity, 10, true);

        final HashSet<Location> allBlocks = new HashSet<>();

        Vector perp = VectorUtil.rotateAroundY(entity.getEyeLocation().getDirection().setY(0), 90);

        runTaskWithDuration(7, 20 * 20, () -> {
            List<Location> wallBlocks = BlockUtil.getBlocksInLine(loc, perp, 48, 28, false).stream().filter(block -> block.getType() == Material.AIR || block.getType() == Material.BARRIER).map(Block::getLocation).toList();

            wallBlocks.forEach(b -> {
                b.getBlock().setType(Material.BARRIER);
                allBlocks.add(b);

                if(random.nextInt(2) != 0 && b.getWorld() != null)
                    ParticleSpawner.displayParticles(b.getWorld(), Particle.DUST, b, 1, .3, .3, .3, 0, dust, 200);
                else if(b.getWorld() != null) {
                    ParticleSpawner.displayParticles(b.getWorld(), Particle.END_ROD, b, 1, .3, .3, .3, 0, 200);
                }
                LOTM.getInstance().getBlocksToRemove().add(b);
            });

        }, () -> {
            allBlocks.forEach(b -> {
                b.getBlock().setType(Material.AIR);
                LOTM.getInstance().getBlocksToRemove().remove(b);
            });
        });
    }
}
