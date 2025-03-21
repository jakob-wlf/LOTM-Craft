package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class WingsOfLight extends ToggleableAbility {

    public WingsOfLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeUSedByNonPlayer = false;
        tickDelay = 1;
    }

    @Override
    protected void start(Beyonder beyonder) {
        if(!(beyonder.getEntity() instanceof Player player)) {
            casting.remove(beyonder);
            return;
        }

        if(player.getAllowFlight()) {
            casting.remove(beyonder);
            return;
        }

        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @Override
    protected void impl(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(1)) {
            casting.remove(beyonder);
            return;
        }

        if(!(beyonder.getEntity() instanceof Player player)) {
            casting.remove(beyonder);
            return;
        }

        player.setFlying(true);
        ParticleUtil.drawShape(player.getEyeLocation().add(0, .1, 0).subtract(player.getEyeLocation().getDirection().setY(0).normalize().multiply(.5)), player.getEyeLocation().getDirection().setY(0).normalize(), 2.5, Particle.END_ROD, ParticleUtil.Shape.CLASSIC_WINGS, null);

    }

    @Override
    protected void stop(Beyonder beyonder) {
        if(!(beyonder.getEntity() instanceof Player player)) {
            return;
        }

        player.setAllowFlight(false);
        player.setFlying(false);
        LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);

    }
}
