package de.jakob.lotm.entity;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;

public class BlueShadowFalcon {
    public static void spawn(Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        Parrot falcon = (Parrot) world.spawnEntity(location, EntityType.PARROT);
        falcon.setCustomName("§9Blue Shadow Falcon");
        falcon.setVariant(Parrot.Variant.BLUE);
        falcon.setCustomNameVisible(true);
        falcon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        falcon.getAttribute(Attribute.SCALE).setBaseValue(2.5);
        falcon.getScoreboardTags().add("blue_shadow_falcon");
        falcon.getScoreboardTags().add("no_spawn");

        BeyonderEntity beyonderEntity = (BeyonderEntity) LOTM.getInstance().createBeyonder(falcon.getUniqueId(), LOTM.getInstance().getPathway("tyrant"), 6, false, false, true);
        if(beyonderEntity == null)
            return;
        beyonderEntity.setDropsCharacteristic(false);
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("tyrant").getAbility("wind_manipulation"));
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("tyrant").getAbility("physical_enhancements_tyrant"));
        beyonderEntity.addMultiplierModifier(1.3);
    }
}
