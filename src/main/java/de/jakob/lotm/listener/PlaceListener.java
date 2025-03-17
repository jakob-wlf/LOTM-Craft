package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        for(Pathway pathway : LOTM.getInstance().getPathways()) {
            for(ItemStack characteristicItem : pathway.getCharacteristicItems()) {
                if(characteristicItem.isSimilar(item)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
