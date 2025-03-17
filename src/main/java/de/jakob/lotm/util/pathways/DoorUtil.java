package de.jakob.lotm.util.pathways;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.VectorUtil;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DoorUtil {
    private static final Particle.DustOptions dustOptionsAqua = new Particle.DustOptions(Color.AQUA, 1.5f);
    private static final Particle.DustOptions dustOptionsMagenta = new Particle.DustOptions(Color.fromRGB(185, 66, 207), 1.5f);

    public static ItemDisplay createDoor(DoorType doorType, Location location, double size, Vector dir, int duration, boolean particles, boolean opened) {
        return createDoor(doorType, location, size, dir, duration, particles, opened, false);
    }

    //TODO: Make door centered and rotate it a bit to better display it. See DeathUtils
    //TODO: Different texture for door

    public static ItemDisplay createDoor(DoorType doorType, Location location, double size, Vector dir, int duration, boolean particles, boolean opened, boolean onlyShowToCertainPlayers, Player... players) {
        size *= 2;

        World world = location.getWorld();
        if (world == null) return null;


        if (opened) {
            dir = VectorUtil.rotateAroundY(dir, 90);
        }

        Material material = getDoorMaterial(doorType);
        Color color = getDoorColor(doorType);
        ItemDisplay itemDisplay = createItemDisplay(world, location, material, size, dir, color, onlyShowToCertainPlayers, players);

        destroyDisplayAfterDuration(itemDisplay, duration);
        if (particles) displayParticles(itemDisplay, duration, color, onlyShowToCertainPlayers, players);

        if (opened) {
            Vector offsetDir = VectorUtil.rotateAroundY(dir, -90).normalize();
            Location glassLocation = location.clone().add(dir.normalize().multiply(0.35 * size))
                    .add(VectorUtil.rotateAroundY(dir, 90).normalize().multiply(size * -0.31))
                    .subtract(0, 0.16, 0);
            ItemDisplay glassDisplay = createGlassPane(world, glassLocation, size * 0.75, size * 1.22, offsetDir, duration, itemDisplay, onlyShowToCertainPlayers, players);
            if(particles)
                displayParticles(glassDisplay, duration, Color.fromRGB(233, 137, 250), onlyShowToCertainPlayers, players);

        }

        LOTM.getInstance().getEntitiesToRemove().add(itemDisplay);

        return itemDisplay;
    }

    private static Material getDoorMaterial(DoorType doorType) {
        return switch (doorType) {
            case WOODEN -> Material.OAK_DOOR;
            case IRON -> Material.IRON_DOOR;
            case JUNGLE -> Material.JUNGLE_DOOR;
            case ACACIA -> Material.ACACIA_DOOR;
            case BIRCH -> Material.BIRCH_DOOR;
            case DARK_OAK -> Material.DARK_OAK_DOOR;
            case CRIMSON -> Material.CRIMSON_DOOR;
            case WARPED -> Material.WARPED_DOOR;
        };
    }
    
    private static Color getDoorColor(DoorType doorType) {
        return switch (doorType) {

            case WOODEN, JUNGLE -> Color.fromRGB(242, 166, 85);
            case IRON -> Color.SILVER;
            case ACACIA -> Color.fromRGB(242, 135, 85);
            case BIRCH -> Color.fromRGB(245, 190, 132);
            case DARK_OAK -> Color.fromRGB(166, 104, 46);
            case CRIMSON -> Color.fromRGB(233, 137, 250);
            case WARPED -> Color.AQUA;
        };
    }

    private static ItemDisplay createItemDisplay(World world, Location location, Material material, double size, Vector dir, Color color, boolean onlyShowToCertainPlayers, Player... players) {
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);

        itemDisplay.setItemStack(new ItemStack(material));
        itemDisplay.setGlowing(true);
        itemDisplay.setGlowColorOverride(color);
        itemDisplay.setBrightness(new Display.Brightness(15, 15));
        if(onlyShowToCertainPlayers) {
            itemDisplay.setVisibleByDefault(false);
            for(Player player : players) {
                player.showEntity(LOTM.getInstance(), itemDisplay);
            }
        }

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(size, size * 1.4, size);

        applyRotation(transformation, dir);
        itemDisplay.setTransformation(transformation);

        return itemDisplay;
    }

    private static void applyRotation(Transformation transformation, Vector dir) {
        Vector3f forward = new Vector3f((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        Vector3f defaultForward = new Vector3f(0, 0, 1);

        Quaternionf rotation = new Quaternionf().rotateTo(defaultForward, forward);
        transformation.getLeftRotation().set(rotation.x, rotation.y, rotation.z, rotation.w);
    }

    private static ItemDisplay createGlassPane(World world, Location location, double scaleX, double scaleY, Vector dir, int duration, ItemDisplay dependentOn, boolean onlyShowToCertainPlayers, Player... players) {
        ItemDisplay glassDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);

        glassDisplay.setItemStack(new ItemStack(Material.GLASS_PANE));
        glassDisplay.setGlowing(true);
        glassDisplay.setGlowColorOverride(Color.fromRGB(233, 137, 250));

        if(onlyShowToCertainPlayers) {
            glassDisplay.setVisibleByDefault(false);
            for(Player player : players) {
                player.showEntity(LOTM.getInstance(), glassDisplay);
            }
        }

        Transformation transformation = glassDisplay.getTransformation();
        transformation.getScale().set(scaleX, scaleY, scaleX);

        applyRotation(transformation, dir);
        glassDisplay.setTransformation(transformation);

        LOTM.getInstance().getEntitiesToRemove().add(glassDisplay);
        destroyDisplayAfterDuration(glassDisplay, duration);

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
        }.runTaskTimer(LOTM.getInstance(), 0, 3);

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

    private static void displayParticles(ItemDisplay itemDisplay, int duration, Color color, boolean onlyShowToCertainPlayers, Player... players) {
        World world = itemDisplay.getLocation().getWorld();
        if (world == null) return;

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1f);

        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                timer += 2;
                if (!itemDisplay.isValid() || timer >= duration) {
                    cancel();
                    return;
                }

                Location location = itemDisplay.getLocation();
                if(!onlyShowToCertainPlayers)
                    world.spawnParticle(Particle.DUST, location, 3, 0.25, 0.7, 0.25, 0, dustOptions);
                else {
                    for(Player player : players) {
                        player.spawnParticle(Particle.DUST, location, 3, 0.25, 0.7, 0.25, 0, dustOptions);
                    }
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 2);
    }

    public static void displayDefaultTeleportParticles(World world, Location location) {
        world.spawnParticle(Particle.DUST, location, 14, 0.3, 0.75, 0.3, 0, dustOptionsAqua);
        world.spawnParticle(Particle.DUST, location, 14, 0.3, 0.75, 0.3, 0, dustOptionsMagenta);
        world.spawnParticle(Particle.LARGE_SMOKE, location, 10, 0.3, 0.75, 0.3, 0);
    }

    public enum DoorType {
        WOODEN,
        IRON,
        JUNGLE,
        ACACIA,
        BIRCH,
        DARK_OAK,
        CRIMSON,
        WARPED
    }
}


