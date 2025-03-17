package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

@NoArgsConstructor
public class DawnWeaponry extends Ability {

    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Sword of Dawn", "Axe of Dawn", "Scythe of Dawn"};

    public DawnWeaponry(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

    }

    @Override
    public void leftClick(Beyonder beyonder) {
        useAbility(beyonder);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        useAbility(beyonder);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        if(beyonder.removeSpirituality(20))
            castAbility(beyonder);
    }

    private void castAbility(Beyonder beyonder) {
        switch (selectedAbilities.get(beyonder)) {
            default -> giveSwordOfDawn(beyonder);
            case 1 -> giveAxeOfDawn(beyonder);
            case 2 -> giveScytheOfDawn(beyonder);
        }
    }

    private void giveScytheOfDawn(Beyonder beyonder) {
        Ability ability = beyonder.getAbilities().stream().filter(a -> a.getId().equalsIgnoreCase("scythe_of_dawn")).findFirst().orElse(null);

        if(ability == null)
            return;

        ItemStack item = ability.getItem();

        if(!(beyonder.getEntity() instanceof Player player))
            return;

        if(Arrays.stream(player.getInventory().getContents()).anyMatch(item::isSimilar))
            return;

        if(!beyonder.removeSpirituality(10))
            return;

        player.playSound(player, Sound.ITEM_TRIDENT_RETURN, 1, .7f);

        player.getInventory().addItem(item);
    }

    private void giveSwordOfDawn(Beyonder beyonder) {
        Ability ability = beyonder.getAbilities().stream().filter(a -> a.getId().equalsIgnoreCase("sword_of_dawn")).findFirst().orElse(null);
        if(ability == null)
            return;

        ItemStack item = ability.getItem();

        if(!(beyonder.getEntity() instanceof Player player))
            return;

        if(Arrays.stream(player.getInventory().getContents()).anyMatch(item::isSimilar))
            return;

        if(!beyonder.removeSpirituality(10))
            return;

        player.playSound(player, Sound.ITEM_TRIDENT_RETURN, 1, .7f);

        player.getInventory().addItem(item);
    }

    private void giveAxeOfDawn(Beyonder beyonder) {
        Ability ability = beyonder.getAbilities().stream().filter(a -> a.getId().equalsIgnoreCase("axe_of_dawn")).findFirst().orElse(null);

        if(ability == null)
            return;

        ItemStack item = ability.getItem();

        if(!(beyonder.getEntity() instanceof Player player))
            return;

        if(Arrays.stream(player.getInventory().getContents()).anyMatch(item::isSimilar))
            return;

        if(!beyonder.removeSpirituality(10))
            return;

        player.playSound(player, Sound.ITEM_TRIDENT_RETURN, 1, .7f);

        player.getInventory().addItem(item);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) >= abilities.length)
            selectedAbilities.replace(beyonder, 0);
    }

    @Override
    public void sneakRightClick(Beyonder beyonder) {
        rightClick(beyonder);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + abilities[selectedAbilities.get(beyonder)]));
    }
}

