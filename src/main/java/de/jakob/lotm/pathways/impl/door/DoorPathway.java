package de.jakob.lotm.pathways.impl.door;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.abilities.common_abilities.SpiritVision;
import de.jakob.lotm.pathways.impl.door.abilities.*;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DoorPathway extends Pathway {
    public DoorPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[] {
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_door"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                        new DoorOpening(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Door Opening", Material.BIRCH_DOOR, "Pass through solid walls effortlessly.\n", "door_opening")
                },
                8, new Ability[] {
                        new SpellCasting(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Spell Casting", Material.PINK_DYE, "Cast several relatively weak magic spells", "spell_casting")
                },
                7, new Ability[] {
                        new SpiritVision(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Spirit Vision", Material.ENDER_EYE, "Reveal the health of nearby entities, granting you insight into your enemies and friends.", "spirit_vision"),
                },
                6, new Ability[] {
                        new Recording(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Recording", Material.BOOK, "Record and replicate the powers of Beyonders when they use them.", "recording")
                },
                5, new Ability[] {
                        new Blink(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Blink", Material.ENDER_PEARL, "Instantly teleport a short distance to evade attacks or reposition in battle.", "blink"),
                        new TravelersDoor(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Travelers Door", Material.WARPED_DOOR, """
                                Open a mystical door that allows instant teleportation.
                                §k§r§7- §6Left-Click: §7Enter teleportation state directly.§r
                                §k§r§7- §6Sneak Right-Click: §7Specify coordinates before opening the door.§r""", "travelers_door")
                },
                4, new Ability[] {
                        new SpaceConcealment(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Space Concealment", Material.IRON_DOOR, """
                                Separate spaces to protect yourself or trap your enemies.
                                §k§r§7- §6Left-Click: §7Cast the concealment.§r
                                §k§r§7- §6Right-Click: §7Change the target area.§r
                                §k§r§7- §6Sneak Right-Click: §7Adjust the radius of the space.§r
                                §k§r§7- §6Sneak Left-Click: §7Remove all separated spaces.§r""", "space_concealment"),
                        new Exile(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Exile", Material.CRIMSON_DOOR, "Banish enemies to a chaotic space, either ending the battle swiftly or giving you time to escape.", "exile"),
                        new DoorSubstitution(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Door Substitution", Material.ACACIA_DOOR, "Create door substitutions that trigger when you get damaged..", "door_substitution"),

                },
                3, new Ability[] {
                        new Wandering(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Wandering", Material.LAPIS_LAZULI, "Travel between all the different realms.", "wandering"),
                        new Conceptualization(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Conceptualization", Material.NETHER_STAR, "Conceptualize yourself to quickly traverse the world and wander around different realms.", "conceptualization"),
                        new Seal(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Seal", Material.OXIDIZED_COPPER_DOOR, "Seal the target. If they are weaker you can send them to an unknown location, unable for them to escape", "seal"),
                },
                2, new Ability[] {
                        new Replicating(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Replicating", Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, "Replicate the powers of Beyonders when they use them and use them as your own.", "replicating"),
                        new SpaceCollapse(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Space Collapse", Material.ECHO_SHARD, "Tear Space and have it become swallowed by the void, causing everything within the affected area to gradually collapse and shatter.", "space_collapse"),
                        new BlackHole(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Black Hole", Material.BLACK_DYE, "Create a black hole that pulls in all entities and blocks within a certain radius.", "black_hole"),
                        new SpaceDistortion(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Space Distortion", Material.ENDER_EYE, "Create a distortion in space that distorts the attacks of beyonders and randomly teleports entities that stand close to it", "space_distortion"),
                },
                1, new Ability[] {
                }
        ));
    }

    @Override
    protected void initPotIngredients() {
        potionIngredients.put(9, new ItemStack[] {
                new ItemStack(Material.ENDER_PEARL),
                new ItemStack(Material.WRITABLE_BOOK),
                new ItemStack(Material.PAPER),
                new ItemStack(Material.IRON_INGOT)
        });

        potionIngredients.put(8, new ItemStack[] {
                ItemsUtil.phantomWing(),
                new ItemStack(Material.SPRUCE_LEAVES),
                new ItemStack(Material.SHORT_GRASS),
                new ItemStack(Material.POPPY)
        });

        potionIngredients.put(7, new ItemStack[] {
                ItemsUtil.squidBlood(),
                new ItemStack(Material.AMETHYST_SHARD),
                new ItemStack(Material.OBSIDIAN)
        });
        potionIngredients.put(6, new ItemStack[] {
                ItemsUtil.wraithArtifact(),
                new ItemStack(Material.WRITABLE_BOOK),
                new ItemStack(Material.PAPER),
                new ItemStack(Material.GOLDEN_APPLE)
        });
        potionIngredients.put(5, new ItemStack[] {
                ItemsUtil.echoPearl(),
                new ItemStack(Material.COMPASS),
                new ItemStack(Material.CARTOGRAPHY_TABLE),
                new ItemStack(Material.MAP),
        });
    }

    @Override
    public String getURL() {
        return "http://textures.minecraft.net/texture/6c7deaf2d7e72f21fca3a99694481a674f5185c44c3b8f53b0b49310fe61767d";
    }

    @Override
    public double optimalDistance(int sequence, double health) {
        if(health < 2)
            return 25;
        else return switch (sequence) {
            case 6, 5 -> 10;
            case 4, 3, 2, 1 -> 15;
            default -> 1;
        };
    }
}
