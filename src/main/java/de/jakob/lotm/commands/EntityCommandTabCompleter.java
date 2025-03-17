package de.jakob.lotm.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityCommandTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> entityTypes = List.of("salamander", "hunting_spider", "gray_demonic_wolf", "blue_shadow_falcon", "ancient_wraith", "lost_traveler");
        if(args.length == 0)
            return entityTypes;

        if(args.length == 1) {
            return entityTypes.stream().filter(entityType -> entityType.startsWith(args[0])).toList();
        }

        return List.of();
    }
}
