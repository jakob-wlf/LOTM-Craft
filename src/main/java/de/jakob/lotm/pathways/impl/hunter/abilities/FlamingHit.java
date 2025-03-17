package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

public class FlamingHit extends PassiveAbility {
    public FlamingHit(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        customModelData = "flaming_hit_ability";
    }

    @Override
    public void onHit(Beyonder beyonder, LivingEntity damaged) {
        damaged.setFireTicks(20 * 5);
    }
}
