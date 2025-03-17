package de.jakob.lotm.listener;

import de.jakob.lotm.util.minecraft.EntityUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(!EntityUtil.mayDamage(event.getEntity(), event.getDamager())[0]) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA ||event.getCause() == EntityDamageEvent.DamageCause.MELTING) {
            if(event.getEntity() instanceof LivingEntity livingEntity)
                livingEntity.setNoDamageTicks(0);
        }

        if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && event.getEntity().getType() == EntityType.PLAYER)
            event.setCancelled(true);
    }
}
