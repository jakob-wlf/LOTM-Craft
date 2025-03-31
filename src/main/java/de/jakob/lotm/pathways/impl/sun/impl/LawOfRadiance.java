package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Marker;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;

public class LawOfRadiance extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    public LawOfRadiance(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        casting.add(beyonder);

        if(!beyonder.removeSpirituality(50)) {
            return;
        }

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getLocation().add(0, .2, 0);

        Location particleLoc = beyonder.getEntity().getEyeLocation().add(0, 5.5, 0);
        Vector direction1 = new Vector(.1, 0, 1);
        Vector direction2 = new Vector(1, 0, 0);
        Vector direction3 = new Vector(0, 1, 0);

        Marker marker = (Marker) world.spawnEntity(location, EntityType.MARKER);
        marker.getScoreboardTags().add("no_abilities");
        marker.getScoreboardTags().add("radius_20");
        marker.getScoreboardTags().add("sequence_" + beyonder.getCurrentSequence());
        marker.getScoreboardTags().add("exclude_" + beyonder.getUuid());

        LOTM.getInstance().getEntitiesToRemove().add(marker);

        List<Location> lightBlocks = BlockUtil.getBlocksInCircleRadius(location.clone().subtract(0, .2, 0).getBlock(), 15, false).stream().filter(block -> block.getType() == Material.AIR).map(Block::getLocation).toList();
        lightBlocks.forEach(loc -> loc.getBlock().setType(Material.LIGHT));

        LOTM.getInstance().getBlocksToRemove().addAll(lightBlocks);

        playSound(beyonder, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        playSound(beyonder, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 40) {
                    casting.remove(beyonder);
                }

                if(!casting.contains(beyonder)) {
                    lightBlocks.forEach(LOTM.getInstance().getBlocksToRemove()::remove);
                    lightBlocks.forEach(loc -> loc.getBlock().setType(Material.AIR));

                    LOTM.getInstance().getEntitiesToRemove().remove(marker);
                    marker.remove();
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(particleLoc, direction1, 3, Particle.END_ROD, null, 25);
                ParticleUtil.drawCircle(particleLoc, direction2, 3, Particle.END_ROD, null, 25);
                ParticleUtil.drawCircle(particleLoc, direction3, 3, Particle.END_ROD, null, 25);

                ParticleSpawner.displayParticles(world, Particle.END_ROD, location, 45, 10, 0, 10, 0, 200);

                counter+=5;
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }

}
