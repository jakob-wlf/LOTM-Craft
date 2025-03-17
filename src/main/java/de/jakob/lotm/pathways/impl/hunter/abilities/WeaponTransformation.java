package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.Pathway;
import org.bukkit.Material;

public class WeaponTransformation extends PassiveAbility {
    public WeaponTransformation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
        showAbilityIcon = false;
    }
}
