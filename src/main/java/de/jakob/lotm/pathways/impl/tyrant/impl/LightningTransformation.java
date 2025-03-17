package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.pathways.TyrantUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

@NoArgsConstructor
public class LightningTransformation extends ToggleableAbility {

    private final HashSet<Beyonder> transformed = new HashSet<>();

    public LightningTransformation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeCopied = false;
        canBeUSedByNonPlayer = false;
    }

    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(!(beyonder instanceof BeyonderPlayer))
            return;

        Player player = (Player) beyonder.getEntity();

        if(transformed.contains(beyonder)) {
            transformed.remove(beyonder);
            return;
        }

        if(player.getAllowFlight())
            return;

        player.setAllowFlight(true);
        player.setVisibleByDefault(false);
        player.setInvisible(true);
        player.setFlying(true);
        player.setFlySpeed(1f);
        transformed.add(beyonder);
        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);

        new BukkitRunnable() {
            int counter = 0;
            int recordTimeout = 0;

            @Override
            public void run() {
                if(recordTimeout > 20 * 8 && abilityType == AbilityType.RECORDED)
                    transformed.remove(beyonder);

                if(counter % 10 == 0 && !beyonder.removeSpirituality(95)) {
                    transformed.remove(beyonder);
                    counter = 0;
                }

                if(!transformed.contains(beyonder)) {
                    player.setFlySpeed(.2f);
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.setVisibleByDefault(true);
                    player.setInvisible(false);
                    LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);
                    cancel();
                    return;
                }

                ParticleSpawner.displayParticles(player.getWorld(), Particle.DUST, player.getEyeLocation().subtract(0, player.getHeight() / 2, 0), 25, .8, .8, .8, 0, TyrantUtil.blueDust,200);

                counter++;
                recordTimeout++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
