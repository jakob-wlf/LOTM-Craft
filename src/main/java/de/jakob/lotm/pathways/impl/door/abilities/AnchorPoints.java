package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

@NoArgsConstructor
public class AnchorPoints extends Ability {

    public AnchorPoints(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        canBeUsedByNonPlayer = false;
        canBeCopied = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {

    }
}
