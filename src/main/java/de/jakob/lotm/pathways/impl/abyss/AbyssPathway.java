package de.jakob.lotm.pathways.impl.abyss;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.impl.abyss.impl.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class AbyssPathway extends Pathway {
    public AbyssPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[] {
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_abyss"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                },
                8, new Ability[] {
                        new PoisonousFlame(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Poisonous Flame", Material.LIME_DYE, "Unleash a deadly green flame that poisons enemies on impact.", "poisonous_flame"),
                        new ToxicSmoke(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Toxic Smoke", Material.INK_SAC, "Release a cloud of toxic smoke that blinds and poisons foes.", "toxic_smoke")
                },
                7, new Ability[] {
                },
                6, new Ability[] {
                        new FireResistance(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Fire Resistance", Material.MAGMA_CREAM, "Gain immunity to damage from fire or lava.", "fire_resistance_abyss"),
                        new DevilTransformation(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Devil Transformation", Material.NETHERITE_INGOT, "Transform into a demonic form, enhancing your abilities", "devil_transformation"),
                        new LanguageOfFoulness(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Language of Foulness", Material.CHARCOAL, "Cast various spells using the devils language. \n§k§r§7- §6Right-Click §7to switch the selected ability.§r", "language_of_foulness"),
                        new FlameSpells(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Flame Spells", Material.BLAZE_POWDER, "Use several flame-related spells. \n§k§r§7- §6Right-Click §7to switch the selected ability", "flame_spells")
                },
                5, new Ability[] {
                        new AvatarOfDesire(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Avatar of Desire", Material.ENDER_EYE, "Morph into a sticky black liquid to avoid danger and escape your enemies.", "avatar_of_desire"),
                        new DefilingSeed(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Defiling Seed", Material.WITHER_ROSE, "Plant a defiling seed into the mind of your opponent, slowly influencing and corrupting them over a long time.", "defiling_seed")
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
        return "http://textures.minecraft.net/texture/df7a9dcdd1b9018719f297b42e45b925bf9b286ebb53f654b21bb5a98d8becb4";
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
