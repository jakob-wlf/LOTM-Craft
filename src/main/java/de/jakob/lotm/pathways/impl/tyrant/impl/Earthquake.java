package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class Earthquake extends Ability {
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(139, 69, 19), 3.5F);
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(95, 140, 19), 3.5F);
    private final Set<Beyonder> currentlyCasting = new HashSet<>();

    public Earthquake(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    //TODO: Rework design
    @Override
    public void useAbility(Beyonder beyonder) {
        if(currentlyCasting.contains(beyonder))
            return;

        Location startLoc = beyonder.getEntity().getLocation();
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        currentlyCasting.add(beyonder);

        List<Block> tempBlocks = new ArrayList<>();
        for(int i = -4; i < 3; i++) {
            tempBlocks.addAll(BlockUtil.getBlocksInCircleRadius(startLoc.getBlock().getRelative(0, i, 0), 28, true, Material.LAVA, Material.WATER).stream().filter(block-> random.nextInt(150) == 0).toList());
        }

        final List<Block> blocks = tempBlocks.stream().filter(block -> !block.getRelative(0, 1, 0).getType().isSolid()).toList();

        runTaskWithDuration(10, 20 * 20, () -> {
            blocks.forEach(
                    block -> {
                        damageNearbyEntities(6, beyonder.getCurrentMultiplier(), entity, 1, block.getLocation().add(0, 1, 0), world);
                        ParticleSpawner.displayParticles(world, Particle.BLOCK, block.getLocation().add(0, 1, 0), 1, .5, .5, .5, 0.2, Material.DIRT.createBlockData(), 150);
                        ParticleSpawner.displayParticles(world, Particle.DUST, block.getLocation().add(0, 1, 0), 1, .5, .5, .5, 0.2, dustOptions, 150);
                        ParticleSpawner.displayParticles(world, Particle.DUST, block.getLocation().add(0, 1, 0), 1, .5, .5, .5, 0.2, dustOptions2, 150);
                        if(random.nextInt(8) == 0) {
                            ParticleSpawner.displayParticles(world, Particle.EXPLOSION, block.getLocation(), 1, 1, 1, 1, 0.2, 150);
                            world.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, .5f, 1);
                        }
                    }
            );

            if(beyonder.isGriefingEnabled()) {
                List<Block> currentBlocks = BlockUtil.getBlocksInCircleRadius(startLoc.getBlock(), 28, true, Material.LAVA, Material.WATER).stream().filter(block -> random.nextInt(150) == 0).toList();

                for(Block block : currentBlocks) {
                    BlockData blockData = block.getBlockData();
                    block.setType(Material.AIR);

                    FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation().add(.5, 0, .5), blockData);
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(0, 1, 0));

                    if(block.getType().getHardness() == 0.5 || block.getType().getHardness() == 1.5 || block.getType().getHardness() == 0.6) {
                        if(random.nextBoolean())
                            block.setType(Material.LAVA);
                    }
                }
            }

        }, () -> currentlyCasting.remove(beyonder));
    }
}
