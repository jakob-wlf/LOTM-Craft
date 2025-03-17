package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class Tame extends Ability {

    public Tame(Pathway pathway, int sequence, AbilityType type, String name, Material item, String description, String id) {
        super(pathway, sequence, type, name, item, description, id);

        canBeUSedByNonPlayer = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        LivingEntity nearestEntity = getTargetEntity(entity, 4, true, 1.5, true);

        if(nearestEntity == null) {
            return;
        }

        if(nearestEntity.getScoreboardTags().contains("belongs_to_" + entity.getUniqueId())) {
            nearestEntity.getScoreboardTags().remove("belongs_to_" + entity.getUniqueId());
            ParticleSpawner.displayParticles(nearestEntity.getWorld(), Particle.ANGRY_VILLAGER, nearestEntity.getEyeLocation().subtract(0, nearestEntity.getHeight() / 2, 0), 50, .2, nearestEntity.getHeight() / 2, .2, 0, 200);
            return;
        }

        if(nearestEntity.getScoreboardTags().stream().anyMatch(s -> s.startsWith("belongs_to")) || nearestEntity instanceof Player)
            return;

        if(!beyonder.removeSpirituality(12))
            return;

        BeyonderEntity beyonderEntity = (BeyonderEntity) (LOTM.getInstance().getBeyonder(nearestEntity.getUniqueId()) == null ? LOTM.getInstance().createBeyonder(nearestEntity.getUniqueId(), LOTM.getInstance().getPathway("moon"), 9) : LOTM.getInstance().getBeyonder(nearestEntity.getUniqueId()));

        if(beyonderEntity == null)
            return;

        if(beyonderEntity.getCurrentSequence() < beyonder.getCurrentSequence()) {
            beyonderEntity.setCurrentTarget(entity);
            return;
        }

        if(entity instanceof Player player)
            player.playSound(nearestEntity, Sound.ENTITY_SNIFFER_HAPPY, 4, 1);

        ParticleSpawner.displayParticles(nearestEntity.getWorld(), Particle.HAPPY_VILLAGER, nearestEntity.getEyeLocation().subtract(0, nearestEntity.getHeight() / 2, 0), 50, .2, nearestEntity.getHeight() / 2, .2, 0, 200);
        ParticleSpawner.displayParticles(nearestEntity.getWorld(), Particle.HEART, nearestEntity.getEyeLocation().subtract(0, nearestEntity.getHeight() / 2, 0), 15, .2, nearestEntity.getHeight() / 2, .2, 0, 200);

        nearestEntity.getScoreboardTags().add("belongs_to_" + entity.getUniqueId());
    }
}
