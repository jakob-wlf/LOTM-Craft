package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import java.util.Random;

public class LootGenerateListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        if(!random.nextBoolean())
            return;

        String lootTableKey = event.getLootTable().getKey().toString();

        // Check if the loot is from a shipwreck
        if (lootTableKey.equals(LootTables.SHIPWRECK_MAP.getKey().toString())) {
            event.getLoot().add(ItemsUtil.captainsLogBook());
        }

        int sequence = getWeightedRandomNumber();
        Pathway pathway = LOTM.getInstance().getPathways().get(random.nextInt(LOTM.getInstance().getPathways().size()));

        ItemStack item = random.nextInt(4) != 0 ? pathway.getCharacteristicForSequence(sequence) : pathway.getPotions().getPotion(sequence);

        event.getLoot().add(item);
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
