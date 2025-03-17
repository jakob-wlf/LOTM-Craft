package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.pathways.impl.moon.impl.potions.*;
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
public class PotionCreation extends SelectableAbility {

    private final Set<Beyonder> casting = new HashSet<>();

    public PotionCreation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Shrinking Potion", "Growth Potion", "Speed Potion", "Sun Water", "Fire Breathing Potion", "Explosive Potion"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 22,
                1, 22,
                2, 22,
                3, 22,
                4, 22,
                5, 22
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
            case 0 -> castShrinkingPotion(beyonder);
            case 1 -> castGrowthPotion(beyonder);
            case 2 -> castSpeedPotion(beyonder);
            case 3 -> castSunWater(beyonder);
            case 4 -> castFireBreathingPotion(beyonder);
            case 5 -> castExplosivePotion(beyonder);
        }
    }

    private void castExplosivePotion(Beyonder beyonder) {
        MysticalPotion potion = new ExplosivePotion("Explosive Potion", "§8", Color.fromRGB(150, 117, 117), beyonder.getCurrentMultiplier(), beyonder.isGriefingEnabled());

        if(!(beyonder instanceof BeyonderPlayer)) {
            potion.onDrink(beyonder.getEntity());
            return;
        }

        ItemStack item = potion.getPotion();
        spawnAnimation(beyonder, item);
    }

    private void castFireBreathingPotion(Beyonder beyonder) {
        MysticalPotion potion = new FireBreathPotion("Fire Breathing Potion", "§c", Color.fromRGB(245, 47, 47), beyonder.getCurrentMultiplier());

        if(!(beyonder instanceof BeyonderPlayer)) {
            potion.onDrink(beyonder.getEntity());
            return;
        }

        ItemStack item = potion.getPotion();
        spawnAnimation(beyonder, item);
    }

    private void castSunWater(Beyonder beyonder) {
        MysticalPotion potion = new SunWater("Sun Water", "§6", Color.fromRGB(240, 182, 48), beyonder.getCurrentMultiplier());

        if(!(beyonder instanceof BeyonderPlayer)) {
            potion.onDrink(beyonder.getEntity());
            return;
        }

        ItemStack item = potion.getPotion();
        spawnAnimation(beyonder, item);
    }

    private void castSpeedPotion(Beyonder beyonder) {
        MysticalPotion potion = new SpeedPotion("Speed Potion", "§b", Color.fromRGB(125, 133, 250));

        if(!(beyonder instanceof BeyonderPlayer)) {
            potion.onDrink(beyonder.getEntity());
            return;
        }

        ItemStack item = potion.getPotion();
        spawnAnimation(beyonder, item);
    }

    private void castGrowthPotion(Beyonder beyonder) {
        MysticalPotion potion = new GrowthPotion("Growth Potion", "§d", Color.fromRGB(217, 125, 250));

        if(!(beyonder instanceof BeyonderPlayer)) {
            potion.onDrink(beyonder.getEntity());
            return;
        }

        ItemStack item = potion.getPotion();
        spawnAnimation(beyonder, item);
    }

    private void castShrinkingPotion(Beyonder beyonder) {
        MysticalPotion potion = new ShrinkingPotion("Shrinking Potion", "§b", Color.fromRGB(3, 252, 169));

        if(!(beyonder instanceof BeyonderPlayer)) {
            potion.onDrink(beyonder.getEntity());
            return;
        }

        ItemStack item = potion.getPotion();
        spawnAnimation(beyonder, item);
    }

    public void spawnAnimation(Beyonder beyonder, ItemStack potion) {
        Player player = (Player) beyonder.getEntity();
        World world = player.getWorld();
        Location loc = player.getLocation().add(player.getEyeLocation().getDirection().multiply(2)).add(0, 2, 0);

        world.playSound(loc, Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
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
