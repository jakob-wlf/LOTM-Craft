package de.jakob.lotm.util.lotm;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.EntityUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class UnderworldUtil{

    public static Location underworldLocation = null;
    public static int underworldSize = 120;

    public void createUnderworld(double x, double y, double z) {
        if (underworldLocation != null) {
            throw new RuntimeException("Underworld was already created.");
        }

        createLocationInNether(x, y, z);

        LOTM.getLogUtil().info("Created Underworld Location.");

        if (underworldLocation == null)
            throw new RuntimeException("Failed to initialize Underworld");

        // Remove a small area immediately
        BlockUtil.getSphereBlocks(underworldLocation, 8).forEach(b -> b.setType(Material.AIR));

        // Schedule bone block placement in batches
        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            LOTM.getLogUtil().info("Creating Underworld.");
            List<Block> blocks = BlockUtil.getBlocksInCircleRadius(underworldLocation.getBlock(), underworldSize, true, Material.BONE_BLOCK, Material.VOID_AIR, Material.CAVE_AIR);
            placeBlocksInBatches(blocks, Material.BONE_BLOCK, 1000, 2);
        }, 5);
    }

    public void loop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                World world = underworldLocation.getWorld();
                if (world == null)
                    return;

                world.getNearbyEntities(underworldLocation, underworldSize, 190, 190).stream().filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e).filter(e -> !e.getScoreboardTags().contains("dead") && !EntityUtil.UNDEAD_ENTITIES.contains(e.getType())).forEach(e -> {
                    e.damage(500);
                });

                world.getNearbyEntities(underworldLocation, 190, 190, 190).stream().filter(e -> !(e instanceof LivingEntity)).forEach(Entity::remove);
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 20);
    }

    private void placeBlocksInBatches(List<Block> blocks, Material material, int batchSize, int delay) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                for (int i = 0; i < batchSize && index < blocks.size(); i++, index++) {
                    blocks.get(index).setType(material);
                }

                if (index >= blocks.size()) {
                    this.cancel(); // Stop task when all blocks are placed
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, delay);
    }

    private void createLocationInNether(double x, double y, double z) {
        if(underworldLocation != null && !underworldLocation.getBlock().getType().isSolid())
            return;

        // Attempt to get the End world
        World netherWorld = Bukkit.getWorld("world_nether");

        // If the world is not loaded, load it
        if (netherWorld == null) {
            netherWorld = Bukkit.createWorld(new WorldCreator("world_nether"));
        }

        // If the world is successfully loaded, create a Location
        if (netherWorld != null) {
            underworldLocation = new Location(netherWorld, x, y, z);
            return;
        }
    }

}
