package de.jakob.lotm.pathways.impl.door.abilities;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class SpaceCollapse extends Ability{

    private static final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(74, 24, 125), 2f);
    private static final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 1.5f);
    private static final BlockData blockData = Bukkit.createBlockData(Material.BLACK_CONCRETE);

    public SpaceCollapse(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20 * 6;
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

        Location lastLoc = loc.clone().add(0, 11, 0);
        riftLocations.add(lastLoc.clone());

        for(int i = 0; i < 39; i++) {
            lastLoc.add(random.nextDouble(-.6, .6), -.5, random.nextDouble(-.6, .6));
            riftLocations.add(lastLoc.clone());
        }

        Set<Location> breakLocations = new HashSet<>();
        riftLocations.forEach(l -> {
            if(random.nextInt(10) < 3)
                return;

            Vector out = new Vector(random.nextDouble(-.5, .5), 0, random.nextDouble(-.5, .5)).normalize().multiply(.05);
            Location breakLoc = l.clone();
            for(int i = 0; i < 95; i++) {
                breakLocations.add(breakLoc.clone());
                breakLoc.add(out).add(random.nextDouble(-.25, .25), random.nextDouble(-.25, .25), random.nextDouble(-.25, .25));
            }
        });

        Location middleLoc = riftLocations.get(riftLocations.size() / 2);

        playRiftOpeningSound(world, loc);

        runTaskWithDuration(4, 20 * 3, () -> {
           for(Location riftLocation : riftLocations) {
               ParticleSpawner.displayParticles(world, Particle.DUST, riftLocation, 10, .175, .175, .175, 0, dust, 200);
               ParticleSpawner.displayParticles(world, Particle.END_ROD, riftLocation, 15, .06, .06, .06, 0, 200);
               damageNearbyEntities(1, beyonder.getCurrentMultiplier(), entity, .65, riftLocation, world, false, 0, 0, true, true);
            }
            getNearbyLivingEntities(entity, 15, loc, world).forEach(e -> {
                e.setVelocity(loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(.75));
            });
        }, () -> {
            if(beyonder.isGriefingEnabled()) {
                riftLocations.forEach(b -> {
                    BlockUtil.getSphereBlocks(b, 3).forEach(block -> block.setType(Material.AIR));
                });
                breakLocations.forEach(b -> {
                    BlockUtil.getSphereBlocks(b, 2).forEach(block -> block.setType(Material.AIR));
                });
            }
            ParticleSpawner.displayParticles(Particle.BLOCK, middleLoc, 1000, 4, 9.5, 4, 0, blockData, 200);
            playSpaceCollapsingSound(world, loc);
            runTaskWithDuration(2, 20 * 15, () -> {
                for (Location riftLocation : riftLocations) {
                    ParticleSpawner.displayParticles(world, Particle.DUST, riftLocation, 10, .175, .175, .175, 0, dust, 200);
                    ParticleSpawner.displayParticles(world, Particle.END_ROD, riftLocation, 15, .06, .06, .06, 0, 200);
                }

                for (Location breakLocation : breakLocations) {
                    ParticleSpawner.displayParticles(world, Particle.DUST, breakLocation, 1, 0, 0, 0, 0, dust2, 200);
                }

                damageNearbyEntities(59, beyonder.getCurrentMultiplier(), entity, 6, middleLoc, world, false, 0, 10, false, true);
            }, null);
        });

    }

    private void playRiftOpeningSound(World world, Location loc) {
        world.playSound(loc, Sound.AMBIENT_CAVE, 1f, 0.5f); // Adjust volume and pitch as needed

        world.playSound(loc, Sound.BLOCK_PORTAL_AMBIENT, 1f, 0.2f);
        world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.7f); // Lower pitch

        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 3f, 1.5f); // Higher pitch

        world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 3f, 1.2f); // Higher pitch
    }

    private void playSpaceCollapsingSound(World world, Location loc) {
        world.playSound(loc, Sound.BLOCK_PORTAL_AMBIENT, 1f, 0.1f); // Lower pitch, lower volume
        world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 3f, 0.5f); // Even lower pitch

        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.3f); // Lower volume, deeper sound
        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 3f, 1.5f); // Higher pitch

        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 3f, 1.5f); // Higher pitch


        world.playSound(loc, Sound.BLOCK_PORTAL_AMBIENT, 1f, 0.05f); // Very low pitch
        world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.3f); // Even lower pitch
    }
}
