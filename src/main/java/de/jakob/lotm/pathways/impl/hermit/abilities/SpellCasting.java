package de.jakob.lotm.pathways.impl.hermit.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class SpellCasting extends SelectableAbility {

    private final HashMap<Beyonder, Location> windCastingLocations = new HashMap<>();

    public SpellCasting(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {
                "Flames", "Wind", "Lightning", "Wave", "Purification"
        };
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 19,
                1, 14,
                2, 30,
                3, 30,
                4, 30
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {

        switch(ability) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
            case 4 -> {}
        }
    }
}
