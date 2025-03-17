package de.jakob.lotm.commands;

import de.jakob.lotm.entity.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EntityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 1) {
            sender.sendMessage("§cWrong usage: /entity [entity_id]");
            return true;
        }

        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return true;
        }

        Location loc = player.getLocation();
        switch (args[0]) {
            case "salamander" -> FireSalamander.spawn(loc);
            case "hunting_spider" -> BlackHuntingSpider.spawn(loc);
            case "gray_demonic_wolf" -> GrayDemonicWolf.spawn(loc);
            case "blue_shadow_falcon" -> BlueShadowFalcon.spawn(loc);
            case "lost_traveler" -> LostTraveler.spawn(loc);
            case "ancient_wraith" -> AncientWraith.spawn(loc);
            default -> sender.sendMessage("§cWrong usage: /entity [entity_id]");
        }

        return true;
    }
}
