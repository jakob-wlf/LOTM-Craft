package de.jakob.lotm.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileShootListener implements Listener {

    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() instanceof Entity entity && entity.getScoreboardTags().contains("spirit")) {
            event.setCancelled(true);
            return;
        }
    }

}
