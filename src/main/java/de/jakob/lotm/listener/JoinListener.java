package de.jakob.lotm.listener;

import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("book_received"))
            return;

        player.getInventory().addItem(ItemsUtil.getInfoBook());
        player.getScoreboardTags().add("book_received");
    }
}
