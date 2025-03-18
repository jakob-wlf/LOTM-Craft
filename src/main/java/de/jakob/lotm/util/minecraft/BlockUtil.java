package de.jakob.lotm.util.minecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

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

    public static List<Block> getBlocksInLine(Location center, Vector direction, int length, int height, boolean ignoreAir, Material... ignore) {
        List<Block> blocks = new ArrayList<>();
        World world = center.getWorld();
        if (world == null) {
            return blocks;
        }

        direction.normalize();
        Location start = center.clone().subtract(direction.clone().multiply(length / 2));

        for (int i = 0; i < length; i++) {
            Location point = start.clone().add(direction.clone().multiply(i));
            for (int y = -height / 2; y <= height / 2; y++) {
                Block block = world.getBlockAt(point.getBlockX(), point.getBlockY() + y, point.getBlockZ());
                if ((block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR || !ignoreAir)
                        && !Arrays.asList(ignore).contains(block.getType())) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
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

    public static List<Block> getBlocksInRectangle(Location corner1, Location corner2, boolean ignoreAir, Material... ignore) {
        List<Block> blocks = new ArrayList<>();
        World world = corner1.getWorld();
        if (world == null || !world.equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Both locations must be in the same world");
        }

        int startX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int endX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int startY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int endY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int startZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int endZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if ((block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR || !ignoreAir)
                            && !Arrays.asList(ignore).contains(block.getType())) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInRectangle(Location center, int width, int height, boolean ignoreAir, Material... ignore) {
        List<Block> blocks = new ArrayList<>();
        World world = center.getWorld();
        if (world == null) {
            return blocks;
        }

        int startX = center.getBlockX() - width / 2;
        int endX = center.getBlockX() + width / 2;
        int startY = center.getBlockY() - height / 2;
        int endY = center.getBlockY() + height / 2;
        int startZ = center.getBlockZ() - width / 2;
        int endZ = center.getBlockZ() + width / 2;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if ((block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR || !ignoreAir)
                            && !Arrays.asList(ignore).contains(block.getType())) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }


}
