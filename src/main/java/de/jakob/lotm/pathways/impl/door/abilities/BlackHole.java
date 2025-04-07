package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor
public class BlackHole extends Ability implements Listener {

    private final HashSet<FallingBlock> blocks = new HashSet<>();

    private final HashSet<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 8f);

    public BlackHole(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        LOTM.getInstance().registerListener(this);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        casting.add(beyonder);

        if (!beyonder.removeSpirituality(400))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 16).add(0, 1, 0);

        if(loc.getWorld() == null)
            return;

        HashSet<FallingBlock> currentBlocks = new HashSet<>();
        Set<Block> nearbyBlocks = new HashSet<>(BlockUtil.getSphereBlocks(loc, 35).stream().sorted(Comparator.comparing(b -> b.getLocation().distance(loc.clone()))).toList());

        AtomicBoolean isCancelled = new AtomicBoolean(false);

        runTaskWithDuration(1, 20 * 60 * 10, isCancelled, () -> {
            if(!casting.contains(beyonder) || !entity.isValid() || entity.getWorld() != loc.getWorld()) {
                isCancelled.set(true);
                return;
            }

            ParticleUtil.createParticleSphere(loc, 3, 80, Particle.DUST, dust);

            damageNearbyEntities(43, beyonder.getCurrentMultiplier(), entity, 3, loc, loc.getWorld());

            getNearbyLivingEntities(entity, 30, loc).forEach(e -> {
                if(e.getWorld() != loc.getWorld())
                    return;

                if(e instanceof Player player) {
                    if(player.getGameMode().equals(GameMode.SPECTATOR))
                        return;
                }

                Vector direction = loc.toVector().subtract(e.getLocation().toVector()).normalize();
                e.setVelocity(direction.multiply(.5));
            });

            Set<Block> removeFromSet = new HashSet<>();

            nearbyBlocks.stream().limit(800).forEach(b -> {
                if(!b.getType().isSolid() || b.getRelative(0, 1, 0).getType().isSolid() || beyonder.isGriefingEnabled() ? random.nextInt(90) != 0 : random.nextInt(400) != 0)
                    return;

                FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(b.getLocation().add(.5, 1.5, .5), b.getBlockData());
                fallingBlock.setGravity(false);
                fallingBlock.setDropItem(false);
                fallingBlock.setHurtEntities(false);
                currentBlocks.add(fallingBlock);
                blocks.add(fallingBlock);
                removeFromSet.add(b);

                runTaskWithDuration(1, 20 * 6, () -> {
                    if(fallingBlock.getLocation().distanceSquared(loc) < 2) {
                        fallingBlock.remove();
                        return;
                    }

                    fallingBlock.setVelocity(loc.clone().toVector().subtract(fallingBlock.getLocation().toVector()).normalize().multiply(.5));
                }, fallingBlock::remove);

                if(beyonder.isGriefingEnabled())
                    b.setType(Material.AIR);
            });

            nearbyBlocks.removeAll(removeFromSet);
        }, () -> currentBlocks.forEach(b -> {
            b.remove();
            blocks.remove(b);
        }));
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(event.getEntity() instanceof FallingBlock fallingBlock && blocks.contains(fallingBlock)) {
            fallingBlock.remove();
            event.setCancelled(true);
        }
    }
}
