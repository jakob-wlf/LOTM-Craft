package de.jakob.lotm.pathways.impl.hunter;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.impl.hunter.abilities.*;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RedPriestPathway extends Pathway {

    public RedPriestPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_AXE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_hunter"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                        new Trap(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Trap", Material.TRIPWIRE_HOOK, "Lay a trap that sets off when an enemy steps on it", "trap")
                },
                8, new Ability[] {
                        new Provoking(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Provoking", Material.BONE, "Angers and weakens nearby enemies, making them more vulnerable to attacks.", "provoking"),
                },
                7, new Ability[]{
                        new FireResistance(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Fire Resistance", Material.BLAZE_POWDER, "Gain immunity to fire and lava", "fire_resistance"),
                        new FlamingHit(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Flaming Hit", Material.BLAZE_POWDER, "All your attacks set enemies ablaze, causing additional fire damage.", "flaming_hit"),
                        new Pyrokinesis(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Pyrokinesis", Material.FIRE_CHARGE, "Manipulate flames in a multitude of ways.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current fire spell.§r", "pyrokinesis")
                },
                6, new Ability[] {
                        new WeaponTransformation(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Weapon Transformation", Material.BLAZE_POWDER, "When using Fireball or Blazing spear, press §6Shift§7 to transform and fly with them.", "weapon_transformation")
                },
                5, new Ability[] {
                        new Cull(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Cull", Material.NETHERITE_SWORD, "A toggleable ability that greatly enhances your attacks but drains spirituality.", "cull"),
                },
                4, new Ability[] {
                        new FireMastery(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Fire Mastery", Material.BLAZE_ROD, "Manipulate flames to an even greater extent.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current fire technique.§r", "fire_mastery"),
                        new SteelMastery(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Steel Mastery", Material.IRON_INGOT, "Gain various Steel related spells.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current steel technique.§r", "steel_mastery"),
                        new ChainOfCommand(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Chain of Command", Material.CHAIN, "Designate entities as your subordinates, allowing them to share your damage and assist in battle.", "chain_of_command")
                },
                3, new Ability[] {
                },
                2, new Ability[] {
                },
                1, new Ability[] {
                }
        ));
    }

    @Override
    public String getURL() {
        return "http://textures.minecraft.net/texture/cc7efd6c1e5c37408b79af5e9bfebd7a56ca5c970e7f861f1e555f4b5ef0f7c6";
    }

    @Override
    protected void initPotIngredients() {
        potionIngredients.put(9, new ItemStack[] {
                ItemsUtil.wolfFang(),
                ItemsUtil.redWine(),
                new ItemStack(Material.POPPY),
                new ItemStack(Material.OAK_LEAVES),
                new ItemStack(Material.SWEET_BERRIES)
        });

        potionIngredients.put(8, new ItemStack[] {
                ItemsUtil.boarTusk(),
                new ItemStack(Material.HONEY_BOTTLE),
                new ItemStack(Material.SEAGRASS),
                new ItemStack(Material.GLOW_BERRIES)
        });

        potionIngredients.put(7, new ItemStack[] {
                ItemsUtil.salamanderGland(),
                new ItemStack(Material.MAGMA_CREAM),
                new ItemStack(Material.LAVA_BUCKET),
                new ItemStack(Material.GHAST_TEAR)
        });
        potionIngredients.put(6, new ItemStack[] {
                ItemsUtil.spiderEyes(),
                new ItemStack(Material.SPIDER_EYE),
                new ItemStack(Material.HONEYCOMB),
                new ItemStack(Material.GOLDEN_APPLE)
        });
        potionIngredients.put(5, new ItemStack[] {
                ItemsUtil.wolfClaws(),
                ItemsUtil.wolfBlood(),
                ItemsUtil.wolfFang(),
                new ItemStack(Material.SPRUCE_SAPLING)
        });
    }

    @Override
    public double optimalDistance(int sequence, double health) {
        if(health < 2)
            return 25;
        else return switch (sequence) {
            case 7, 6, 5 -> 9;
            case 4, 3, 2, 1 -> 12;
            default -> 1;
        };
    }
}
