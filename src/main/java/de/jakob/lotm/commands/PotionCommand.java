package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import de.jakob.lotm.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PotionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if(args.length < 3 || args.length > 4) {
            sender.sendMessage("§cWrong usage.");
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if(player == null || !player.isValid()) {
            sender.sendMessage(ErrorMessageUtil.playerNotFound());
            return true;
        }

        Pathway pathway = LOTM.getInstance().getPathway(args[1]);
        if(pathway == null) {
            sender.sendMessage("§cPathway not found.");
            return true;
        }

        if(!MathUtil.isInteger(args[2])) {
            sender.sendMessage("§cSequence must be a number.");
            return true;
        }

        int sequence = Integer.parseInt(args[2]);
        if(sequence > 9 || sequence < 1) {
            sender.sendMessage("§cSequence must be a number between 9 and 1.");
            return true;
        }

        boolean includePreviousSequences = true;
        if(args.length == 4) {
            if(!MathUtil.isBoolean(args[3])) {
                sender.sendMessage("§cInclude Previous Sequences must be true or false.");
                return true;
            }

            includePreviousSequences = Boolean.parseBoolean(args[3]);
        }

        ItemStack potion = pathway.getPotions().getPotion(sequence, includePreviousSequences);

        player.getInventory().addItem(potion);

        return true;
    }
}
