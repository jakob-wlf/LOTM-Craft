package de.jakob.lotm.util.minecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtil {

    public static List<Block> getPassableBlocksInCircle(Location center, double radius, int detail) {
        List<Block> passableBlocks = new ArrayList<>();
        World world = center.getWorld();
        if (world == null) return passableBlocks;

        // Iterate over the circle's coordinates
        for (int step = 0; step < detail; step++) {
            double angle = 2 * Math.PI * step / detail;

            // Calculate x and z coordinates on the circle's circumference
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            // Create the target location by adding the calculated x and z to the center
            Location targetLocation = center.clone().add(x, 0, z);

            // Check for the passable block at the specific y level of the circle
            Block block = world.getBlockAt(targetLocation);

            // Check if the block is passable (e.g., air, water)
            if (block.isPassable()) {
                passableBlocks.add(block);
            }
        }

        return passableBlocks;
    }

    public static List<Location> createHollowCube(World world, Location center, int radius, Material blockType, Material... blocksToReplace) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }

        int startX = center.getBlockX() - radius;
        int startY = center.getBlockY() - radius;
        int startZ = center.getBlockZ() - radius;

        int endX = center.getBlockX() + radius;
        int endY = center.getBlockY() + radius;
        int endZ = center.getBlockZ() + radius;

        List<Location> locations = new ArrayList<>();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    // Check if the block is on the boundary of the cube
                    boolean isBoundary = x == startX || x == endX || y == startY || y == endY || z == startZ || z == endZ;

                    if (isBoundary) {
                        Location currentLocation = new Location(world, x, y, z);
                        Material currentBlock = world.getBlockAt(currentLocation).getType();

                        // Replace block only if it's the target block to replace or blockToReplace is null
                        if (blocksToReplace.length == 0 || List.of(blocksToReplace).contains(currentBlock)) {
                            world.getBlockAt(currentLocation).setType(blockType);
                            locations.add(currentLocation);
                        }
                    }
                }
            }
        }

        return locations;
    }

    public static ArrayList<Block> getBlocksInCircleRadius(Block start, int radius, boolean ignoreAir, Material... ignore) {

        Location loc = start.getLocation();

        ArrayList<Block> blocks = new ArrayList<>();

        for (int i = radius; i > -radius; i--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if ((x * x) + (z * z) <= Math.pow(radius, 2)) {
                        Block block = start.getWorld().getBlockAt((int) loc.getX() + x, (int) loc.getY() + i, (int) loc.getZ() + z);
                        if ((block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR || !ignoreAir) && !Arrays.asList(ignore).contains(block.getType()))
                            blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

}
