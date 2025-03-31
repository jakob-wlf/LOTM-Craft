package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Comparator;

@NoArgsConstructor
public class SpiritCommunication extends Ability {

    public SpiritCommunication(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {

        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = getTargetEntity(entity, 20, EntityType.ALLAY);

        if(target == null) {
            entity.sendMessage("§7No Target found.");
            return;
        }

        BeyonderSpirit spirit = entity.getNearbyEntities(21, 21, 21).stream().filter(e -> LOTM.getInstance().getBeyonders().containsKey(e.getUniqueId())).map(e -> LOTM.getInstance().getBeyonder(e.getUniqueId())).filter(b -> b instanceof BeyonderSpirit).map(b -> (BeyonderSpirit) b).filter(b -> b.getCurrentTarget() == null).max(Comparator.comparing(Beyonder::getCurrentSequence)).orElse(null);
        if(spirit == null) {
            entity.sendMessage("§7No Spirits nearby.");
            return;
        }

        if(spirit.getCurrentSequence() < beyonder.getCurrentSequence()) {
            entity.sendMessage("§7Spirit is too powerful.");
            return;
        }

        if(!beyonder.removeSpirituality(24))
            return;

        if(random.nextInt(4) == 0 && spirit.getCurrentSequence() <= beyonder.getCurrentSequence()) {
            entity.sendMessage("§7Spirit chose not to cooperate.");
            return;
        }

        spirit.setCurrentTarget(target);
        if(entity instanceof Player player)
            player.playSound(player, Sound.ENTITY_ALLAY_HURT, .7f, .6f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> spirit.setCurrentTarget(null), 20 * 8);
        runTaskWithDuration(2, 20 * 8, () -> {
            if(beyonder.getEntity() == null)
                return;
            boolean seeSpirits = beyonder.getEntity().getScoreboardTags().stream().anyMatch(s -> s.startsWith("see_spirits"));
            if(spirit.getEntity() != null && seeSpirits)
                ParticleSpawner.displayParticles(spirit.getEntity().getWorld(), Particle.ANGRY_VILLAGER, spirit.getEntity().getLocation(), random.nextInt(2) + 1, .5, .5, .5, 0, 200);
        }, null);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        player.getScoreboardTags().add("see_spirits_spirit_communication");
    }

    @Override
    public void onSwitchOutItem(Beyonder beyonder, Player player) {
        player.getScoreboardTags().remove("see_spirits_spirit_communication");
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        return entity.getNearbyEntities(21, 21, 21).stream().filter(e -> LOTM.getInstance().getBeyonders().containsKey(e.getUniqueId())).map(e -> LOTM.getInstance().getBeyonder(e.getUniqueId())).filter(b -> b instanceof BeyonderSpirit).map(b -> (BeyonderSpirit) b).anyMatch(b -> b.getCurrentTarget() == null);
    }
}
