package de.jakob.lotm.commands;

import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import de.jakob.lotm.util.minecraft.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BeyonderHelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }

        if(args.length != 0) {
            sender.sendMessage("§cInvalid usage! Use §6/beyonderhelp");
            return true;
        }

        TextUtil.displayMessageWithCorrectLineBreaks(player, TextUtil.helpMessage);
        return true;
    }
}
