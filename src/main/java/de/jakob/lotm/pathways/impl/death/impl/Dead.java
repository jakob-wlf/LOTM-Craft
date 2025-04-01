package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;

public class Dead extends PassiveAbility {
    public Dead(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void tick(Beyonder beyonder) {
        if(beyonder.getEntity() == null)
            return;

        if(beyonder.getEntity().getScoreboardTags().contains("dead"))
            return;

        beyonder.getEntity().getScoreboardTags().add("dead");
    }
}
