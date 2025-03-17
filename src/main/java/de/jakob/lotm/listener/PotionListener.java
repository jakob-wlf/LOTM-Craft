package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PotionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if(item == null || item.getType() != Material.POTION || item.getItemMeta() == null)
            return;

        ItemMeta meta = item.getItemMeta();

        if(meta.getLore() == null || !meta.hasCustomModelData())
            return;

        int sequence = 0;
        Pathway pathway = null;
        boolean includePrevious = true;

        if(meta.getCustomModelData() == 102) {
            Beyonder beyonder = LOTM.getInstance().getBeyonder(event.getPlayer().getUniqueId(), true);

            if(beyonder == null)
                return;

            event.getPlayer().getInventory().remove(item);

            beyonder.looseControl(100);
            return;
        }

        for(Pathway p : LOTM.getInstance().getPathways()) {
            for(int i = 1; i < 10; i++) {
                ItemMeta potionMeta = p.getPotions().getPotion(i).getItemMeta();

                if(potionMeta == null || potionMeta.getLore() == null || !potionMeta.hasCustomModelData())
                    continue;

                if(potionMeta.getLore().get(0).equals(meta.getLore().get(0)) && potionMeta.getCustomModelData() == 100) {
                    sequence = i;
                    pathway = p;
                    break;
                } else if(potionMeta.getLore().get(0).equals(meta.getLore().get(0)) && potionMeta.getCustomModelData() == 101) {
                    includePrevious = false;
                    sequence = i;
                    pathway = p;
                    break;
                }
            }
        }

        if(pathway == null)
            return;

        Beyonder beyonder = LOTM.getInstance().getBeyonder(event.getPlayer().getUniqueId(), true);

        if(beyonder == null)
            return;

        event.getPlayer().getInventory().remove(item);

        beyonder.advance(pathway, sequence, includePrevious, true);
    }

}
