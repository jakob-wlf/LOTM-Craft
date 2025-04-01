package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class SpaceCollapse extends Ability implements Listener {

    private static final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(74, 24, 125), 2f);
    private static final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(10, 10, 10), 1.5f);
    private static final BlockData blockData = Bukkit.createBlockData(Material.BLACK_CONCRETE);

    private final HashSet<FallingBlock> blocksToRemove = new HashSet<>();

    public SpaceCollapse(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        LOTM.getInstance().registerListener(this);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if (!beyonder.removeSpirituality(400))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 16).add(0, 1, 0);
        World world = loc.getWorld();

        if(world == null)
            return;

        List<Location> riftLocations = new ArrayList<>();

        Location lastLoc = loc.clone().add(0, 18, 0);
        riftLocations.add(lastLoc.clone());

        for(int i = 0; i < 45; i++) {
            lastLoc.add(random.nextDouble(-.6, .6), -.5, random.nextDouble(-.6, .6));
            riftLocations.add(lastLoc.clone());
        }

        Set<Location> breakLocations = new HashSet<>();
        riftLocations.forEach(l -> {
            if(random.nextInt(3) != 0)
                return;

            Vector out = new Vector(random.nextDouble(-.5, .5), random.nextDouble(-.01, .02), random.nextDouble(-.5, .5)).normalize().multiply(.3);
            Location breakLoc = l.clone();
            for(int i = 0; i < 40; i++) {
                breakLocations.add(breakLoc.clone());
                breakLoc.add(out).add(random.nextDouble(-.25, .25), random.nextDouble(-.25, .25), random.nextDouble(-.25, .25));
            }
        });

        Location middleLoc = riftLocations.get(riftLocations.size() / 2);

        Set<Block> blocks = BlockUtil.getSphereBlocks(loc, 16);

        runTaskWithDuration(4, 20 * 3, () -> {
           for(Location riftLocation : riftLocations) {
               ParticleSpawner.displayParticles(world, Particle.DUST, riftLocation, 10, .175, .175, .175, 0, dust, 200);
               ParticleSpawner.displayParticles(world, Particle.END_ROD, riftLocation, 15, .06, .06, .06, 0, 200);
               damageNearbyEntities(1, beyonder.getCurrentMultiplier(), entity, .65, riftLocation, world, false, 0, 0, true, true);
            }
            blocks.stream().filter(b -> random.nextInt(450) == 0 && !b.getRelative(0, 1, 0).getType().isSolid()).forEach(b -> {
                FallingBlock fallingBlock = world.spawnFallingBlock(b.getLocation().add(0, 2, 0), b.getBlockData());
                Vector direction = middleLoc.toVector().subtract(fallingBlock.getLocation().toVector()).normalize().multiply(.65);
                fallingBlock.setDropItem(false);
                blocksToRemove.add(fallingBlock);

                if(beyonder.isGriefingEnabled())
                    b.setType(Material.AIR);

                runTaskWithDuration(1, 20 * 3, () -> {
                    fallingBlock.setVelocity(direction);
                    if(fallingBlock.getLocation().distance(middleLoc) < 1.2) {
                        fallingBlock.remove();
                    }
                }, fallingBlock::remove);
            });
            getNearbyLivingEntities(entity, 15, middleLoc, world).forEach(e -> {
                e.setVelocity(middleLoc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(.75));
            });
        }, () -> {
            blocksToRemove.forEach(FallingBlock::remove);
            if(beyonder.isGriefingEnabled()) {
                riftLocations.forEach(b -> {
                    BlockUtil.getSphereBlocks(b, 9).forEach(block -> block.setType(Material.AIR));
                });
            }
            ParticleSpawner.displayParticles(Particle.BLOCK, middleLoc, 1000, 4, 9.5, 4, 0, blockData, 200);
            runTaskWithDuration(2, 20 * 15, () -> {
                for (Location riftLocation : riftLocations) {
                    ParticleSpawner.displayParticles(world, Particle.DUST, riftLocation, 10, .175, .175, .175, 0, dust, 200);
                    ParticleSpawner.displayParticles(world, Particle.END_ROD, riftLocation, 15, .06, .06, .06, 0, 200);
                }

                for (Location breakLocation : breakLocations) {
                    ParticleSpawner.displayParticles(world, Particle.DUST, breakLocation, 2, 0, 0, 0, 0, dust2, 200);
                }

                damageNearbyEntities(59, beyonder.getCurrentMultiplier(), entity, 6, middleLoc, world, false, 0, 10, false, true);
            }, null);
        });

    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        if(event.getEntity() instanceof FallingBlock fallingBlock && blocksToRemove.contains(fallingBlock)) {
            event.setCancelled(true);
            fallingBlock.remove();
        }
    }
}
