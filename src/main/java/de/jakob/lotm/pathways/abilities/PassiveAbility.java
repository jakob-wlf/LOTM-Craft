package de.jakob.lotm.pathways.abilities;

import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

public abstract class PassiveAbility extends Ability {

    public PassiveAbility(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Nullable
    public PotionEffect[] getPotionEffect(Beyonder beyonder) {
        return null;
    }

    public void onHit(Beyonder beyonder, LivingEntity damaged) {

    }

    public void onDamage(Beyonder beyonder, EntityDamageEvent event) {

    }

    /**
     * Gets called every 5 ingame ticks
     * @param beyonder
     * The Beyonder using the ability
     */
    public void tick(Beyonder beyonder) {

    }

}
