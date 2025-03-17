package de.jakob.lotm.pathways.impl.tyrant;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.impl.tyrant.impl.*;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TyrantPathway extends Pathway {
    public TyrantPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_tyrant"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                },
                8, new Ability[] {
                        new RagingBlows(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Raging Blows", Material.BONE_MEAL, "Unleash a flurry of powerful strikes, overwhelming your enemies with relentless force.", "raging_blows")
                },
                7, new Ability[]{
                        new WaterManipulation(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Water Manipulation", Material.PRISMARINE_CRYSTALS, "Gain several water related spells.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current water spell.§r", "water_manipulation")
                },
                6, new Ability[] {
                        new WindManipulation(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Wind Manipulation", Material.FEATHER, "Gain several wind related spells.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current wind spell.§r", "wind_manipulation")
                },
                5, new Ability[] {
                        new Lightning(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Lightning", Material.LIGHT_BLUE_DYE, "Summon bolts of lightning to strike down your foes with devastating precision.", "lightning"),
                        new SirenSong(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Siren Song", Material.MUSIC_DISC_CREATOR, "Enchant enemies with a mesmerizing melody, weakening them and leaving them vulnerable.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current song effect.§r", "siren_song")
                },
                4, new Ability[] {
                        new Tsunami(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Tsunami", Material.BLUE_DYE, "Release a powerful tsunami that knocks back your foes.\n" +
                                "§k§rr§7- §6Sneak-Right-Click: §7The tsunami will seal the enemies it hits.", "tsunami"),
                        new WaterMastery(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Water Mastery", Material.PRISMARINE_SHARD, "Gain more potent and powerful options for manipulating water.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the current water spell.§r", "water_mastery"),
                        new Roar(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Roar", Material.FIREWORK_STAR, "Unleash a mighty roar.", "roar"),
                        new Earthquake(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Earthquake", Material.BROWN_DYE, "Shake the ground violently, disrupting enemy movement and dealing area damage.", "earthquake"),
                        new Tornado(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Tornado", Material.WHITE_DYE, "Summon raging tornado's.", "tornado")
                },
                3, new Ability[] {
                        new LightningStorm(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Lightning Storm", Material.CYAN_DYE, "Unleash a terrifying lightning storm reducing everything to ashes", "lightning_storm"),
                        new TorrentialDownpour(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Torrential Downpour", Material.WATER_BUCKET, "Create a Torrential Downpour covering a large area and damaging all nearby targets.", "torrential_downpour"),
                        new LightningBranch(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Branching Lightning", Material.LAPIS_LAZULI, "Create a lightning that branches out and damages all it touches.", "branching_lightning"),
                        new Thunderclap(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Thunderclap", Material.RAW_IRON, "Create a thunderclap that knocks enemies away.", "thunderclap")

                },
                2, new Ability[] {
                        new CalamityCreation(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Calamity Creation", Material.NETHER_BRICK, "Create various calamities. (Use /togglegriefing for destruction)", "calamity_creation")
                },
                1, new Ability[] {
                        new LightningTransformation(this, 1, AbilityType.SEQUENCE_PROGRESSION, "Lightning Transformation", Material.GLOW_INK_SAC, "Transform yourself into Lightning to travel at lightning fast speeds.", "lightning_transformation")
                }
        ));
    }

    @Override
    protected void initPotIngredients() {
        potionIngredients.put(9, new ItemStack[] {
                ItemsUtil.drownedLung(),
                new ItemStack(Material.KELP),
                new ItemStack(Material.WATER_BUCKET),
                new ItemStack(Material.COD)
        });

        potionIngredients.put(8, new ItemStack[] {
                new ItemStack(Material.TRIDENT),
                new ItemStack(Material.WATER_BUCKET),
                new ItemStack(Material.SEAGRASS),
                new ItemStack(Material.PHANTOM_MEMBRANE)
        });

        potionIngredients.put(7, new ItemStack[] {
                ItemsUtil.captainsLogBook(),
                new ItemStack(Material.WATER_BUCKET),
                new ItemStack(Material.PUFFERFISH),
                new ItemStack(Material.COMPASS)
        });
        potionIngredients.put(6, new ItemStack[] {
                ItemsUtil.blueFeather(),
                new ItemStack(Material.FEATHER),
                new ItemStack(Material.TROPICAL_FISH),
                new ItemStack(Material.NAUTILUS_SHELL),
                new ItemStack(Material.WATER_BUCKET),
        });
        potionIngredients.put(5, new ItemStack[] {
                ItemsUtil.songCrystal(),
                new ItemStack(Material.ECHO_SHARD),
                new ItemStack(Material.HEART_OF_THE_SEA),
                new ItemStack(Material.GLOW_INK_SAC)
        });
    }

    @Override
    public String getURL() {
        return "http://textures.minecraft.net/texture/e01e040cb01cf2cce4428358ae31d2e266207c47cb3ad139709c620130dc8ad4";
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
