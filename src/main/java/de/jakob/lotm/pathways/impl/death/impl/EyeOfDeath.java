package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class EyeOfDeath extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(120, 120, 120), 1.3f);

    public EyeOfDeath(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeCopied = false;
    }


    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        if(!beyonder.removeSpirituality(20))
            return;

        casting.add(beyonder);
        LivingEntity entity = beyonder.getEntity();
        boolean isPlayer = entity instanceof Player;

        new BukkitRunnable() {

            @Override
            public void run() {
                beyonder.addMultiplierModifier(1.18, 15);
                if(isPlayer) {
                    Player player = (Player) entity;
                    player.spawnParticle(Particle.DUST, entity.getEyeLocation(), 25, .5, .5, .5, 0, dustOptions);
                    player.playSound(entity.getLocation(), Sound.BLOCK_BEACON_AMBIENT, .1f, .1f);
                }


                if(!beyonder.removeSpirituality(3) || !casting.contains(beyonder)) {
                    casting.remove(beyonder);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 12);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        String text = casting.contains(beyonder) ? "§dActive" : "§dInactive";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
