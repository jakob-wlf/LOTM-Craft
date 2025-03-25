package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class SpiritWorldShuttling extends ToggleableAbility {

    private final HashMap<UUID, GameMode> previousGameModes = new HashMap<>();

    public SpiritWorldShuttling(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeUSedByNonPlayer = false;
        canBeCopied = false;
    }

    @Override
    protected void start(Beyonder beyonder) {
        Player player = (Player) beyonder.getEntity();
        previousGameModes.put(player.getUniqueId(), player.getGameMode());

        player.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    protected void impl(Beyonder beyonder) {
        Player player = (Player) beyonder.getEntity();

        player.setVelocity(player.getLocation().getDirection().normalize().multiply(1));
        for(int i = 0; i < 3; i++) {
            Particle.DustOptions dustOptions = ParticleUtil.coloredDustOptionsSize4[random.nextInt(ParticleUtil.coloredDustOptionsSize4.length)];
            player.spawnParticle(Particle.DUST, player.getEyeLocation().add(player.getLocation().getDirection().multiply(5)), 2, 4, 4, 4, dustOptions);
        }
    }

    @Override
    protected void stop(Beyonder beyonder) {
        Player player = (Player) beyonder.getEntity();
        GameMode gameMode = previousGameModes.getOrDefault(beyonder.getEntity().getUniqueId(), GameMode.SURVIVAL);

        player.setGameMode(gameMode);
    }
}
