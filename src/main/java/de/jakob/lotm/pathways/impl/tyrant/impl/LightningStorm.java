package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.pathways.TyrantUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class LightningStorm extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public LightningStorm(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            return;
        }

        if(!beyonder.removeSpirituality(250)) {
            return;
        }

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location startLoc = getTargetLocation(entity, 18);

        if(startLoc.getWorld() == null)
            return;

        entity.getWorld().setStorm(true);

        runTaskWithDuration(6, 20 * 15, () ->{
            for(int i = 0; i < random.nextInt(5) + 3; i++) {
                Location location = startLoc.clone().add(random.nextDouble(42) - 21, 0, random.nextDouble(42) - 21);

                TyrantUtil.strikeLightning(location, beyonder.isGriefingEnabled(), TyrantUtil.blueDust, TyrantUtil.blueDust2, 4);
                damageNearbyEntities(40, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 10, location, beyonder.getEntity().getWorld(), false, 0);
            }
        }, () -> {
           casting.remove(beyonder);
           startLoc.getWorld().setStorm(false);
        });
    }
}
