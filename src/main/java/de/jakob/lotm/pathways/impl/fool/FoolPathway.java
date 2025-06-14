package de.jakob.lotm.pathways.impl.fool;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.abilities.common_abilities.SpiritVision;
import de.jakob.lotm.pathways.impl.fool.abilities.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class FoolPathway extends Pathway {
    public FoolPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                        new SpiritVision(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Spirit Vision", Material.ENDER_EYE, "Reveal the health of nearby entities, granting you insight into your enemies and friends.", "spirit_vision"),
                },
                8, new Ability[] {
                        new PhysicalEnhancements(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_hermit"),
                        new PaperDagger(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Paper Dagger", Material.PAPER, "Right click with paper to throw it as a dagger.", "paper_dagger"),
                },
                7, new Ability[]{
                        new AirBullet(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Air Bullet", Material.GHAST_TEAR, "Shoot a bullet of compressed air that deals damage to enemies.", "air_bullet"),
                        new FlameControlling(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Flame Controlling", Material.BLAZE_POWDER, "Control flames and use them as a projectile.", "flame_controlling"),
                        new PaperFigurine(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Paper Figurine", Material.ARMOR_STAND, "Create a paper figurine that substitutes you when you take damage.", "paper_figurine"),
                        new FlamingJump(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Flaming Jump", Material.FIRE_CHARGE, "Teleport to a nearby flame.", "flaming_jump"),
                        new UnderwaterBreathing(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Underwater Breathing", Material.PRISMARINE_CRYSTALS, "Breathe underwater using a windpipe", "underwater_breathing")
                },
                6, new Ability[] {
                },
                5, new Ability[] {
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
        return "http://textures.minecraft.net/texture/df60e6182cab215d59ea32e1a48a2a6a973d7620936774d58b3fde9efa718123";
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
