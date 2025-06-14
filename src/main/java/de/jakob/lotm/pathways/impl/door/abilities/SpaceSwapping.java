package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class SpaceSwapping extends SelectableAbility {

    public SpaceSwapping(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
        canBeUsedByNonPlayer = false;
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Swap two locations", "Move space to a new location"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 500, 1, 500, 2, 500
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
        }
    }
}
