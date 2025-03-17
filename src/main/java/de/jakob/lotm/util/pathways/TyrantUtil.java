package de.jakob.lotm.util.pathways;

import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TyrantUtil {
    public static Particle.DustOptions blueDust = new Particle.DustOptions(Color.fromRGB(143, 255, 244), 1.4f);
    public static Particle.DustOptions blueDust2 = new Particle.DustOptions(Color.fromRGB(143, 255, 244), 1f);
    private static final Random random = new Random();

    public static void strikeLightning(Location loc, boolean griefing, Particle.DustOptions dust, Particle.DustOptions dust2, double detail) {
        World world = loc.getWorld();

        if (world == null)
            return;

        while(!loc.getBlock().getType().isSolid() && loc.getY() >= 0) {
            loc.subtract(0, .5, 0);
        }
        Location top = loc.clone().add(0, 30, 0);

        Location lightningLoc = top.clone();
        int breakoutCounter = 0;
        while(breakoutCounter < 120 && !lightningLoc.getBlock().getType().isSolid()) {
            ParticleSpawner.displayParticles(world, Particle.DUST, lightningLoc, (int) (detail), 0, 0, 0, 0, dust, 1040);
            if(breakoutCounter > 110)
                lightningLoc.add(0, -.5, 0);
            else
                lightningLoc.add(random.nextDouble(-.25, .25), -.5, random.nextDouble(-.25, .25));
            breakoutCounter++;
        }

        for(int i = 0; i < random.nextInt(5) + 2; i++) {
            Location branchLoc = top.clone();
            int counter = 0;
            while(counter < 110 && !branchLoc.getBlock().getType().isSolid()) {
                ParticleSpawner.displayParticles(world, Particle.DUST, branchLoc, (int) (detail), 0, 0, 0, 0, dust2, 1040);
                branchLoc.add(random.nextDouble(-.5, .5), -.2, random.nextDouble(-.5, .5));
                counter++;
            }
        }

        Material[] burnMaterials = {
                Material.GRASS_BLOCK,
                Material.SAND,
                Material.GRAVEL,
                Material.DIRT,
                Material.DIRT_PATH,
                Material.COARSE_DIRT,
                Material.STONE,
                Material.OAK_LOG,
                Material.JUNGLE_LOG,
                Material.ACACIA_LOG,
                Material.DARK_OAK_LOG,
                Material.BIRCH_LOG,
                Material.SPRUCE_LOG,
                Material.MANGROVE_LOG

        };

        world.playSound(lightningLoc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2, 1);

        if (griefing) {
            int radius = 6;
            int power = 4;

            world.createExplosion(lightningLoc, power, true);


            ArrayList<Block> blocks = BlockUtil.getBlocksInCircleRadius(lightningLoc.getBlock(), radius, true);
            for (Block block : blocks) {
                if (random.nextInt(22) == 0) {
                    Block fire = block.getLocation().add(0, 1, 0).getBlock();
                    if (!fire.getType().isSolid())
                        fire.setType(Material.FIRE);
                }

                if (Arrays.asList(burnMaterials).contains(block.getType())) {
                    if (random.nextInt(3) != 0)
                        block.setType(Material.BASALT);
                }
            }
        }
    }
}
