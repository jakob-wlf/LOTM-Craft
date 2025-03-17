package de.jakob.lotm.pathways.impl.moon.impl.potions;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Random;

public abstract class MysticalPotion implements Listener {

    protected String name;
    protected String colorPrefix;
    @Getter
    protected Color color;

    protected final Particle.DustOptions dustOptions;
    private final int id = (new Random()).nextInt(999999);

    public MysticalPotion(String name, String colorPrefix, Color color) {
        this.name = name;
        this.colorPrefix = colorPrefix;
        this.color = color;

        dustOptions = new Particle.DustOptions(color, 1);

        LOTM.getInstance().registerListener(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!ItemsUtil.isSimilar(event.getItem(), getPotion())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Remove only one instance of the item
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && ItemsUtil.isSimilar(contents[i], item)) {
                player.getInventory().setItem(i, null);
                break; // Exit loop after removing one instance
            }
        }

        LOTM.getInstance().removeListener(this);
        onDrink(player);
    }

    public abstract void onDrink(LivingEntity entity);

    public ItemStack getPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if(meta == null) return potion;

        meta.setDisplayName(colorPrefix + "§l" + name);
        meta.setColor(color);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(id);
        potion.setItemMeta(meta);
        return potion;
    }

}
