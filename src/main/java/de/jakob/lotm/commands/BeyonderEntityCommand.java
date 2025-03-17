package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.MathUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BeyonderEntityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 4)
            return false;

        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return true;
        }

        try {
            EntityType entityType = EntityType.valueOf(args[0].toLowerCase());

            if(!entityType.isAlive()) {
                sender.sendMessage("§cYou can only spawn living entities.");
                return true;
            }

            boolean hostile = false;

            if(args[1].equalsIgnoreCase("true")) {
                hostile = true;
            } else if(!args[1].equalsIgnoreCase("false")) {
                sender.sendMessage("§cInvalid boolean.");
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

            boolean hasAI = true;

            if(args.length == 5) {
                if(args[4].equalsIgnoreCase("false")) {
                    hasAI = false;
                } else if(!args[4].equalsIgnoreCase("true")) {
                    sender.sendMessage("§cInvalid boolean.");
                    return true;
                }
            }

            if(entityType == EntityType.PLAYER) {
                LOTM.getInstance().createBeyonderNPC(pathway, sequence, hostile, player.getLocation(), !hasAI);
                return true;
            }

            Entity entity  = player.getWorld().spawnEntity(player.getLocation(), entityType);
            entity.setPersistent(true);
            if(entity instanceof LivingEntity livingEntity) {
                livingEntity.setAI(hasAI);
            }


            LOTM.getInstance().createBeyonder(entity.getUniqueId(), pathway, sequence, true, true, hostile);

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid entity type.");
            return true;
        }

        return true;
    }
}
