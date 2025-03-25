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

public class AllyCommand implements CommandExecutor {

    private final HashMap<UUID, Set<UUID>> invitations = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }
        if(args.length != 1) {
            player.sendMessage("§cWrong usage: Use /ally <Player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null || !target.isValid()) {
            player.sendMessage(ErrorMessageUtil.playerNotFound());
            return true;
        }

        for(Map.Entry<UUID, Set<UUID>> entry : invitations.entrySet()) {
            if(entry.getValue().contains(player.getUniqueId()) && entry.getKey() == target.getUniqueId()) {
                entry.getValue().remove(player.getUniqueId());
                player.sendMessage("§aYou are now allies.");
                target.sendMessage("§aYou are now allies.");
                target.getScoreboardTags().add("ally_" + player.getUniqueId());
                player.getScoreboardTags().add("ally_" + target.getUniqueId());
                return true;
            }
        }

        player.sendMessage("§aRequested to be allied to " + args[0]);
        target.sendMessage("§a" + player.getDisplayName() + " §awants to be on a team with you. use /ally " + player.getName() + " §ato accept");

        Set<UUID> list = invitations.containsKey(player.getUniqueId()) ? invitations.get(player.getUniqueId()) : new HashSet<>();
        list.add(target.getUniqueId());
        invitations.put(player.getUniqueId(), list);

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            if(invitations.get(player.getUniqueId()).contains(target.getUniqueId())) {
                invitations.get(player.getUniqueId()).remove(target.getUniqueId());
                player.sendMessage("§cInvitation expired.");
                target.sendMessage("§cInvitation expired.");
            }
        }, 20 * 60 * 2);

        return true;
    }
}
