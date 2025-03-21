package de.jakob.lotm.pathways.impl.sun;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.impl.sun.impl.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class SunPathway extends Pathway {

    public SunPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(
                Map.of(
                        9, new Ability[]{
                                new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_sun"),
                                new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                                new HolySong(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Holy Song", Material.MUSIC_DISC_PIGSTEP, "Sing a Holy song that boosts you and your allies", "holy_song")
                        },
                        8, new Ability[] {
                                new HolyLight(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Holy Light", Material.GLOWSTONE_DUST, "Call down a light from the sky to purify your enemies.", "holy_light"),
                                new Illuminate(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Illuminate", Material.GOLD_NUGGET, "Illuminate the area with golden light.", "illuminate")
                        },
                        7, new Ability[]{
                                new HolyLightSummoning(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Holy Light Summoning", Material.BLAZE_ROD, "Summon a holy pillar of light from the sky.", "holy_light_summoning"),
                                new CleaveOfPurification(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Cleave of Purification", Material.HONEYCOMB, "Strike a nearby target with holy light.", "cleave_of_purification"),
                                new FireOfLight(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Fire of Light", Material.BLAZE_POWDER, "Create dense and holy flames at a target location.", "fire_of_light"),
                                new HolyOath(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Holy Oath", Material.PAPER, "Strengthen and empower yourself.", "holy_oath"),
                                new SunHalo(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Sun Halo", Material.GOLDEN_APPLE, "Form a sun halo around your head that strengthens and encourages you and your allies.", "sun_halo")

                        },
                        6, new Ability[] {
                                new GodSaysItsEffective(this, 6, AbilityType.SEQUENCE_PROGRESSION, "God says it's effective", Material.BOOK, "Enhance the attacks of yourself and your allies for a short time.", "notar_buff"),
                                new GodSaysItsNotEffective(this, 6, AbilityType.SEQUENCE_PROGRESSION, "God says it's not effective", Material.BOOK, "Weaken the attacks of your enemies for a short time.", "notar_debuff"),
                        },
                        5, new Ability[] {
                                new LightOfPurification(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Light of Purification", Material.GLOWSTONE, "Create an expanding halo of light around you damaging all nearby enemies.", "light_of_purification"),
                                new LightOfHoliness(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Light of Holiness", Material.RAW_GOLD, "Calls forth a pure and blazing hot pillar of Light.", "light_of_holiness"),
                        },
                        4, new Ability[] {
                                new FlaringSun(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Flaring Sun", Material.SUNFLOWER, "Call forth a huge ball of pure Light condensed by countless sacred flames.", "flaring_sun"),
                                new UnshadowedSpear(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Unshadowed Spear", Material.SPECTRAL_ARROW, "Condense an Unshadowed Spear made out of pure Sunlight.", "unshadowed_spear"),
                                new UnshadowedDomain(this, 4, AbilityType.SEQUENCE_PROGRESSION, "Unshadowed Domain", Material.GOLD_BLOCK, "Create a bright blazing Light illuminating every corner in a region.\n" +
                                        "§k§r§7- §6Sneaking with Ability: §7Damage entities in domain.§r", "unshadowed_domain")
                        },
                        3, new Ability[] {
                                new GazeOfPurification(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Gaze of Purification", Material.YELLOW_CANDLE, "As the embodiment of justice anything you gaze at will be purified.", "gaze_of_purification"),
                                new WallOfLight(this, 3, AbilityType.SEQUENCE_PROGRESSION, "Wall of Light", Material.YELLOW_BANNER, "Create an impenetrable wall of light.", "wall_of_light")
                        },
                        2, new Ability[] {
                                new WingsOfLight(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Wings of Light", Material.FEATHER, "Create wings out of pure light, allowing you to fly.", "wings_of_light"),
                                new SpearOfLight(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Spear of Light", Material.SPECTRAL_ARROW, "Summon a spear made from pure sunlight that travels at liht speed and restrains targets with holy light.", "spear_of_lights"),
                                new OceanOfLight(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Ocean of Light", Material.WHITE_CONCRETE, "Flood the area with holy light that purifies everything it touches.", "ocean_of_light"),
                                new LawOfRadiance(this, 2, AbilityType.SEQUENCE_PROGRESSION, "Law of Radiance", Material.TOTEM_OF_UNDYING, "Emits a blinding divine radiance that suppresses the usage of all Beyonder abilities within its domain.", "law_of_radiance")
                        },
                        1, new Ability[] {
                                new DivinePurification(this, 1, AbilityType.SEQUENCE_PROGRESSION, "Divine Purification", Material.GOLDEN_SWORD, "Create countless pillars of light around you, purifying everything.", "divine_purification"),
                                new DivineKingdomOfBlazingLight(this, 1, AbilityType.SEQUENCE_PROGRESSION, "Divine Kingdom of Blazing Light", Material.GOLDEN_HORSE_ARMOR, "Manifest your Divine Kindom into the world. Inside you and yxour allies can not be harmed.", "divine_kingdom_of_blazing_light")
                        }
                )
        );
    }

    @Override
    protected void initPotIngredients() {
    }

    @Override
    public String getURL() {
        return "http://textures.minecraft.net/texture/f37c70882f1b53b3f1ad3067fa7d32cb8ccefba7fd2ed1c15a58ed2d826db846";
    }

    @Override
    public double optimalDistance(int sequence, double health) {
        if(health < 2)
            return 25;
        else return switch (sequence) {
            case 8, 7, 6, 5 -> 9;
            case 4, 3, 2, 1 -> 12;
            default -> 1;
        };
    }
}
