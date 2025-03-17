package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.VectorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Cull extends Ability implements Listener {

    private final Set<UUID> activatedCull;

    public Cull(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        LOTM.getInstance().registerListener(this);

        activatedCull = new HashSet<>();

        canBeCopied = false;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if(!activatedCull.contains(damager.getUniqueId()))
            return;

        Beyonder beyonder = LOTM.getInstance().getBeyonder(damager.getUniqueId());
        if(beyonder == null)
            return;

        if(!beyonder.removeSpirituality(38))
            return;

        event.setDamage(event.getDamage() * 1.45);

        World world = damager.getWorld();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(40, 40, 40), 2);

        world.spawnParticle(Particle.DAMAGE_INDICATOR, event.getEntity().getLocation(), 20, .3, .3, .3, 0.5);
        world.spawnParticle(Particle.DUST, event.getEntity().getLocation(), 30, 1, 1, 1, 0.5, dustOptions);

        world.playSound(event.getEntity().getLocation(), Sound.ENTITY_WITHER_HURT, .85f, 1f);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        UUID uuid = beyonder.getUuid();
        if(activatedCull.contains(uuid)) {
            activatedCull.remove(uuid);
            return;
        }

        activatedCull.add(uuid);
    }

    final Particle.DustOptions grayDust = new Particle.DustOptions(Color.fromRGB(40, 40, 40), 1.25f);
    final Particle.DustOptions redDust = new Particle.DustOptions(Color.fromRGB(150, 40, 40), 1.25f);

    @Override
    public void tick(Beyonder beyonder) {
        if(!activatedCull.contains(beyonder.getUuid()) ||!(beyonder instanceof BeyonderPlayer beyonderPlayer))
            return;

        Player player = beyonderPlayer.getPlayer();

        Vector rightLocation = VectorUtil.rotateAroundY(player.getLocation().getDirection().setY(0), 90).normalize().multiply(.4);
        Location rightHand = player.getEyeLocation().add(rightLocation).add(0, -.85, 0);
        Location leftHand = player.getEyeLocation().add(rightLocation.multiply(-1)).add(0, -.85, 0);

        World world = player.getWorld();

        world.spawnParticle(Particle.DUST, rightHand, 6, .05, .05, .05, 0, grayDust);
        world.spawnParticle(Particle.DUST, leftHand, 6, .05, .05, .05, 0, grayDust);
        world.spawnParticle(Particle.DUST, rightHand, 3, .05, .05, .05, 0, redDust);
        world.spawnParticle(Particle.DUST, leftHand, 3, .05, .05, .05, 0, redDust);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        String activated = "§8" + (activatedCull.contains(beyonder.getUuid()) ? "Activated" : "Not Active");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(activated));
    }
}
