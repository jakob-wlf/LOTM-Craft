package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;

public class FallDamageImmunity extends PassiveAbility{
    public FallDamageImmunity(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
        showAbilityIcon = false;
    }

    @Override
    public void tick(Beyonder beyonder) {
        beyonder.getEntity().setFallDistance(0);
    }
}
