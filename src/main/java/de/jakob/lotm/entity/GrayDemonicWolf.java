package de.jakob.lotm.entity;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

public class GrayDemonicWolf {
    public static void spawn(Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        Wolf wolf = (Wolf) world.spawnEntity(location, EntityType.WOLF);
        wolf.setCustomName("§8§lGray Demonic Wolf");
        wolf.setCustomNameVisible(true);
        wolf.getAttribute(Attribute.MAX_HEALTH).setBaseValue(60);
        wolf.getAttribute(Attribute.SCALE).setBaseValue(2.75);
        wolf.getScoreboardTags().add("gray_demonic_wolf");
        wolf.setAngry(true);
        wolf.setVariant(Wolf.Variant.ASHEN);
        wolf.getScoreboardTags().add("no_spawn");

        BeyonderEntity beyonderEntity = (BeyonderEntity) LOTM.getInstance().createBeyonder(wolf.getUniqueId(), LOTM.getInstance().getPathway("red_priest"), 5, false, false, true);
        if(beyonderEntity == null)
            return;
        beyonderEntity.setDropsCharacteristic(false);
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("red_priest").getAbility("physical_enhancements_hunter"));
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("red_priest").getAbility("pyrokinesis"));
    }
}
