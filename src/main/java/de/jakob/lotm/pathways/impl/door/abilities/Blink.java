package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.pathways.DoorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;

@NoArgsConstructor
public class Blink extends Ability {

    public Blink(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(10))
            return;

        Location targetLocation = getTargetBlock(beyonder.getEntity(), 12).getLocation();
        if(targetLocation.getBlock().getType().isSolid()) targetLocation.add(0, 1, 0);
        targetLocation.setDirection(beyonder.getEntity().getLocation().getDirection());

        beyonder.getEntity().teleport(targetLocation);

        World world = beyonder.getEntity().getWorld();

        DoorUtil.displayDefaultTeleportParticles(world, beyonder.getEntity().getEyeLocation());
        world.playSound(beyonder.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .8f, 1);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return (beyonder.getEntity().getHealth() < 10 || beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 6);
    }
}
