package de.jakob.lotm.pathways.impl.moon;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.abilities.common_abilities.SpiritVision;
import de.jakob.lotm.pathways.impl.moon.impl.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class MoonPathway extends Pathway {
    public MoonPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[] {
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_moon"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                        new SpiritVision(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Spirit Vision", Material.ENDER_EYE, "Reveal the health of nearby entities, granting you insight into your enemies and friends.", "spirit_vision")
                },
                8, new Ability[] {
                        new Tame(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Tame", Material.STRING, "tame any creature and make it follow and fight for you.", "tame")
                },
                7, new Ability[] {
                        new FallDamageImmunity(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Fall Damage Immunity", Material.IRON_BOOTS, "", "fall_damage_immunity"),
                        new AbyssShackle(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Abyss Shackle", Material.CHAIN, "Restrain enemies with shadowy chains.", "abyss_shackles"),
                        new WingsOfDarkness(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Wings Of Darkness", Material.BLACK_DYE, "Unleash shadowy wings to glide through the air.\n" +
                                "§k§r§7- §6Right-Click: §7Transform them into a swarm of bats, blinding and damaging nearby enemies.§r", "wings_of_darkness"),
                        new ShardsOfDarkness(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Shards of Darknes", Material.FLINT, "Condense darkness into 3 projectiles that quickly launch at your enemy.", "shards_of_darkness")
                },
                6, new Ability[] {
                        new PotionCreation(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Potion Creation", Material.CAULDRON, "Craft mystical potions with unique effects.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the potion you want to create.§r", "potion_creation")
                },
                5, new Ability[] {
                        new LunarBattlefield(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Lunar Battlefield", Material.RED_CANDLE, "Flood the area with the light from the Red Moon strengthening yourself and weakening your enemies", "lunar_battlefield"),
                        new FlashTeleportation(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Flash Teleportation", Material.RED_DYE, "Quickly Teleport around as long as the area is under the influence of the moon.", "flash_teleportation")
                },
                4, new Ability[] {
                        new MoonSubstitution(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Moon Paper Figurine", Material.ARMOR_STAND, "Create up to 5 substitutions that will break when you take damage. (5 minute cooldown)", "moon_substitutions"),
                        new GazeOfDarkness(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Gaze of Darkness", Material.ENDER_EYE, "Create a connection with your target and shroud them in darkness. Any damage you take will partially be reflected upon them.", "gaze_of_darkness"),
                        new BatSwarmTransformation(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Bat Swarm Transformation", Material.INK_SAC, "Transform into a swarm of bats to escape or blind your target", "bat_swarm_transformation"),
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
    protected void initPotIngredients() {

    }

    @Override
    public String getURL() {
        return "http://textures.minecraft.net/texture/402a36e2142fd6b9263b0aabfa1e2420c14e1d64405e29a23749d6a68711dea7";
    }

    @Override
    public double optimalDistance(int sequence, double health) {
        if(health < 2)
            return 25;
        else return switch (sequence) {
            case 6, 5 -> 10;
            case 4, 3, 2, 1 -> 20;
            default -> 0;
        };
    }
}
