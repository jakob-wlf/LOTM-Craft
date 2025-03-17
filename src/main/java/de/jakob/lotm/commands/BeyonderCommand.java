package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import de.jakob.lotm.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BeyonderCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1)
            return false;

        if(args[0].equalsIgnoreCase("set")) {
            if(args.length < 4 || args.length > 5) {
                sender.sendMessage("§cWrong usage.");
                return false;
            }

            Player player = Bukkit.getPlayerExact(args[1]);

            if(player == null || !player.isValid()) {
                sender.sendMessage(ErrorMessageUtil.playerNotFound());
                return true;
            }

            Pathway pathway = LOTM.getInstance().getPathway(args[2]);
            if(pathway == null) {
                sender.sendMessage("§cPathway not found.");
                return true;
            }

            if(!MathUtil.isInteger(args[3])) {
                sender.sendMessage("§cSequence must be a number.");
                return true;
            }

            int sequence = Integer.parseInt(args[3]);
            if(sequence > 9 || sequence < 1) {
                sender.sendMessage("§cSequence must be a number between 9 and 1.");
                return true;
            }

            boolean includePreviousSequences = true;
            if(args.length == 5) {
                if(!MathUtil.isBoolean(args[4])) {
                    sender.sendMessage("§cInclude Previous Sequences must be true or false.");
                    return true;
                }

                includePreviousSequences = Boolean.parseBoolean(args[4]);
            }

            BeyonderPlayer beyonderPlayer = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), true);

            if(beyonderPlayer == null) {
                sender.sendMessage("§cSomething went wrong.");
                return true;
            }

            pathway.setSequencePathway(beyonderPlayer, sequence, includePreviousSequences);
            sender.sendMessage("§aSuccessfully made " + player.getName() + " a Sequence " + sequence + " Beyonder of the " + pathway.getColorPrefix() + pathway.getDisplayName() + " §aPathway.");
        }
        else if(args[0].equalsIgnoreCase("clear")) {
            if(args.length != 2) {
                sender.sendMessage("§cWrong usage.");
                return false;
            }

            Player player = Bukkit.getPlayerExact(args[1]);

            if(player == null || !player.isValid()) {
                sender.sendMessage(ErrorMessageUtil.playerNotFound());
                return true;
            }

            BeyonderPlayer beyonderPlayer = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);
            if(beyonderPlayer == null)
                return true;

            beyonderPlayer.setSequencePathway(null, 10);
            beyonderPlayer.clearAbilities();
        }

        return true;
    }
}
