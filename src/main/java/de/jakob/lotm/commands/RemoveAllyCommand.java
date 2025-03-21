package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RemoveAllyCommand implements CommandExecutor {

    private final HashMap<UUID, Set<UUID>> invitations = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }
        if(args.length != 1) {
            player.sendMessage("§cWrong usage: Use /removeally <Player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null || target.isValid()) {
            player.sendMessage(ErrorMessageUtil.playerNotFound());
            return true;
        }

        if(!target.getScoreboardTags().contains("ally_" + player.getUniqueId()) && !player.getScoreboardTags().contains("ally_" + target.getUniqueId())) {
            player.sendMessage("§cYou are not allies.");
            return true;
        }

        player.getScoreboardTags().remove("ally_" + target.getUniqueId());
        target.getScoreboardTags().remove("ally_" + player.getUniqueId());

        return true;
    }
}
