package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropListener implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        for(Pathway pathway : LOTM.getInstance().getPathways()) {
            for(Ability ability : pathway.getAbilities()) {
                if(event.getItemDrop().getItemStack().isSimilar(ability.getItem())) {
                    event.getItemDrop().remove();
                    break;
                }
            }
        }
    }
}
