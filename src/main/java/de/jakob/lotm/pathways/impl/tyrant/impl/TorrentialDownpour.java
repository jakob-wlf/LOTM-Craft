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
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class TorrentialDownpour extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();
    final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(30, 120, 255), 1.25f);

    public TorrentialDownpour(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        if(!beyonder.removeSpirituality(280))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        Location loc = getTargetLocation(entity, 25);

        List<Block> blocks = BlockUtil.getBlocksInCircleRadius(loc.getBlock(), 25, true);

        runTaskWithDuration(5, 20 * 25, () -> {
            world.playSound(loc, Sound.WEATHER_RAIN, 10, 1);

            ParticleSpawner.displayParticles(world, Particle.FALLING_WATER, loc, 1800, 21, 11, 21, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, loc, 1800, 21, 11, 21, 0, dust, 200);
            ParticleSpawner.displayParticles(world, Particle.CLOUD, loc.clone().add(0, 14, 0), 1800, 21, 0, 21, 0, 200);

            damageNearbyEntities(35, beyonder.getCurrentMultiplier(), entity, 22, loc, world);

            if(beyonder.isGriefingEnabled()) {
                List<Block> possibleBlocks = blocks.stream().filter(b -> !b.getRelative(0, 1, 0).getType().isSolid()).toList();
                for(int i = 0; i < 17; i++)
                    possibleBlocks.get(random.nextInt(possibleBlocks.size())).setType(Material.AIR);

            }
        }, () -> {
            casting.remove(beyonder);
        });
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }
}
