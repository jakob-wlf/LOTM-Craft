package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class CalamityCreation extends SelectableAbility implements Listener {

    private final HashSet<Beyonder> castingColdness = new HashSet<>();
    private final HashSet<Beyonder> creatingVolcano = new HashSet<>();

    private final HashSet<FallingBlock> magmaBlocksToRemove = new HashSet<>();
    private final HashSet<FallingBlock> magmaBlocksToSetLava = new HashSet<>();

    private final Particle.DustOptions volcanoDust = new Particle.DustOptions(Color.fromRGB(255, 101, 54), 4f);

    public CalamityCreation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        LOTM.getInstance().registerListener(this);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Extreme Coldness", "Volcano"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 450,
                1, 390
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> {
                castExtremeColdness(beyonder);
            }
            case 1 -> {
                createVolcano(beyonder);
            }
        }
    }

    private void createVolcano(Beyonder beyonder) {
        if(creatingVolcano.contains(beyonder))
            return;

        creatingVolcano.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 25);
        World world = loc.getWorld();
        if(world == null)
            return;

        world.createExplosion(loc.clone(), 15, beyonder.isGriefingEnabled(), beyonder.isGriefingEnabled(), entity);

        runTaskWithDuration(4, 20 * 15, () -> {
            ParticleSpawner.displayParticles(world, Particle.LARGE_SMOKE, loc, 220, 3, 5, 3, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.FLAME, loc, 420, 3, 5, 3, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.EXPLOSION, loc, 20, 3, 5, 3, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, loc, 300, 3, 5, 3, 0, volcanoDust, 200);
            for(int i = 0; i < random.nextInt(3) + 2; i++) {
                FallingBlock magma = world.spawnFallingBlock(loc.clone().add(0, 2.75, 0), Material.MAGMA_BLOCK.createBlockData());
                magma.setVelocity((new Vector(random.nextDouble(-.5, .5), 1.2, random.nextDouble(-.5, .5))).normalize().multiply(1.8));
                magma.setDropItem(false);
                if(beyonder.isGriefingEnabled())
                    magmaBlocksToSetLava.add(magma);
                else
                    magmaBlocksToRemove.add(magma);
            }

            damageNearbyEntities(34, beyonder.getCurrentMultiplier(), entity, 14, loc, world, true, 20 * 3);

        }, () -> {
            creatingVolcano.remove(beyonder);
        });
    }

    private void castExtremeColdness(Beyonder beyonder) {
        if(castingColdness.contains(beyonder))
            return;

        castingColdness.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location startLoc = entity.getEyeLocation();

        if(startLoc.getWorld() == null)
            return;

        AtomicInteger i = new AtomicInteger(0);
        runTaskWithDuration(5, 30, () -> {
            if(beyonder.isGriefingEnabled()) {
                List<Block> blocks = BlockUtil.getBlocksInCircleRadius(startLoc.getBlock(), i.get(), true, Material.PACKED_ICE);
                blocks.forEach(block -> block.setType(Material.PACKED_ICE));
                i.addAndGet(5);

                List<LivingEntity> targets = getNearbyLivingEntities(entity, 30, startLoc, startLoc.getWorld());

                targets.forEach(t -> {
                    for(int j = 0; j < t.getHeight(); j++) {
                        t.getEyeLocation().getBlock().getRelative(0, -1 * j, 0).setType(Material.PACKED_ICE);
                    }
                });
            }

            addPotionEffectToNearbyEntities(entity, 30, startLoc, startLoc.getWorld(), new PotionEffect(PotionEffectType.SLOWNESS, 30, 10, false, false, false));
            damageNearbyEntities(39.5, beyonder.getCurrentMultiplier(), entity, 30, startLoc, startLoc.getWorld());

            ParticleSpawner.displayParticles(startLoc.getWorld(), Particle.SNOWFLAKE, startLoc, 350, 20, 2, 20, 0, 200);

        }, () -> runTaskWithDuration(5, 20 * 25, () -> {
            if(beyonder.isGriefingEnabled()) {
                List<Block> blocks = BlockUtil.getBlocksInCircleRadius(entity.getEyeLocation().getBlock(), 30, true, Material.PACKED_ICE);
                blocks.forEach(block -> block.setType(Material.PACKED_ICE));

                List<LivingEntity> targets = getNearbyLivingEntities(entity, 30, entity.getEyeLocation(), entity.getWorld());

                targets.forEach(t -> {
                    for(int j = 0; j < t.getHeight(); j++) {
                        t.getEyeLocation().getBlock().getRelative(0, -1 * j, 0).setType(Material.PACKED_ICE);
                    }
                });
            }

            addPotionEffectToNearbyEntities(entity, 30, entity.getEyeLocation(), entity.getWorld(), new PotionEffect(PotionEffectType.SLOWNESS, 30, 10, false, false, false));
            damageNearbyEntities(39.5, beyonder.getCurrentMultiplier(), entity, 30, entity.getEyeLocation(), entity.getWorld());
            ParticleSpawner.displayParticles(entity.getWorld(), Particle.SNOWFLAKE, entity.getEyeLocation(), 350, 20, 2, 20, 0, 200);
        }, () -> {
            castingColdness.remove(beyonder);
        }));
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        if(!(event.getEntity() instanceof FallingBlock fallingBlock))
            return;

        if(magmaBlocksToRemove.contains(fallingBlock)) {
            fallingBlock.remove();
            event.setCancelled(true);
            fallingBlock.getWorld().createExplosion(fallingBlock.getLocation(), 7, false, false);
            magmaBlocksToRemove.remove(fallingBlock);
            return;
        }

        if(magmaBlocksToSetLava.contains(fallingBlock)) {
            if(random.nextBoolean()) {
                fallingBlock.getWorld().createExplosion(fallingBlock.getLocation(), 7, true, true);
                fallingBlock.remove();
                event.setCancelled(true);
                event.getBlock().setType(Material.LAVA);
            }

            magmaBlocksToSetLava.remove(fallingBlock);
        }
    }
}
