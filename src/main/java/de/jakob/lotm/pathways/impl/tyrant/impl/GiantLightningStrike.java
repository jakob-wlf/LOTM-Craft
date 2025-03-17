package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.pathways.TyrantUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

@NoArgsConstructor
public class GiantLightningStrike extends Ability {

    public GiantLightningStrike(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(350))
            return;

        Location location = getTargetLocation(beyonder.getEntity(), 14);

        TyrantUtil.strikeGiantLightning(location, beyonder.isGriefingEnabled(), TyrantUtil.purpleDust, TyrantUtil.purpleDust2, 7);
        damageNearbyEntities(83, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 14, location, beyonder.getEntity().getWorld(), false, 0);
    }

}
