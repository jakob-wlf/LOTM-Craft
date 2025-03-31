package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Fly extends ToggleableAbility {

    public Fly(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void start(Beyonder beyonder) {
        if(!(beyonder.getEntity() instanceof Player player))
            return;

        if(player.getAllowFlight())
            return;

        player.setAllowFlight(true);

        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);
    }

    @Override
    protected void impl(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(5))
            casting.remove(beyonder);
    }

    @Override
    protected void stop(Beyonder beyonder) {
        if(!(beyonder.getEntity() instanceof Player player))
            return;

        player.setAllowFlight(false);
        LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return false;
    }
}
