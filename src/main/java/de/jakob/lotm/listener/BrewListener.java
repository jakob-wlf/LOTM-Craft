package de.jakob.lotm.listener;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.Potions;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BrewListener implements Listener {

    Random random = new Random();

    //TODO: Add sounds
    @EventHandler
    public void onClickCauldron(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null || event.getClickedBlock().getType() != org.bukkit.Material.CAULDRON)
            return;

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        MetadataValue metadataValue = event.getClickedBlock().getMetadata("brewing").stream().filter(m -> Objects.equals(m.getOwningPlugin(), LOTM.getInstance())).findFirst().orElse(null);

        if(item == null && metadataValue == null)
            return;

        if(metadataValue == null && item.getType() == Material.POTION) {
            event.getClickedBlock().setMetadata("brewing", new FixedMetadataValue(LOTM.getInstance(), new ArrayList<ItemStack>()));
            player.getInventory().removeItem(item);

            new BukkitRunnable() {

                int counter = 0;

                @Override
                public void run() {
                    if(!event.getClickedBlock().hasMetadata("brewing")) {
                        cancel();
                        return;
                    }

                    if(counter > 20 * 60) {
                        event.getClickedBlock().getWorld().createExplosion(event.getClickedBlock().getLocation(), 3, false, false);
                        cancel();
                        event.getClickedBlock().removeMetadata("brewing", LOTM.getInstance());
                        return;
                    }

                    ParticleSpawner.displayParticles(player.getWorld(), Particle.WITCH, event.getClickedBlock().getLocation().add(0.5, 0, 0.5), 50, .2, .2, .2, 0, 120);

                    counter += 5;
                }
            }.runTaskTimer(LOTM.getInstance(), 0, 5);

            return;
        }

        if(metadataValue == null)
            return;

        event.setCancelled(true);

        for(Pathway pathway : LOTM.getInstance().getPathways()) {
            for(Ability ability : pathway.getAbilities()) {
                if(item != null && item.isSimilar(ability.getItem())) {
                    return;
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(item == null)
                return;
            addItem(event.getClickedBlock(), item, metadataValue);
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null && ItemsUtil.isSimilar(contents[i], item)) {
                    if(item.getAmount() > 1)
                        item.setAmount(item.getAmount() - 1);
                    else
                        player.getInventory().setItem(i, null);
                    break; // Exit loop after removing one instance
                }
            }
        } else {
            brewPotion(event.getClickedBlock(), metadataValue);
        }
    }

    private void brewPotion(@NotNull Block block, @NotNull MetadataValue metadataValue) {
        ArrayList<ItemStack> items = new ArrayList<>();

        if (metadataValue.value() instanceof List<?> list) {
            for (Object obj : list) {
                if (obj instanceof ItemStack ingredient) {
                    items.add(ingredient);
                }
            }
        }

        block.removeMetadata("brewing", LOTM.getInstance());

        ItemStack potion = null;
        for(Pathway pathway : LOTM.getInstance().getPathways()) {

            for (int i = 1; i < 10; i++) {
                ItemStack[] ingredients = pathway.getPotionIngredients().get(i);

                if (ingredients.length != items.size())
                    continue;

                boolean equal = true;
                List<Integer> matchedIndices = new ArrayList<>();

                for (ItemStack ingredient : ingredients) {
                    boolean found = false;
                    for (int j = 0; j < items.size(); j++) {
                        if (!matchedIndices.contains(j) && ItemsUtil.isSimilar(ingredient, items.get(j))) {
                            matchedIndices.add(j);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        equal = false;
                        break;
                    }
                }


                if(!equal) {
                    if(items.size() == 1) {
                        if(ItemsUtil.isSimilar(items.get(0), pathway.getCharacteristicForSequence(i)))
                            equal = true;
                    }
                }

                if (equal) {
                    potion = pathway.getPotions().getPotion(i);
                    break;
                }
            }
        }

        if(potion == null) {
            potion = Potions.createWrongPotion();
        }

        block.getWorld().createExplosion(block.getLocation(), 3, false, false);
        block.getWorld().dropItem(block.getLocation().add(0.5, 1, 0.5), potion).setVelocity((new Vector(random.nextDouble(-0.3, 0.3), random.nextDouble(0.3, .6), random.nextDouble(-0.3, 0.3))).normalize().multiply(.6));

    }



    private void addItem(@NotNull Block block, ItemStack item, @NotNull MetadataValue metadataValue) {
        ArrayList<ItemStack> items = new ArrayList<>();

        if (metadataValue.value() instanceof List<?> list) {
            for (Object obj : list) {
                if (obj instanceof ItemStack ingredient) {
                    items.add(ingredient);
                } else {
                    block.removeMetadata("brewing", LOTM.getInstance());
                    return;
                }
            }
        } else {
            block.removeMetadata("brewing", LOTM.getInstance());
            return;
        }

        items.add(item);
        block.setMetadata("brewing", new FixedMetadataValue(LOTM.getInstance(), items));

        displayItems(block, item);
    }

    private void displayItems(Block block, ItemStack item) {

        ItemDisplay itemDisplay = (ItemDisplay) block.getWorld().spawnEntity(block.getLocation().add(random.nextDouble(-1.5, 1.5), 1 + random.nextDouble(1.5), random.nextDouble(-1.5, 1.5)), EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(item);
        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(0.5);
        itemDisplay.setTransformation(transformation);
        itemDisplay.addScoreboardTag("brewingItem" + block.getX() + block.getY() + block.getZ());

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(!block.hasMetadata("brewing")) {
                    block.getWorld().getNearbyEntities(block.getLocation(), 3, 3, 3).stream().filter(e -> e.getScoreboardTags().contains("brewingItem" + block.getX() + block.getY() + block.getZ())).forEach(Entity::remove);
                    cancel();
                    return;
                }

                ParticleSpawner.displayParticles(itemDisplay.getWorld(), Particle.DUST, itemDisplay.getLocation(), 2, .2, .2, .2, 0, ParticleUtil.coloredDustOptions[random.nextInt(ParticleUtil.coloredDustOptions.length)], 120);

                counter += 5;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 5);
    }
}
