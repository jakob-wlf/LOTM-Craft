package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.Pathway;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireResistance extends PassiveAbility {
    public FireResistance(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public PotionEffect[] getPotionEffect(Beyonder beyonder) {
        return new PotionEffect[]{new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 0, 1)};
    }

    @Override
    public void tick(Beyonder beyonder) {
        beyonder.getEntity().setFireTicks(0);
    }
}
