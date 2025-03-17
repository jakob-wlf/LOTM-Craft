package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.EntityUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

@NoArgsConstructor
public class SpiritControlling extends Ability {

    public SpiritControlling(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void leftClick(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = null;

        Location eyeLocation = entity.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        if(eyeLocation.getWorld() == null)
            return;

        for (int i = 1; i <= 20; i++) {
            Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(i));

            if (currentLocation.getBlock().getType().isSolid() || currentLocation.getWorld() == null) {
                break;
            }

            for (Entity nearbyEntity : currentLocation.getWorld().getNearbyEntities(currentLocation, 1.1, 1.1, 1.1)) {
                if (
                        nearbyEntity instanceof LivingEntity && EntityUtil.mayDamage(nearbyEntity, entity)[1] &&
                                nearbyEntity.getScoreboardTags().contains("spirit") &&
                                nearbyEntity.getScoreboardTags().contains("belongs_to_" + entity.getUniqueId()) &&
                                !nearbyEntity.equals(entity)
                ) {
                    target = (LivingEntity) nearbyEntity;
                    break;
                }
            }
        }

        if(target == null) {
            entity.sendMessage("§7No Spirit found.");
            return;
        }

        if(entity instanceof Player player)
            player.playSound(player, Sound.ENTITY_WITHER_HURT, .4f, .1f);

        target.getScoreboardTags().remove("belongs_to_" + entity.getUniqueId());
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        leftClick(beyonder);
    }

    //TODO: Add sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        if(entity.getNearbyEntities(30, 30, 30).stream().filter(e -> e.getScoreboardTags().contains("spirit") && e.getScoreboardTags().contains("belongs_to_" + entity.getUniqueId())).toList().size() > 6 * (Math.pow(beyonder.getCurrentMultiplier(), 2))) {
            beyonder.getEntity().sendMessage("§7Too many spirits. Release a spirit with left click.");
            return;
        }

        LivingEntity target = null;

        Location eyeLocation = entity.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        if(eyeLocation.getWorld() == null)
            return;

        for (int i = 1; i <= 20; i++) {
            Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(i));

            if (currentLocation.getBlock().getType().isSolid() || currentLocation.getWorld() == null) {
                break;
            }

            for (Entity nearbyEntity : currentLocation.getWorld().getNearbyEntities(currentLocation, 1.1, 1.1, 1.1)) {
                if (
                        nearbyEntity instanceof LivingEntity && EntityUtil.mayDamage(nearbyEntity, entity)[1] &&
                                nearbyEntity.getScoreboardTags().contains("spirit") &&
                                !nearbyEntity.equals(entity)
                ) {
                    target = (LivingEntity) nearbyEntity;
                    break;
                }
            }
        }

        if(target == null) {
            entity.sendMessage("§7No Spirit found.");
            return;
        }

        if(!beyonder.removeSpirituality(33)) {
            return;
        }

        if(target.getScoreboardTags().stream().anyMatch(tag -> tag.startsWith("belongs_to_"))) {
            entity.sendMessage("§7Spirit is already under control.");
            return;
        }

        target.getScoreboardTags().add("belongs_to_" + entity.getUniqueId());

    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        player.getScoreboardTags().add("see_spirits_spirit_controlling");
    }

    @Override
    public void onSwitchOutItem(Beyonder beyonder, Player player) {
        player.getScoreboardTags().remove("see_spirits_spirit_controlling");
    }
}
