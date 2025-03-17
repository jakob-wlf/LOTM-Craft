package de.jakob.lotm.util.minecraft;

import org.bukkit.entity.Entity;

import java.util.List;
import java.util.UUID;

public class EntityUtil {

    //returns a boolean array, first boolean is mayDamage, second is shouldDamage
    public static boolean[] mayDamage(Entity entity, Entity damager) {
        if(entity.isInvulnerable())
            return new boolean[]{false, false};

        if(areOnTheSameTeam(entity, damager))
            return new boolean[]{false, false};

        List<String> noDamage = entity.getScoreboardTags().stream().filter(s -> s.startsWith("no_damage")).toList();
        for(String tag : noDamage) {
            if(damager.getScoreboardTags().contains(tag)) {
                return new boolean[]{false, false};
            }
        }

        List<String> protectedEntities = entity.getScoreboardTags().stream().filter(s -> s.startsWith("protected")).toList();
        for(String tag : protectedEntities) {
            if(!damager.getScoreboardTags().contains(tag)) {
                return new boolean[]{false, true};
            }
        }

        List<String> servantEntities = damager.getScoreboardTags().stream().filter(s -> s.startsWith("belongs_to_")).toList();
        for(String tag : servantEntities) {
            if(entity.getScoreboardTags().contains(tag)) {
                return new boolean[]{false, false};
            }

            UUID uuid = UUID.fromString(tag.replace("belongs_to_", ""));
            if(uuid.equals(entity.getUniqueId()))
                return new boolean[]{false, false};
        }

        if(entity.getScoreboardTags().contains("spirit") && damager.getScoreboardTags().stream().noneMatch(tag -> tag.startsWith("see_spirits")))
            return new boolean[] {false, false};

        return new boolean[]{true, true};
    }

    public static boolean areOnTheSameTeam(Entity entity1, Entity entity2) {
        List<String> servantEntities = entity1.getScoreboardTags().stream().filter(s -> s.startsWith("belongs_to_")).toList();
        for(String tag : servantEntities) {
            if(entity2.getScoreboardTags().contains(tag)) {
                return true;
            }
        }

        if(entity1.getScoreboardTags().contains("belongs_to_" + entity2.getUniqueId()) || entity2.getScoreboardTags().contains("belongs_to_" + entity1.getUniqueId()))
            return true;

        List<String> teams = entity1.getScoreboardTags().stream().filter(s -> s.startsWith("lotm_team_")).toList();
        if(entity2.getScoreboardTags().stream().anyMatch(teams::contains)) {
            return true;
        }

        return false;
    }

}