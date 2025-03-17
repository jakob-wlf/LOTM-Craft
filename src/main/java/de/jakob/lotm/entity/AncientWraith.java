package de.jakob.lotm.entity;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vex;

public class AncientWraith {
    public static void spawn(Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        Vex wraith = (Vex) world.spawnEntity(location, EntityType.VEX);
        wraith.setCustomName("§dAncient Wraith");
        wraith.setCustomNameVisible(true);
        wraith.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        wraith.getAttribute(Attribute.SCALE).setBaseValue(2.5);
        wraith.getScoreboardTags().add("ancient_wraith");
        wraith.getScoreboardTags().add("no_spawn");

        BeyonderEntity beyonderEntity = (BeyonderEntity) LOTM.getInstance().createBeyonder(wraith.getUniqueId(), LOTM.getInstance().getPathway("door"), 5, false, false, true);
        if(beyonderEntity == null)
            return;
        beyonderEntity.setDropsCharacteristic(false);
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("door").getAbility("physical_enhancements_door"));
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("moon").getAbility("shards_of_darkness"));
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("abyss").getAbility("poisonous_flame"));
        beyonderEntity.addMultiplierModifier(.8);
    }
}
