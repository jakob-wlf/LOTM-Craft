package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class Resistances extends PassiveAbility {

    PotionEffectType[] harmfulEffects = new PotionEffectType[] {
            PotionEffectType.POISON,
            PotionEffectType.WITHER,
            PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.BLINDNESS,
            PotionEffectType.NAUSEA
    };

    public Resistances(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void tick(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        entity.setFireTicks(0);
        entity.setFreezeTicks(0);

        for(PotionEffectType harmfulEffectType : harmfulEffects) {
            entity.removePotionEffect(harmfulEffectType);
        }
    }
}
