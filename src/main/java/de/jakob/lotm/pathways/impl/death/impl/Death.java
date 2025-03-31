package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class Death extends Ability {

    public Death(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        hasCooldown = true;
        cooldownTicks = 20 * 6;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getLocation();

        World world = loc.getWorld();
        if(world == null)
            return;

        AtomicInteger i = new AtomicInteger(0);


        runTaskWithDuration(5, 20 * 3, () -> {
            if(beyonder.isGriefingEnabled()) {
                List<Block> blocks = BlockUtil.getBlocksInCircleRadius(loc.getBlock(), i.get(), true, Material.SOUL_SOIL, Material.SOUL_SAND, Material.BASALT).stream().filter(b -> b.getType().isSolid()).toList();
                blocks.forEach(block -> {
                    switch(random.nextInt(4)) {
                        case 0:
                            block.setType(Material.SOUL_SAND);
                            break;
                        case 1, 2:
                            block.setType(Material.SOUL_SOIL);
                            break;
                        case 3:
                            block.setType(Material.BASALT);
                    }
                });
            }

            i.addAndGet(2);

            getNearbyLivingEntities(entity, i.get(), loc, world, true, EntityType.SKELETON).forEach(e -> {
                System.out.println(e);

                Beyonder beyonderTarget = LOTM.getInstance().getBeyonder(e.getUniqueId());
                Pathway p = LOTM.getInstance().getPathway("death");
                int sequence = 7;

                if(beyonderTarget != null && beyonderTarget.getCurrentSequence() < 3) {
                    e.damage(80 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
                    return;
                }

                if(beyonderTarget != null && beyonderTarget.getCurrentSequence() < 7) {
                    sequence = beyonderTarget.getCurrentSequence();
                    p = beyonderTarget.getCurrentPathway();
                }

                Skeleton skeleton = (Skeleton) e.getWorld().spawnEntity(e.getLocation(), EntityType.SKELETON);
                skeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                skeleton.getScoreboardTags().add("belongs_to_" + beyonder.getUuid());
                skeleton.getScoreboardTags().add("no_spawn");
                skeleton.getScoreboardTags().add("no_drop");
                LOTM.getInstance().createBeyonder(skeleton.getUniqueId(), p, sequence);

                if(e instanceof Player) {
                    if(e.hasMetadata("NPC")) {
                        NPC npc = CitizensAPI.getNPCRegistry().getNPC(e);
                        if(npc != null) {
                            npc.despawn();
                            npc.destroy();
                        }
                    }
                    else
                        e.setHealth(0);
                } else {
                    e.remove();
                }
            });

            damageNearbyEntities(60, beyonder.getCurrentMultiplier(), entity, i.get(), loc, world, false, 0);

            ParticleSpawner.displayParticles(loc.getWorld(), Particle.SOUL, loc, 350, i.get(), 2, i.get(), 0, 200);
        }, null);
    }
}
