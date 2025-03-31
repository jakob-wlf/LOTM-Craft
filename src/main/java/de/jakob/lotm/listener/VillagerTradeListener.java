package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VillagerTradeListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onTradeAcquire(VillagerAcquireTradeEvent event) {
        if(random.nextInt(4) != 0)
            return;

        int sequence = getWeightedRandomNumber();
        Pathway pathway = LOTM.getInstance().getPathways().get(random.nextInt(LOTM.getInstance().getPathways().size()));

        ItemStack item = random.nextInt(4) != 0 ? pathway.getCharacteristicForSequence(sequence) : pathway.getPotions().getPotion(sequence);

        List<MerchantRecipe> trades = new ArrayList<>(event.getEntity().getRecipes());
        MerchantRecipe recipe = new MerchantRecipe(item, 1);
        ItemStack cost1 = new ItemStack(Material.DIAMOND);
        cost1.setAmount(20 / sequence);
        ItemStack cost2 = new ItemStack(Material.EMERALD);
        cost2.setAmount(90 / sequence);
        recipe.addIngredient(cost1);
        recipe.addIngredient(cost2);
        trades.add(recipe);
        event.getEntity().setRecipes(trades);

    }

    private int getWeightedRandomNumber() {
        int totalWeight = 45; // Sum of weights 1+2+3+...+9
        int randomValue = random.nextInt(totalWeight) + 1; // Random number from 1 to 45

        int cumulativeWeight = 0;
        for (int i = 1; i <= 9; i++) {
            cumulativeWeight += i;
            if (randomValue <= cumulativeWeight) {
                return i;
            }
        }
        return 9; // Fallback (shouldn't be reached)
    }
}
