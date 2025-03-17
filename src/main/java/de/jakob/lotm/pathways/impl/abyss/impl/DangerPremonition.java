package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;

public class DangerPremonition extends PassiveAbility {

    public DangerPremonition(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void tick(Beyonder beyonder) {

    }
}
