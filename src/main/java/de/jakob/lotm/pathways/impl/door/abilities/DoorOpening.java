package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class DoorOpening extends Ability {

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.AQUA, 1.3f);

    public DoorOpening(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        canBeUSedByNonPlayer = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(8))
            return;

        Location location = beyonder.getEntity().getLocation();
        Vector direction = location.getDirection().normalize();

        beyonder.getEntity().getWorld().playSound(location, Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 1);

        if(!location.clone().add(direction).getBlock().getType().isSolid())
            return;

        int maxDistance = (int) Math.round(20f * beyonder.getCurrentMultiplier());

        for(int i = 0; i < maxDistance; i++) {
            location.add(direction);
            if(!location.getBlock().getType().isSolid()) {
                beyonder.getEntity().teleport(location);
                beyonder.getEntity().getWorld().spawnParticle(Particle.DUST, location.add(0, .8, 0), 12, 0.2, 0.2, 0.2, 0, dustOptions);
                beyonder.getEntity().getWorld().spawnParticle(Particle.SMOKE, location.add(0, .75, 0), 6, 0.2, 0.2, 0.2, 0);
                break;
            }
        }
    }
}
