package de.jakob.lotm.commands;

import de.jakob.lotm.entity.spirit.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpiritCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 1) {
            sender.sendMessage("§cWrong usage: /spirit [spirit_id]");
            return true;
        }

        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return true;
        }

        Location loc = player.getLocation();
        switch (args[0]) {
            case "default" -> DefaultSpirit.spawn(loc);
            case "wither" -> WitherSpirit.spawn(loc);
            case "fire" -> BlazeSpirit.spawn(loc);
            case "mooshroom" -> CowSpirit.spawn(loc);
            case "vex" -> VexSpirit.spawn(loc);
            default -> sender.sendMessage("§cWrong usage: /spirit [spirit_id]");
        }

        return true;
    }

}
