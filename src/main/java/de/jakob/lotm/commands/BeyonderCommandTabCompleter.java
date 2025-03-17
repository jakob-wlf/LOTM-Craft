package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeyonderCommandTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> commandOptions = List.of("set", "clear");
        if(args.length == 0)
            return commandOptions;
        if(args.length == 1) {
            return commandOptions.stream().filter(option -> option.startsWith(args[0])).toList();
        }

        List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if(args.length == 2) {
            if(args[1].isEmpty() || args[1].isBlank())
                return players;
            return players.stream().filter(player -> player.startsWith(args[1])).toList();
        }

        if(args[0].equals("set")) {
            List<String> pathways = LOTM.getInstance().getPathwayKeys();

            if(args.length == 3) {
                if(args[2].isEmpty() || args[2].isBlank())
                    return pathways;
                return pathways.stream().filter(pathway -> pathway.startsWith(args[2])).toList();
            }

            List<String> sequences = List.of("9", "8", "7", "6", "5", "4", "3", "2", "1");
            if(args.length == 4) {
                if(args[3].isEmpty() || args[3].isBlank())
                    return sequences;
            }

            List<String> includePreviousSequences = List.of("true", "false");
            if(args.length == 5) {
                if(args[4].isEmpty() || args[4].isBlank())
                    return includePreviousSequences;
                return includePreviousSequences.stream().filter(option -> option.startsWith(args[4])).toList();
            }
        }

        return List.of("");
    }
}
