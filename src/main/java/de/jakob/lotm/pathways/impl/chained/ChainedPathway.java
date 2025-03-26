package de.jakob.lotm.pathways.impl.chained;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.impl.chained.abilities.PhysicalEnhancements;
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
                },
                7, new Ability[]{
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
