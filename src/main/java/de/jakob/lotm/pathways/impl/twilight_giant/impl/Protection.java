package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@NoArgsConstructor
public class Protection extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();
    private final Set<Beyonder> cooldown = new HashSet<>();
    private final HashMap<Beyonder, Integer> radiusPerPlayer = new HashMap<>();

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 176, 92), 1.5f);

    private final HashMap<Beyonder, Set<LivingEntity>> protectedEntities = new HashMap<>();

    public Protection(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    //TODO: Add sounds
    //TODO: When attacking, use up spirituality
    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getLocation();;

        if(cooldown.contains(beyonder))
            return;

        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        casting.add(beyonder);
        protectedEntities.put(beyonder, new HashSet<>(Set.of(entity)));

        int radius = !(beyonder instanceof BeyonderPlayer) ? 4 : radiusPerPlayer.get(beyonder);

        new BukkitRunnable() {
            List<Location> blocks = BlockUtil.createHollowCube(world, location, radius, Material.AIR, Material.AIR, Material.BARRIER);
            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 8 && abilityType == AbilityType.RECORDED) {
                    casting.remove(beyonder);
                }

                if(!beyonder.removeSpirituality(5)) {
                    casting.remove(beyonder);
                }

                if(!casting.contains(beyonder)) {
                    blocks.forEach(l -> l.getBlock().setType(Material.AIR));
                    blocks.forEach(l -> LOTM.getInstance().getBlocksToRemove().remove(l));
                    for(LivingEntity protectedEntity : protectedEntities.get(beyonder)) {
                        protectedEntity.getScoreboardTags().remove("protected_" + entity.getUniqueId());
                    }
                    protectedEntities.remove(beyonder);
                    cancel();
                    return;
                }

                List<LivingEntity> entitiesInCube = getNearbyEntitiesInCubeWithCenter(null, location, radius, world);
                for(LivingEntity entityInCube : entitiesInCube) {
                    protectedEntities.get(beyonder).add(entityInCube);
                }

                Set<LivingEntity> removeEntities = new HashSet<>();

                for(LivingEntity protectedEntity : protectedEntities.get(beyonder)) {
                    if(!entitiesInCube.contains(protectedEntity)) {
                        removeEntities.add(protectedEntity);
                        protectedEntity.getScoreboardTags().remove("protected_" + entity.getUniqueId());
                        continue;
                    }
                    protectedEntity.getScoreboardTags().add("protected_" + entity.getUniqueId());
                }

                removeEntities.forEach(e -> protectedEntities.get(beyonder).remove(e));

                blocks = BlockUtil.createHollowCube(world, location, radius, Material.BARRIER, Material.AIR, Material.BARRIER);

                for(Location l : blocks) {
                    ParticleSpawner.displayParticles(world, Particle.DUST, l, 2, .3, .3, .3, 0, dustOptions, 150);
                }

                blocks.forEach(l -> {
                    LOTM.getInstance().getBlocksToRemove().add(l);
                });

                counter++;
            }
        }.runTaskTimer(plugin, 0, 7);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        cooldown.add(beyonder);

        if(!radiusPerPlayer.containsKey(beyonder))
            radiusPerPlayer.put(beyonder, 5);

        radiusPerPlayer.replace(beyonder, radiusPerPlayer.get(beyonder) + 1);
        if(radiusPerPlayer.get(beyonder) > 7)
            radiusPerPlayer.replace(beyonder, 4);

        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldown.remove(beyonder), 10);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        if(!casting.contains(beyonder))
            return true;
        return random.nextInt(15) == 0;
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!radiusPerPlayer.containsKey(beyonder))
            radiusPerPlayer.put(beyonder, 5);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Radius: " + radiusPerPlayer.get(beyonder)));
    }
}
