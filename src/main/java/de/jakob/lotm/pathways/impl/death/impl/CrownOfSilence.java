package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

@NoArgsConstructor
public class CrownOfSilence extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    public CrownOfSilence(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Pathway p = beyonder.getCurrentPathway();
        int currentSequence = beyonder.getCurrentSequence();

        if(entity == null) {
            casting.remove(beyonder);
            return;
        }

        Marker marker = (Marker) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.MARKER);
        marker.getScoreboardTags().add("no_abilities");
        marker.getScoreboardTags().add("radius_30");
        marker.getScoreboardTags().add("sequence_" + beyonder.getCurrentSequence());
        marker.getScoreboardTags().add("exclude_" + beyonder.getUuid());

        LOTM.getInstance().getEntitiesToRemove().add(marker);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!entity.isValid() || beyonder.getCurrentPathway() != p || beyonder.getCurrentSequence() != currentSequence) {
                    casting.remove(beyonder);
                }

                if(!beyonder.removeSpirituality(7)) {
                    casting.remove(beyonder);
                }

                if(!casting.contains(beyonder)) {
                    LOTM.getInstance().getEntitiesToRemove().remove(marker);
                    marker.remove();
                    cancel();
                    return;
                }

                getNearbyLivingEntities(entity, 30, entity.getLocation(), entity.getWorld()).forEach(e -> {
                    if(e instanceof Player player)
                        player.stopAllSounds();
                });

                if(entity instanceof Player player)
                    player.stopAllSounds();

                marker.teleport(entity);

                restrictMovement(entity, entity.getLocation(), 30);
            }
        }.runTaskTimer(plugin, 0, 0);
    }
}
