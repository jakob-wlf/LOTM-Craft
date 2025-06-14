package de.jakob.lotm.pathways.impl.fool.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;


public class PaperDagger extends PassiveAbility implements Listener {
    public PaperDagger(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        LOTM.getInstance().registerListener(this);
    }

    @EventHandler
    public void onPaperRightClick(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.PAPER) {
            return;
        }

        BeyonderPlayer beyonderPlayer = LOTM.getInstance().getBeyonderPlayer(event.getPlayer().getUniqueId());
        if( beyonderPlayer == null || !beyonderPlayer.getAbilities().contains(this)) {
            return;
        }

        ItemsUtil.removeItem(event.getPlayer(), new ItemStack(Material.PAPER));

        LivingEntity entity = event.getPlayer();
        World world = entity.getWorld();

        world.playSound(entity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);

        Vector direction = entity.getEyeLocation().getDirection().normalize().multiply(2);
        Location loc = entity.getEyeLocation().add(direction.clone().multiply(1.5));

        AtomicBoolean finished = new AtomicBoolean(false);

        runTaskWithDuration(1, 20 * 5, finished, () -> {
            ParticleSpawner.displayParticles(Particle.CLOUD, loc, 8, .05, .05, .05, 0, 100);
            if(damageNearbyEntities(7.75, beyonderPlayer.getCurrentMultiplier(), entity, 1, loc, world)) {
                world.playSound(loc, Sound.ENTITY_ARROW_HIT, 1, 1);
                finished.set(true);
            }
            if(loc.clone().add(direction).getBlock().getType().isSolid()) {
                finished.set(true);
            }
            if(finished.get()) {
                world.dropItem(loc, new ItemStack(Material.PAPER));
            }
            loc.add(direction);
        });

    }
}
