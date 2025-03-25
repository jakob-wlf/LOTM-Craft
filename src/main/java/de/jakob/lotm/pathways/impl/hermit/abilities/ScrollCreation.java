package de.jakob.lotm.pathways.impl.hermit.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.pathways.impl.hermit.abilities.scrolls.*;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class ScrollCreation extends SelectableAbility {

    private final Set<Beyonder> casting = new HashSet<>();

    public ScrollCreation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Storm", "Freezing", "Healing", "Stars", "Flames"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 22,
                1, 22,
                2, 22,
                3, 22,
                4, 22
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        if(casting.contains(beyonder)) {
            return;
        }

        if(!(beyonder instanceof BeyonderPlayer) && (ability == 1 || ability == 0)) {
            ability = random.nextInt(2, 6);
        }

        if(beyonder instanceof BeyonderPlayer)
            casting.add(beyonder);

        switch (ability) {
            case 0 -> castStormScroll(beyonder);
            case 1 -> castFreezeScroll(beyonder);
            case 2 -> castHealingScroll(beyonder);
            case 3 -> castStarScroll(beyonder);
            case 4 -> castFlameScroll(beyonder);
        }
    }

    private void castStormScroll(Beyonder beyonder) {
        MysticalScroll scroll = new StormScroll("Storm Scroll", pathway.getColorPrefix(), Color.fromRGB(30, 120, 255));

        if(!(beyonder instanceof BeyonderPlayer)) {
            scroll.onUse(beyonder.getEntity());
            return;
        }

        ItemStack item = scroll.getItem();
        spawnAnimation(beyonder, item);
    }

    private void castStarScroll(Beyonder beyonder) {
        MysticalScroll scroll = new StarScroll("Star Scroll", pathway.getColorPrefix(), Color.fromRGB(30, 120, 255));

        if(!(beyonder instanceof BeyonderPlayer)) {
            scroll.onUse(beyonder.getEntity());
            return;
        }

        ItemStack item = scroll.getItem();
        spawnAnimation(beyonder, item);
    }

    private void castFlameScroll(Beyonder beyonder) {
        MysticalScroll scroll = new FlameScroll("Flame Scroll", pathway.getColorPrefix(), Color.fromRGB(30, 120, 255));

        if(!(beyonder instanceof BeyonderPlayer)) {
            scroll.onUse(beyonder.getEntity());
            return;
        }

        ItemStack item = scroll.getItem();
        spawnAnimation(beyonder, item);
    }

    private void castHealingScroll(Beyonder beyonder) {
        MysticalScroll scroll = new HealingScroll("Healing Scroll", pathway.getColorPrefix(), Color.fromRGB(255, 199, 69));

        if(!(beyonder instanceof BeyonderPlayer)) {
            scroll.onUse(beyonder.getEntity());
            return;
        }

        ItemStack item = scroll.getItem();
        spawnAnimation(beyonder, item);
    }

    private void castFreezeScroll(Beyonder beyonder) {
        MysticalScroll scroll = new FreezeScroll("Freeze Scroll", pathway.getColorPrefix(), Color.fromRGB(209, 247, 255));

        if(!(beyonder instanceof BeyonderPlayer)) {
            scroll.onUse(beyonder.getEntity());
            return;
        }

        ItemStack item = scroll.getItem();
        spawnAnimation(beyonder, item);
    }

    public void spawnAnimation(Beyonder beyonder, ItemStack potion) {
        Player player = (Player) beyonder.getEntity();
        World world = player.getWorld();
        Location loc = player.getLocation().add(player.getEyeLocation().getDirection().multiply(2)).add(0, 2, 0);

        world.playSound(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

        // Create an ItemDisplay entity for the potion
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(potion);
        itemDisplay.setRotation(0, 0);

        // Create an invisible ArmorStand as a base
        ArmorStand stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setGravity(false);

        LOTM.getInstance().getEntitiesToRemove().add(stand);
        LOTM.getInstance().getEntitiesToRemove().add(itemDisplay);

        new BukkitRunnable() {
            double angle = 0;
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 20 * 5) { // After 3 seconds (60 ticks)
                    itemDisplay.remove();
                    stand.remove();

                    LOTM.getInstance().getEntitiesToRemove().remove(stand);
                    LOTM.getInstance().getEntitiesToRemove().remove(itemDisplay);

                    player.getInventory().addItem(potion);

                    casting.remove(beyonder);

                    cancel();
                    return;
                }

                angle += 10;
                Location baseLoc = player.getLocation().add(player.getEyeLocation().getDirection().multiply(2)).add(0, 1, 0);
                Location newLoc = baseLoc.clone().add(Math.sin(Math.toRadians(angle)) * 0.3, 0.06, Math.cos(Math.toRadians(angle)) * 0.3);
                stand.teleport(newLoc);
                itemDisplay.teleport(newLoc);

                // Apply rotation transformation to ItemDisplay
                Transformation transformation = itemDisplay.getTransformation();
                transformation.getLeftRotation().rotationY((float) Math.toRadians(angle));
                itemDisplay.setTransformation(transformation);

                ParticleSpawner.displayParticles(world, Particle.SOUL_FIRE_FLAME, newLoc, 5, 0.2, 0.2, 0.2, 0, 200);
                ParticleSpawner.displayParticles(world, Particle.ENCHANT, newLoc, 5, 0.2, 0.2, 0.2, 0, 200);

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1); // Runs every tick
    }
}
