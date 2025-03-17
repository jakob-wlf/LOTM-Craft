package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class LightOfDawn extends Ability {

    private final ArrayList<Beyonder> currentlyCasting = new ArrayList<>();
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 2f);
    private final Particle.DustOptions dustOptions3 = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 2.4f);

    public LightOfDawn(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(currentlyCasting.contains(beyonder) || !beyonder.removeSpirituality(60))
            return;

        Location location = beyonder.getEntity().getEyeLocation().add(0, 5.5, 0);
        Vector direction1 = new Vector(.1, 0, 1);
        Vector direction2 = new Vector(1, 0, 0);
        Vector direction3 = new Vector(0, 1, 0);

        World world = location.getWorld();
        if(world == null)
            return;

        List<Location> lightBlocks = BlockUtil.getBlocksInCircleRadius(location.clone().subtract(0, .2, 0).getBlock(), 20, false).stream().filter(block -> block.getType() == Material.AIR).map(Block::getLocation).toList();
        lightBlocks.forEach(loc -> loc.getBlock().setType(Material.LIGHT));

        LOTM.getInstance().getBlocksToRemove().addAll(lightBlocks);

        world.playSound(location, Sound.BLOCK_BELL_USE, 1, .8f);

        currentlyCasting.add(beyonder);
        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                if(counter > 20 * 10 || beyonder.getEntity() == null || !beyonder.getEntity().isValid()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> currentlyCasting.remove(beyonder), 20 * 5);
                    lightBlocks.forEach(LOTM.getInstance().getBlocksToRemove()::remove);
                    lightBlocks.forEach(loc -> loc.getBlock().setType(Material.AIR));
                    cancel();
                    return;
                }

                if(counter % 40 == 0) {
                    damageNearbyEntities(14, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 22, location, world, false, 0);
                }

                world.playSound(location, Sound.BLOCK_BELL_RESONATE, 1, .2f);

                ParticleUtil.drawCircle(location, direction1, 3, Particle.DUST, dustOptions2, 25);
                ParticleUtil.drawCircle(location, direction2, 3, Particle.DUST, dustOptions2, 25);
                ParticleUtil.drawCircle(location, direction3, 3, Particle.DUST, dustOptions2, 25);

                ParticleSpawner.displayParticles(world, Particle.END_ROD, location.clone().subtract(0, 6.7, 0), 8, 16, 5, 16, 0, 250);
                ParticleSpawner.displayParticles(world, Particle.END_ROD, location.clone().subtract(0, 6.7, 0), 30, 16, .5, 16, 0, 250);
                ParticleSpawner.displayParticles(world, Particle.DUST, location.clone().subtract(0, 6.7, 0), 120, 16, .5, 16, 0, dustOptions3, 250);

                counter+=2;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

}
