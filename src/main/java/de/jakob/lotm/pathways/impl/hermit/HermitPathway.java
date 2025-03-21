package de.jakob.lotm.pathways.impl.hermit;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.abilities.common_abilities.SpiritVision;
import de.jakob.lotm.pathways.impl.hermit.abilities.PhysicalEnhancements;
import de.jakob.lotm.pathways.impl.hermit.abilities.SpellCasting;
import de.jakob.lotm.pathways.impl.hermit.abilities.StarrySpellCasting;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class HermitPathway extends Pathway {

    public HermitPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_hermit"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                        new SpiritVision(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Spirit Vision", Material.ENDER_EYE, "Reveal the health of nearby entities, granting you insight into your enemies and friends.", "spirit_vision"),
                },
                8, new Ability[] {
                },
                7, new Ability[]{
                        new SpellCasting(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Spellcasting", Material.AMETHYST_SHARD, "cast various different spells using different materials.", "spell_casting_hermit")
                },
                6, new Ability[] {
                },
                5, new Ability[] {
                        new StarrySpellCasting(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Starry Spellcasting", Material.NETHER_STAR, "Conjure and manipulate dazzling stars to attack or restrain your foes.", "star_magic")
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
