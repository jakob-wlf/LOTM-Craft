package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import de.jakob.lotm.pathways.beyonder.BeyonderNPC;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.*;

public class ChainOfCommand extends Ability implements Listener {

    private final HashMap<UUID, List<UUID>> entitiesUnderPlayer;

    private final Particle.DustOptions greyDust = new Particle.DustOptions(org.bukkit.Color.fromRGB(128, 128, 128), 1);
    private final Particle.DustOptions redDust = new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 0, 0), 2.25f);

    //TODO: load army on reload using scoreboard tags
    public ChainOfCommand(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        entitiesUnderPlayer = new HashMap<>();
        canBeCopied = false;
        canBeUsedByNonPlayer = false;

        LOTM.getInstance().registerListener(this);
    }

    //TODO: Rework
    @Override
    public void rightClick(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        LivingEntity nearestEntity = getNearbyLivingEntities(entity, 3, entity.getLocation(), entity.getWorld())
                .stream().min(Comparator.comparing(
                        e -> e.getLocation().distance(entity.getLocation())
                )).orElse(null);

        if(nearestEntity == null) return;

        //TODO: Check if entity already serves under player in any way
        for(String tag : nearestEntity.getScoreboardTags()) {
            if(tag.startsWith("belongs_to"))
                return;
        }

        if(nearestEntity instanceof Player) {
            //TODO: Ask Player for permission
            return;
        }

        Beyonder targetBeyonder = LOTM.getInstance().getBeyonder(nearestEntity.getUniqueId());
        if(targetBeyonder != null) {
            int sequenceDifference = targetBeyonder.getCurrentSequence() - beyonder.getCurrentSequence();

            if(sequenceDifference >= 0) {
                return;
            }

            if(targetBeyonder instanceof BeyonderEntity beyonderEntity) {
                if(beyonderEntity.isHostile()) {
                    return;
                }
            }
            if(targetBeyonder instanceof BeyonderNPC beyonderNPC) {
                if(beyonderNPC.isHostile())
                    return;
            }
        }
        else {
            LOTM.getInstance().createBeyonder(
                    nearestEntity.getUniqueId(),
                    LOTM.getInstance().getPathway("red_priest"),
                    9
            );
        }

        World world = nearestEntity.getWorld();

        world.spawnParticle(Particle.ANGRY_VILLAGER, nearestEntity.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
        world.spawnParticle(Particle.DUST, nearestEntity.getLocation(), 10, 0.5, 0.5, 0.5, greyDust);

        world.playSound(nearestEntity.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 1);

        if(entitiesUnderPlayer.containsKey(entity.getUniqueId())) {
            List<UUID> entities = entitiesUnderPlayer.get(entity.getUniqueId());

            if(entities.size() >= 5 * beyonder.getCurrentMultiplier()) return;
            if(entities.contains(nearestEntity.getUniqueId())) return;

            entitiesUnderPlayer.get(entity.getUniqueId()).add(nearestEntity.getUniqueId());
        } else {
            entitiesUnderPlayer.put(entity.getUniqueId(), new ArrayList<>(List.of(nearestEntity.getUniqueId())));
        }

        nearestEntity.addScoreboardTag("chain_of_command-" + entity.getUniqueId());
        nearestEntity.addScoreboardTag("belongs_to" + entity.getUniqueId());
        nearestEntity.addScoreboardTag("no_damage-" + entity.getUniqueId());

        if(!entity.getScoreboardTags().contains("chain_of_command-" + entity.getUniqueId())) {
            entity.addScoreboardTag("chain_of_command-" + entity.getUniqueId());
        }

        if(!entity.getScoreboardTags().contains("no_damage-" + entity.getUniqueId())) {
            entity.addScoreboardTag("no_damage-" + entity.getUniqueId());
        }
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!entitiesUnderPlayer.containsKey(player.getUniqueId())) return;

        List<LivingEntity> entities = entitiesUnderPlayer.get(player.getUniqueId()).stream()
                .map(uuid -> (LivingEntity) Bukkit.getEntity(uuid))
                .filter(
                        entity -> entity != null && entity.isValid() &&
                                entity.getWorld().equals(player.getWorld()) &&
                                entity.getLocation().distance(player.getLocation()) <= 50 * beyonder.getCurrentMultiplier()
                )
                .toList();

        for(LivingEntity entity : entities) {
            player.spawnParticle(Particle.DUST, entity.getEyeLocation().subtract(0, entity.getHeight() / 2, 0), 20, .8, 1, .8, redDust);
        }
    }

    @EventHandler
    public void onTargetEntity(EntityTargetLivingEntityEvent event) {
        if(!(event.getTarget() instanceof Player player)) return;

        if(!entitiesUnderPlayer.containsKey(player.getUniqueId())) return;

        if(entitiesUnderPlayer.get(player.getUniqueId()).contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHitEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player player)) return;

        if(!entitiesUnderPlayer.containsKey(player.getUniqueId())) return;

        if(!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        if(entitiesUnderPlayer.get(player.getUniqueId()).contains(livingEntity.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        for(UUID uuid : entitiesUnderPlayer.get(player.getUniqueId())) {
            Entity entity = Bukkit.getEntity(uuid);
            if(!(entity instanceof Mob mob)) continue;

            mob.setTarget(livingEntity);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;

        if(!entitiesUnderPlayer.containsKey(player.getUniqueId())) return;

        Beyonder beyonder = LOTM.getInstance().getBeyonder(player.getUniqueId());
        if(beyonder == null) return;

        double damage = event.getDamage();

        entitiesUnderPlayer.get(player.getUniqueId()).removeIf(uuid -> {
            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);

            return ((entity == null || !entity.isValid()) && !p.hasPlayedBefore());
        });

        List<LivingEntity> entities = entitiesUnderPlayer.get(player.getUniqueId()).stream()
                .map(uuid -> (LivingEntity) Bukkit.getEntity(uuid))
                .filter(
                        entity -> entity != null && entity.isValid() &&
                                entity.getWorld().equals(player.getWorld()) &&
                                entity.getLocation().distance(player.getLocation()) <= 50 * beyonder.getCurrentMultiplier()
                )
                .toList();

        if(entities.isEmpty()) return;

        double damagePerEntity = damage / entities.size();
        double minDamage = damage * .1;

        for(LivingEntity entity : entities) {
            entity.damage(damagePerEntity);
        }

        event.setDamage(Math.max(damagePerEntity, minDamage));
    }
}
