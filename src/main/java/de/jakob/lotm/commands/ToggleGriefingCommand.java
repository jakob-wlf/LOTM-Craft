package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleGriefingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }

        BeyonderPlayer beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);
        if(beyonder == null) {
            player.sendMessage("§cYou must be a Beyonder to use this command.");
            return true;
        }

        beyonder.setGriefingEnabled(!beyonder.isGriefingEnabled());
        player.sendMessage("§a" + (beyonder.isGriefingEnabled() ? "Enabled " : "Disabled ") + "Griefing for your Beyonder Powers");

        return true;
    }
}
