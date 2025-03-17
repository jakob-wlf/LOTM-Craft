package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.MathUtil;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import de.jakob.lotm.util.minecraft.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class AbilityInfosCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }

        BeyonderPlayer beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);
        if(beyonder == null || beyonder.getCurrentPathway() == null || beyonder.getCurrentSequence() == 10) {
            player.sendMessage("§cYou must be a Beyonder to use this command.");
            return true;
        }

        if(args.length > 1) {
            player.sendMessage("§cInvalid usage! Use §6/abilityinfos [sequence]");
            return true;
        }

        boolean showAll = args.length != 1;

        if(!showAll && !MathUtil.isInteger(args[0])) {
            player.sendMessage("§cInvalid usage! Use §6/abilityinfos [sequence]");
            return true;
        }

        int sequence = !showAll ? Integer.parseInt(args[0]) : 10;
        if(sequence < beyonder.getCurrentSequence()) {
            player.sendMessage("§cYou can't view abilities from higher sequences.");
            return true;
        }

        List<Ability> abilities = beyonder.getAbilities().stream().filter(ability -> ability.getSequence() >= beyonder.getCurrentSequence() && ability.getAbilityType() == AbilityType.SEQUENCE_PROGRESSION).sorted(Comparator.comparing(Ability::getSequence).reversed()).toList();
        if(!showAll) {
            abilities = abilities.stream().filter(a -> a.getSequence() == sequence).toList();
        }

        displayAbilityInfos(player, abilities);

        return true;
    }

    public static void displayAbilityInfos(Player player, List<Ability> abilities) {
        for(Ability ability : abilities) {
            TextUtil.displayAbilityInfoWithCorrectLineBreaks(player, ability.getDescription());
        }
    }
}
