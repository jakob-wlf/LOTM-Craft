package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

@NoArgsConstructor
public class Decay extends Ability {

    public Decay(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        hasCooldown = true;
        cooldownTicks = 40;
    }

    @Override
    public void useAbility(Beyonder beyonder) {

    }
}
