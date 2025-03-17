package de.jakob.lotm.pathways.impl.twilight_giant;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.impl.twilight_giant.impl.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class TwilightGiantPathway extends Pathway {
    public TwilightGiantPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_SWORD, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_warrior"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                },
                8, new Ability[] {
                        new Resistances(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Supernatural Resistances", Material.IRON_CHESTPLATE, "Grants immunity to certain harmful effects, making you more resilient in battle.\n", "resistances")
                },
                7, new Ability[]{
                },
                6, new Ability[] {
                        new LightOfDawn(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Light of Dawn", Material.GLOWSTONE_DUST, "Conjure a brilliant light that dispels all darkness and deals area-of-effect damage to nearby enemies.", "light_of_dawn"),
                        new DawnWeaponry(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Dawn Weaponry", Material.GOLDEN_HORSE_ARMOR, "Summon powerful weapons forged from the light of dawn.\n" +
                                "§k§r§7- §6Right-Click: §7Switch the selected weapon.§r", "dawn_weaponry"),
                        new AxeOfDawn(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Axe of Dawn", Material.GOLDEN_AXE, "A mighty axe that channels the strength of dawn.\n" +
                                "§k§r§7- §6Right-Click: §7Smash the ground, causing a shockwave around you.§r", "axe_of_dawn"),
                        new SwordOfDawn(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Sword of Dawn", Material.GOLDEN_SWORD, "A radiant blade imbued with the power of light.\n" +
                                "§k§r§7- §6Right-Click: §7Release a Dawn Tornado, striking enemies in its path.§r", "sword_of_dawn"),
                        new ScytheOfDawn(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Scythe of Dawn", Material.GOLDEN_HOE, "A deadly scythe that carves through foes with light.\n" +
                                "§k§r§7- §6Right-Click: §7Release a sweeping arc of dawn energy that damages enemies.§r", "scythe_of_dawn"),
                        new DawnArmor(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Armor of Dawn", Material.GOLDEN_CHESTPLATE, "Conjure a golden suit of armor that shields you from harm.", "dawn_armor"),
                        new RadiantSmite(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Radiant Smite", Material.GOLD_INGOT, "Smite down on your enemies with the Light of Dawn", "radiant_smite")
                },
                5, new Ability[] {
                        new Protection(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Protection", Material.CHAINMAIL_CHESTPLATE, "Create a Dawn Barrier that protects you and your allies from incoming attacks.", "protection"),
                        new BastionOfLight(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Bastion of Light", Material.RAW_GOLD, "Create a Bastion of Light, strengthening your attacks and healing you.", "bastion_of_light")
                },
                4, new Ability[] {
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
        return "http://textures.minecraft.net/texture/8d7ed449b85de12551e01fefb1819840915e215ad134e15ecd270f0fd80bd324";
    }

    @Override
    public double optimalDistance(int sequence, double health) {
        if(health < 2)
            return 25;
        else return switch (sequence) {
            case 7, 6, 5 -> 5;
            case 4, 3, 2, 1 -> 12;
            default -> 1;
        };
    }
}
