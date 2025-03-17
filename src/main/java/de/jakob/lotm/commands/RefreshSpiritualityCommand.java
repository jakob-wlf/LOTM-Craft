package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RefreshSpiritualityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }

        BeyonderPlayer beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);
        if(beyonder == null || beyonder.getCurrentPathway() == null || beyonder.getCurrentSequence() == 10) {
            player.sendMessage("§cYou must be a Beyonder to use this command.");
            return true;
        }

        beyonder.refreshSpirituality();
        player.sendMessage("§aYour spirituality has been refreshed.");
        return true;
    }
}
