package de.jakob.lotm.pathways.impl.chained;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.abilities.common_abilities.SpiritVision;
import de.jakob.lotm.pathways.impl.chained.abilities.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ChainedPathway extends Pathway {

    public ChainedPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_chained"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                },
                8, new Ability[] {
                        new SpiritVision(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Spirit Vision", Material.ENDER_EYE, "Reveal the health of nearby entities, granting you insight into your enemies and friends.", "spirit_vision")
                },
                7, new Ability[]{
                        new WerewolfTransformation(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Werewolf Transformation", Material.RAW_IRON, "Transform into a werewolf, gaining enhanced abilities and strength.", "werewolf_transformation"),
                        new DarknessEncroachment(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Darkness Encroachment", Material.BLACK_DYE, "Make the Darkness grow heavier and condense like frost, slowly spreading to your surroundings.", "darkness_encroachment")
                },
                6, new Ability[] {
                        new ZombieTransformation(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Zombie Transformation", Material.ROTTEN_FLESH, "Transform into a zombie, gaining enhanced abilities and strength.", "zombie_transformation"),
                        new IceManipulation(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Ice Manipulation", Material.PACKED_ICE, "Manipulate ice to create walls and stun your enemies.", "ice_manipulation"),
                        new WitherExplosion(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Wither Explosion", Material.SOUL_SAND, "Create a powerful explosion that damages and knocks back nearby entities.", "wither_explosion"),
                        new ZombieSummoning(this, 6, AbilityType.SEQUENCE_PROGRESSION, "Zombie Summoning", Material.ZOMBIE_HORSE_SPAWN_EGG, "Summon a horde of zombies to fight for you.", "zombie_summoning")
                },
                5, new Ability[] {
                        new WraithTransformation(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Wraith Transformation", Material.GHAST_TEAR, "Transform into a wraith, gaining enhanced abilities, turning invisible and being able to fly.", "wraith_transformation"),
                        new WraithShriek(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Wraith Shriek", Material.SNOWBALL, "let out a sharp shriek, that damages your opponents.", "wraith_shriek")
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
    protected void initSequenceNames() {
        sequenceNames = new String[]{ "Chained", "Abomination", "Ancient Bane", "Disciple of Silence", "Puppet", "Wraith", "Zombie", "Werewolf", "Lunatic", "Prisoner" };
    }

    @Override
    public String getURL() {
        return "http://textures.minecraft.net/texture/e9469a2f7a98593a4b0bd6383d70fcf105904a930beaa0671a999b1930b94953";
    }

    @Override
    protected void initPotIngredients() {
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
