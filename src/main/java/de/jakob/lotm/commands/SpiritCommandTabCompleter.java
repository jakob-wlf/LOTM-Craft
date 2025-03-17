package de.jakob.lotm.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpiritCommandTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> entityTypes = List.of("default", "wither", "fire", "mooshroom", "vex");
        if(args.length == 0)
            return entityTypes;

        if(args.length == 1) {
            return entityTypes.stream().filter(entityType -> entityType.startsWith(args[0])).toList();
        }

        return List.of();
    }
}
