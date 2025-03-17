package de.jakob.lotm.pathways;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Potions {

    private final Pathway pathway;
    private final ItemStack[] potions = new ItemStack[10];
    private final ItemStack[] potionsWithoutPrevious = new ItemStack[10];

    private static final String[] possiblePrefixes = new String[] {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"
    };

    public Potions(Pathway pathway) {
        this.pathway = pathway;

        initPotions();
    }

    private void initPotions() {
        for(int i = 1; i < 10; i++) {
            potions[i] = createPotion(pathway, i, true);
            potionsWithoutPrevious[i] = createPotion(pathway, i, false);
        }
    }

    public ItemStack getPotion(int sequence) {
        return potions[sequence];
    }
    public ItemStack getPotion(int sequence, boolean includePrevious) {
        return includePrevious ? potions[sequence] : potionsWithoutPrevious[sequence];
    }

    public static ItemStack createPotion(Pathway pathway, int sequence, boolean includePrevious) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if(meta == null || sequence < 1 || sequence > 9)
            return potion;

        meta.setColor(pathway.getColor());
        meta.setDisplayName(pathway.getColorPrefix() + "§lMystical Potion");
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        int modelData = includePrevious ? 100 : 101;
        meta.setCustomModelData(modelData);

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );

        meta.setLore(List.of(
                "§d§k" + sequence + pathway.getName() + " §r§o§dPotion §kThis is nonsense§r§d."
        ));

        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack createWrongPotion() {
        Random random = new Random();

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if(meta == null)
            return potion;

        meta.setColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        meta.setDisplayName("§" + possiblePrefixes[random.nextInt(possiblePrefixes.length)] + "§lMystical Potion");
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        meta.setCustomModelData(102);

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );

        meta.setLore(List.of(
                "§d§kWrong Potion §r§o§dPotion §kThis is nonsense§r§d."
        ));

        potion.setItemMeta(meta);
        return potion;
    }

}
