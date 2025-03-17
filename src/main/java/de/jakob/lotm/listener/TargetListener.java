package de.jakob.lotm.listener;

import de.jakob.lotm.util.minecraft.EntityUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class TargetListener implements Listener {

    @EventHandler
    public void onEntityTargetEntity(EntityTargetEvent event) {
        if(event.getTarget() != null && !EntityUtil.mayDamage(event.getTarget(), event.getEntity())[1]) {
            event.setCancelled(true);
        }
    }

}
