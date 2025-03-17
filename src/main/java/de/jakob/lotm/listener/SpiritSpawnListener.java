package de.jakob.lotm.listener;

import de.jakob.lotm.entity.spirit.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Random;

public class SpiritSpawnListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(random.nextInt(155) != 0)
            return;

        Location loc = event.getTo();
        if(loc == null)
            return;

        Location location = loc.clone().add(random.nextInt(20) - 10, random.nextInt(6), random.nextInt(20) - 10);

        if(location.getWorld() == null || location.getBlock().getType().isSolid())
            return;

        List<Entity> nearbySpirits = location.getWorld().getNearbyEntities(location, 35, 35, 35).stream().filter(entity -> entity.getScoreboardTags().contains("spirit")).toList();
        if(nearbySpirits.size() >= 12)
            return;

        switch(random.nextInt(15)) {
            default -> DefaultSpirit.spawn(location);
            case 7, 8 -> VexSpirit.spawn(location);
            case 10 -> WitherSpirit.spawn(location);
            case 11 -> BlazeSpirit.spawn(location);
            case 12 -> CowSpirit.spawn(location);
        }
    }
}
