package de.jakob.lotm.pathways.impl.chained.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor
public class IceManipulation extends SelectableAbility {

    public IceManipulation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Ice Stun", "Ice Wall"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(0, 25, 1, 20));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch(ability) {
            case 0 -> castIceStun(beyonder);
            case 1 -> castIceWall(beyonder);
        }
    }

    private void castIceWall(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location loc = getLocationLookedAt(entity, 10, true);

        final HashSet<Location> allBlocks = new HashSet<>();

        Vector perp = VectorUtil.rotateAroundY(entity.getEyeLocation().getDirection().setY(0), 90);

        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 1, 1);

        runTaskWithDuration(7, 20 * 6, () -> {
            List<Location> wallBlocks = BlockUtil.getBlocksInLine(loc, perp, 17, 9, false).stream().filter(block -> block.getType() == Material.AIR || block.getType() == Material.ICE).map(Block::getLocation).toList();

            wallBlocks.forEach(b -> {
                b.getBlock().setType(Material.ICE);
                allBlocks.add(b);

                ParticleSpawner.displayParticles(b.getWorld(), Particle.SNOWFLAKE, b.clone().add(0.5, .5, .5), 2, .5, .5, .5, 0, 200);
                ParticleSpawner.displayParticles(b.getWorld(), Particle.ITEM_SNOWBALL, b.clone().add(0.5, .5, .5), 2, .5, .5, .5, 0, 200);

                LOTM.getInstance().getBlocksToRemove().add(b);
            });

        }, () -> {
            if(!beyonder.isGriefingEnabled()) {
                allBlocks.forEach(b -> {
                    b.getBlock().setType(Material.AIR);
                    LOTM.getInstance().getBlocksToRemove().remove(b);
                });
            }
        });
    }

    private void castIceStun(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Vector direction = getDirectionNormalized(entity, 40);
        Location location = entity.getEyeLocation().add(direction);

        world.playSound(location, Sound.BLOCK_GLASS_BREAK, 1, 1);

        AtomicBoolean hit = new AtomicBoolean(false);

        runTaskWithDuration(1, 20 * 5, hit, () -> {
            ParticleSpawner.displayParticles(Particle.ITEM_SNOWBALL, location, 10, .2, .2, .2, 0, 200);
            ParticleSpawner.displayParticles(Particle.SNOWFLAKE, location, 30, .2, .2, .2, 0, 200);

            if(damageNearbyEntities(20, beyonder.getCurrentMultiplier(), entity, 2, location, world) || location.getBlock().getType().isSolid()) {
                hit.set(true);
                ParticleSpawner.displayParticles(Particle.SNOWFLAKE, location, 30, .2, .2, .2, 0, 200);
                ParticleSpawner.displayParticles(Particle.ITEM_SNOWBALL, location, 30, .2, .2, .2, 0, 200);

                addPotionEffectToNearbyEntities(entity, 5, location, world,
                        new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, 5, false, false, false),
                        new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1, false, false, false));

                if(beyonder.isGriefingEnabled()) {
                    BlockUtil.getSphereBlocks(location, 2).stream()
                            .filter(block -> !block.getType().isSolid())
                            .forEach(block -> block.setType(Material.ICE));
                }
            }

            location.add(direction);
        });
    }
}
