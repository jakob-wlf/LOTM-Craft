package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


@NoArgsConstructor
public class Conceptualization extends ToggleableAbility implements Listener {

    public Conceptualization(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        plugin.registerListener(this);
        canBeUsedByNonPlayer = false;
        allowDifferentWorld = true;
    }

    @Override
    protected void start(Beyonder beyonder) {
        if(!(beyonder instanceof BeyonderPlayer))
            return;

        Player player = (Player) beyonder.getEntity();
        if(player.getAllowFlight()) {
            casting.remove(beyonder);
            return;
        }

        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);
    }

    @Override
    protected void impl(Beyonder beyonder) {
        if(!(beyonder instanceof BeyonderPlayer))
            return;

        Player player = (Player) beyonder.getEntity();

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvisible(true);

        ParticleSpawner.displayParticles(Particle.ENCHANT, player.getEyeLocation().subtract(0, .5, 0), 100, 1, 1, 1, 0, 200);
    }

    @Override
    protected void stop(Beyonder beyonder) {
        if(!(beyonder instanceof BeyonderPlayer))
            return;

        Player player = (Player) beyonder.getEntity();

        if(player == null || !player.isValid())
            return;

        player.setInvisible(false);
        player.setAllowFlight(false);

        LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);
    }
}
