package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class BeyonderEntityCommandTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> entityTypes = Stream.of(EntityType.values()).filter(EntityType::isAlive).map(Enum::name).toList();
        if(args.length == 0)
            return entityTypes;

        if(args.length == 1) {
            return entityTypes.stream().filter(entityType -> entityType.startsWith(args[0])).toList();
        }

        List<String> booleans = List.of("true", "false");
        if(args.length == 2) {
            if(args[1].isEmpty() || args[1].isBlank())
                return booleans;
            return booleans.stream().filter(aBoolean -> aBoolean.startsWith(args[1])).toList();
        }

        if(args.length == 3) {
            List<String> pathways = LOTM.getInstance().getPathwayKeys();

            if(args[2].isEmpty() || args[2].isBlank())
                return pathways;
            return pathways.stream().filter(pathway -> pathway.startsWith(args[2])).toList();
        }

        List<String> sequences = List.of("9", "8", "7", "6", "5", "4", "3", "2", "1");
        if(args.length == 4) {
            if(args[3].isEmpty() || args[3].isBlank())
                return sequences;
        }

        if(args.length == 5) {
            return List.of("true", "false");
        }

        return List.of("");
    }
}
