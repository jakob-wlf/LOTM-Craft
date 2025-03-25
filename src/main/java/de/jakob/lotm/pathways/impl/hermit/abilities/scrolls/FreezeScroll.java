package de.jakob.lotm.pathways.impl.hermit.abilities.scrolls;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FreezeScroll extends MysticalScroll{
    public FreezeScroll(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onUse(LivingEntity entity) {
        Location startLoc = entity.getEyeLocation();

        if(startLoc.getWorld() == null)
            return;

        AtomicInteger i = new AtomicInteger(0);

        Beyonder beyonder = LOTM.getInstance().getBeyonder(entity.getUniqueId());

        runTaskWithDuration(5, 30, () -> {
            if(beyonder != null && beyonder.isGriefingEnabled()) {
                List<Block> blocks = BlockUtil.getBlocksInCircleRadius(startLoc.getBlock(), i.get(), true, Material.PACKED_ICE);
                blocks.forEach(block -> block.setType(Material.PACKED_ICE));

                List<LivingEntity> targets = getNearbyLivingEntities(entity, i.get(), startLoc, startLoc.getWorld());

                targets.forEach(t -> {
                    for(int j = 0; j < t.getHeight(); j++) {
                        t.getEyeLocation().getBlock().getRelative(0, -1 * j, 0).setType(Material.PACKED_ICE);
                    }
                });
            }

            i.addAndGet(5);

            addPotionEffectToNearbyEntities(entity, 30, startLoc, startLoc.getWorld(), new PotionEffect(PotionEffectType.SLOWNESS, 30, 10, false, false, false));
            damageNearbyEntities(39.5, 1, entity, i.get(), startLoc, startLoc.getWorld(), false, 0, 10, false);

            ParticleSpawner.displayParticles(startLoc.getWorld(), Particle.SNOWFLAKE, startLoc, 350, i.get(), 2, i.get(), 0, 200);
            ParticleSpawner.displayParticles(startLoc.getWorld(), Particle.SNOWFLAKE, startLoc, 350, i.get(), .2, i.get(), 0, 200);
        }, null);
    }
}
