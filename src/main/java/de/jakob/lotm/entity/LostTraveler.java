package de.jakob.lotm.entity;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Random;

public class LostTraveler {
    public static void spawn(Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        LivingEntity entity = (LivingEntity) world.spawnEntity(location, EntityType.ENDERMAN);
        entity.setCustomName("§dLost Traveler");
        entity.setCustomNameVisible(true);
        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(30);
        entity.getAttribute(Attribute.SCALE).setBaseValue(1.45);
        entity.getScoreboardTags().add("lost_traveler");
        entity.getScoreboardTags().add("no_spawn");

        BeyonderEntity beyonderEntity = (BeyonderEntity) LOTM.getInstance().createBeyonder(entity.getUniqueId(), LOTM.getInstance().getPathway("door"), 5, false, false, false);
        if(beyonderEntity == null)
            return;
        beyonderEntity.setDropsCharacteristic(false);
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("door").getAbility("physical_enhancements_door"));
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("door").getAbility("blink"));
        List<Pathway> pathways = LOTM.getInstance().getPathways().stream().filter(pathway -> !pathway.getName().equals("door")).toList();
        Random random = new Random();
        for(int i = 0; i < 4; i++) {
            Pathway pathway = pathways.get(random.nextInt(pathways.size()));
            List<Ability> abilities = pathway.getAbilities().stream().filter(ability -> ability.getSequence() >= 5).filter(ability -> !(ability instanceof PassiveAbility)).toList();
            if(abilities.isEmpty()) {
                i--;
                continue;
            }
            beyonderEntity.addAbility(abilities.get(random.nextInt(abilities.size())));
        }
        beyonderEntity.addMultiplierModifier(.8);
    }
}
