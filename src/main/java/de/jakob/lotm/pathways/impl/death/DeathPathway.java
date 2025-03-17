package de.jakob.lotm.pathways.impl.death;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.common_abilities.Cogitation;
import de.jakob.lotm.pathways.abilities.common_abilities.SpiritVision;
import de.jakob.lotm.pathways.impl.death.impl.*;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class DeathPathway extends Pathway {
    public DeathPathway(String displayName, String name, String colorPrefix, Color color) {
        super(displayName, name, colorPrefix, color);

        //TODO: Spirit Manipulation -> Select spirits that will actually follow you and fight for you. You will get their abilities as items and as long as they are nearby you can use them but they will still be fired from the spirit
        abilities = new HashMap<>(Map.of(
                9, new Ability[]{
                        new PhysicalEnhancements(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "Gain boosts to your physical stats that grow with your Sequence.", "physical_enhancements_death"),
                        new Cogitation(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Cogitation", Material.POPPED_CHORUS_FRUIT, "Enter a state of Cogitation to quickly replenish your spirituality.", "cogitation"),
                        new SpiritVision(this, 9, AbilityType.SEQUENCE_PROGRESSION, "Spirit Vision", Material.ENDER_EYE, "Reveal the health of nearby entities, granting you insight into your enemies and friends.", "spirit_vision")
                },
                8, new Ability[] {
                        new EyeOfDeath(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Eye of Death", Material.ENDER_EYE, "Determine the weaknesses of your enemies, effectively dealing more damage.", "eye_of_death"),
                        new SpiritCommunication(this, 8, AbilityType.SEQUENCE_PROGRESSION, "Spirit Communication", Material.COAL, "Communicate with the nearby spirits and let them help you restrain or fight your enemy.", "spirit_communication")
                },
                7, new Ability[]{
                        new SpiritControlling(this, 7, AbilityType.SEQUENCE_PROGRESSION, "Spirit Controlling", Material.AMETHYST_SHARD, """
                                Control the spirits around you and have them fight your foes.
                                §k§r§7- §6Right-Click: §7Control Spirit.
                                §k§r§7- §6Left-Click: §7Release Spirit.
                                """, "spirit_controlling")
                },
                6, new Ability[] {
                        new Necromancy(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Necromancy", Material.BONE, "Summon a horde of undead to fight on your behalf.", "necromancy")
                },
                5, new Ability[] {
                        new UnderworldGate(this, 5, AbilityType.SEQUENCE_PROGRESSION, "Underworld Gate", Material.PALE_OAK_DOOR, """
                                Create a Gate to the underworld either sucking in enemies, manifesting illusory tentacles, summoning spirits or use it to travel the underworld.
                                §k§r§7- §6Right-Click: §7Switch through the different casting options.
                                """, "underworld_gate")
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
        return "http://textures.minecraft.net/texture/e053ddc22485384318317790bf4bdd245b759e6d947a0dd8efbaa11a4aa0d1ad";
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
