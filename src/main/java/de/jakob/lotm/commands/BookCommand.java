package de.jakob.lotm.commands;

import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BookCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }

        player.getInventory().addItem(ItemsUtil.getInfoBook());

        return true;
    }
}
