package de.jakob.lotm.listener;

import de.jakob.lotm.BeyonderConfigManager;
import de.jakob.lotm.LOTM;
import de.jakob.lotm.entity.*;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.ConfigManager;
import de.jakob.lotm.util.lotm.Lookup;
import de.jakob.lotm.util.lotm.UnderworldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Random;

public class SpawnListener implements Listener {

    Random random = new Random();

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getLocation().getWorld() == UnderworldUtil.underworldLocation.getWorld() && event.getLocation().distance(UnderworldUtil.underworldLocation) <= UnderworldUtil.underworldSize) {
            event.setCancelled(true);
        }

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            if(event.getEntity() instanceof LivingEntity livingEntity && !livingEntity.hasAI()) {
                return;
            }

            if(event.getEntityType() == EntityType.ZOMBIE_HORSE) {
                spawnBeyonderNPC(event.getLocation(), false);
                event.setCancelled(true);
                return;
            }

            if(event.getEntity().getScoreboardTags().contains("no_spawn"))
                return;

            spawnCustomEntity(event);

            if(Lookup.isNoNPCSpawnEntity(event.getEntity()))
                return;

            if(random.nextInt(1000) > ConfigManager.getNPCSpawnRate())
                return;

            if(!(event.getEntity() instanceof LivingEntity))
                return;

            Player player = LOTM.getInstance().getNearestPlayer(event.getLocation());
            if(player == null || player.getLocation().distance(event.getLocation()) > 200) return;

            Block block = event.getLocation().getBlock();
            if(block.getType() == Material.WATER || block.getType() == Material.LAVA) return;
            if(block.getType().isSolid() || block.getRelative(0, 10, 0).getType().isSolid()) return;

            Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> spawnBeyonderNPC(event.getLocation(), random.nextInt(4) == 0), 20);
        }, 20);
    }

    private void spawnCustomEntity(EntitySpawnEvent event) {
        switch(event.getEntityType()) {
            case MAGMA_CUBE -> {
                if(random.nextInt(10) == 0)
                    FireSalamander.spawn(event.getLocation());
            }
            case CAVE_SPIDER -> {
                if(random.nextInt(15) == 0)
                    BlackHuntingSpider.spawn(event.getLocation());
            }
            case WOLF -> {
                if(random.nextInt(22) == 0)
                    GrayDemonicWolf.spawn(event.getLocation());
            }
            case PARROT -> {
                if(random.nextInt(12) == 0)
                    BlueShadowFalcon.spawn(event.getLocation());
            }
            case CHICKEN -> {
                if(random.nextInt(35) == 0)
                    BlueShadowFalcon.spawn(event.getLocation());
            }
            case ENDERMAN -> {
                if(random.nextInt(15) == 0)
                    LostTraveler.spawn(event.getLocation());
            }
            case ZOMBIE -> {
                if(random.nextInt(48) == 0)
                    AncientWraith.spawn(event.getLocation());
            }
        }
    }

    private void spawnBeyonderNPC(Location location, boolean hostile) {
        Pathway pathway = LOTM.getInstance().getPathways().get(random.nextInt(LOTM.getInstance().getPathways().size()));

        if(pathway == null)
            return;

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            LOTM.getInstance().createBeyonderNPC(pathway, random.nextInt(4, 10), hostile, location);
        }, 20);
    }
}
