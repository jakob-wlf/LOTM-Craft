package de.jakob.lotm.entity;


import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class BlackHuntingSpider {
    public static void spawn(Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        CaveSpider spider = (CaveSpider) world.spawnEntity(location, EntityType.CAVE_SPIDER);
        spider.setCustomName("§8Black Hunting Spider");
        spider.setCustomNameVisible(true);
        spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(30);
        spider.getScoreboardTags().add("hunting_spider");
        spider.getScoreboardTags().add("no_spawn");

        BeyonderEntity beyonderEntity = (BeyonderEntity) LOTM.getInstance().createBeyonder(spider.getUniqueId(), LOTM.getInstance().getPathway("red_priest"), 4, false, false, true);
        if(beyonderEntity == null)
            return;
        beyonderEntity.setDropsCharacteristic(false);
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("red_priest").getAbility("physical_enhancements_hunter"));
    }
}
