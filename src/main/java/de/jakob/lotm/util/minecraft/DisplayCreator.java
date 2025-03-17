package de.jakob.lotm.util.minecraft;

import de.jakob.lotm.LOTM;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
public class DisplayCreator {
    public static void spawnIronSwordDisplay(Location location, long duration, float scale, Vector direction) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        World world = location.getWorld();

        ItemStack swordStack = new ItemStack(Material.IRON_SWORD);

        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(swordStack);

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(scale);
        itemDisplay.setTransformation(transformation);

        Vector dir = direction.clone().normalize();

        location.setDirection(dir);

        itemDisplay.teleport(location);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!itemDisplay.isDead()) {
                    itemDisplay.remove();
                }
            }
        }.runTaskLater(LOTM.getInstance(), duration);
    }

}

