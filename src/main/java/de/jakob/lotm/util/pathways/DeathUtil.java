package de.jakob.lotm.util.pathways;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DeathUtil {

    public static ItemDisplay createDoor(Location location, Vector dir, int duration, int riseTime) {
        location.add(0, .5, 0);
        double size = 2.5;

        World world = location.getWorld();
        if (world == null) return null;


        dir = VectorUtil.rotateAroundY(dir, 90);


        Material material = Material.PALE_OAK_DOOR;
        Color color = Color.WHITE;
        ItemDisplay itemDisplay = createItemDisplay(world, location.clone().add(dir.clone().multiply(1.22)), material, size, dir, color, -15, riseTime);
        ItemDisplay itemDisplay2 = createItemDisplay(world, location.clone().add(dir.clone().multiply(-1.22)), material, size, dir, color, 15, riseTime, itemDisplay);

        destroyDisplayAfterDuration(itemDisplay, duration);
        destroyDisplayAfterDuration(itemDisplay2, duration);

        Vector offsetDir = VectorUtil.rotateAroundY(dir, -90).normalize();
        Location glassLocation = location.clone()
                .add(VectorUtil.rotateAroundY(dir, -90).multiply(.8))
                .subtract(0, 0.24, 0);
        createGlassPane(world, glassLocation, size * .8, size * 1.48, offsetDir, duration, riseTime, itemDisplay);

        LOTM.getInstance().getEntitiesToRemove().add(itemDisplay);
        LOTM.getInstance().getEntitiesToRemove().add(itemDisplay2);

        return itemDisplay;
    }

    private static ItemDisplay createItemDisplay(World world, Location location, Material material, double size, Vector dir, Color color, int angle, double riseTime, Entity... depend) {
        if(riseTime != 0) {
            location.subtract(0, 2, 0);
        }
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        ItemsUtil.addTextureComponent("underworld_door", meta);
        itemStack.setItemMeta(meta);

        itemDisplay.setItemStack(itemStack);
        itemDisplay.setGlowing(true);
        itemDisplay.setGlowColorOverride(color);
        itemDisplay.setBrightness(new Display.Brightness(15, 15));

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(size * 1.25, size * 1.7, size * 1.25);

        applyRotation(transformation, dir, angle);
        itemDisplay.setTransformation(transformation);

        if(riseTime != 0) {
            new BukkitRunnable() {
                int counter = 0;
                double step = (2f / riseTime);

                @Override
                public void run() {
                    if(counter >= riseTime){
                        cancel();
                        return;
                    }

                    location.add(0, step, 0);
                    location.setYaw(itemDisplay.getLocation().getYaw());
                    location.setPitch(itemDisplay.getLocation().getPitch());
                    itemDisplay.teleport(location);

                    counter++;
                }
            }.runTaskTimer(LOTM.getInstance(), 0, 0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!itemDisplay.isValid()) {
                    cancel();
                    return;
                }

                for(Entity entity : depend) {
                    if(!entity.isValid()) {
                        itemDisplay.remove();
                        cancel();
                    }
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);

        return itemDisplay;
    }

    private static void applyRotation(Transformation transformation, Vector dir, int angle) {
        dir = VectorUtil.rotateAroundY(dir, angle);
        Vector3f forward = new Vector3f((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        Vector3f defaultForward = new Vector3f(0, 0, 1);

        Quaternionf rotation = new Quaternionf().rotateTo(defaultForward, forward);
        transformation.getLeftRotation().set(rotation.x, rotation.y, rotation.z, rotation.w);
    }

    private static ItemDisplay createGlassPane(World world, Location location, double scaleX, double scaleY, Vector dir, int duration, int riseTime, ItemDisplay dependentOn) {
        if(riseTime != 0) {
            location.subtract(0, 2, 0);
        }

        ItemDisplay glassDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);

        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        ItemsUtil.addTextureComponent("underworld_doorway", meta);
        itemStack.setItemMeta(meta);

        glassDisplay.setItemStack(itemStack);
        glassDisplay.setGlowing(true);
        glassDisplay.setGlowColorOverride(Color.WHITE);
        glassDisplay.setBrightness(new Display.Brightness(15, 15));

        Transformation transformation = glassDisplay.getTransformation();
        transformation.getScale().set(scaleX, scaleY, scaleX);

        applyRotation(transformation, dir, 0);
        glassDisplay.setTransformation(transformation);

        LOTM.getInstance().getEntitiesToRemove().add(glassDisplay);
        destroyDisplayAfterDuration(glassDisplay, duration);

        if(riseTime != 0) {
            new BukkitRunnable() {
                int counter = 0;
                double step = (2f / riseTime);

                @Override
                public void run() {
                    if(counter >= riseTime){
                        cancel();
                        return;
                    }

                    location.add(0, step, 0);
                    location.setYaw(glassDisplay.getLocation().getYaw());
                    location.setPitch(glassDisplay.getLocation().getPitch());
                    glassDisplay.teleport(location);

                    counter++;
                }
            }.runTaskTimer(LOTM.getInstance(), 0, 0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!glassDisplay.isValid()) {
                    cancel();
                    return;
                }

                if (!dependentOn.isValid()) {
                    glassDisplay.remove();
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);

        return glassDisplay;
    }

    private static void destroyDisplayAfterDuration(ItemDisplay itemDisplay, int duration) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!itemDisplay.isValid()) return;

                LOTM.getInstance().getEntitiesToRemove().remove(itemDisplay);
                itemDisplay.remove();
            }
        }.runTaskLater(LOTM.getInstance(), duration);
    }

}
