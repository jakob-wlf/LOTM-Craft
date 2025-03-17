package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class DeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player) {
            Iterator<ItemStack> iterator = event.getDrops().iterator();
            while(iterator.hasNext()) {
                ItemStack item = iterator.next();
                for(Pathway pathway : LOTM.getInstance().getPathways()) {
                    for(Ability ability : pathway.getAbilities()) {
                        if(item.isSimilar(ability.getItem())) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity())) {
            event.setDeathMessage(null); // Remove the message for all NPCs
        }
    }
}
