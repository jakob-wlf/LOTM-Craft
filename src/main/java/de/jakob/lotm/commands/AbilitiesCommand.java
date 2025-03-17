package de.jakob.lotm.commands;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.lotm.BeyonderInventoryHolder;
import de.jakob.lotm.util.lotm.ErrorMessageUtil;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class AbilitiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ErrorMessageUtil.mustBePlayerMessage());
            return true;
        }

        BeyonderPlayer beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);
        if(beyonder == null || beyonder.getCurrentPathway() == null || beyonder.getCurrentSequence() == 10) {
            player.sendMessage("§cYou must be a Beyonder to use this command.");
            return true;
        }

        List<ItemStack> abilityItems = beyonder.getAbilities().stream().sorted(Comparator.comparing(Ability::getSequence).reversed()).filter(Ability::showAbilityIcon).filter(a -> a.getAbilityType() == AbilityType.SEQUENCE_PROGRESSION).map(Ability::getItem).toList();
        List<ItemStack> copiedAbilityItems = beyonder.getAbilities().stream().sorted(Comparator.comparing(Ability::getSequence).reversed()).filter(a -> a.getAbilityType() != AbilityType.SEQUENCE_PROGRESSION).map(Ability::getItem).toList();

        int inventorySize = copiedAbilityItems.size() + 1 + abilityItems.size() < 27 ? 27 : 54;

        Inventory inventory = Bukkit.createInventory(new BeyonderInventoryHolder(), inventorySize, beyonder.getCurrentPathway().getColorPrefix() + "§lAbilities");
        ItemStack grayPane = ItemsUtil.getNameLessItem(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack bluePane = ItemsUtil.getNameLessItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        for(int i = 0; i < inventorySize; i++) {
            inventory.setItem(i, grayPane);
        }

        for(int i = 0; i < abilityItems.size(); i++) {
            inventory.setItem(i, abilityItems.get(i));
        }

        inventory.setItem(abilityItems.size(), bluePane);

        for(int i = 0; i < copiedAbilityItems.size(); i++) {
            if(abilityItems.size() + 1 + i >= inventorySize)
                break;
            inventory.setItem(abilityItems.size() + 1 + i, copiedAbilityItems.get(i));
        }

        player.openInventory(inventory);

        return true;
    }
}
