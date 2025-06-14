package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.MathUtil;
import de.jakob.lotm.util.minecraft.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;

public class DivineKingdomOfBlazingLight extends Ability {

    private final HashSet<Beyonder> cooldown = new HashSet<>();

    public DivineKingdomOfBlazingLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeCopied = false;
        canBeUsedByNonPlayer = false;

        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.getNearbyEntities(110, 90, 110).stream().filter(e -> e.getType() == EntityType.MARKER).filter(e -> {
                        if (e.getScoreboardTags().stream().noneMatch(s -> s.startsWith("remove_damage")))
                            return false;

                        String radiusString = e.getScoreboardTags().stream().filter(s -> s.startsWith("radius_")).map(s -> s.replace("radius_", "")).findFirst().orElse(null);

                        if (radiusString == null || !MathUtil.isInteger(radiusString))
                            return false;

                        int radius = Integer.parseInt(radiusString);

                        if (e.getLocation().distance(player.getLocation()) > radius)
                            return false;

                        String uuid = e.getScoreboardTags().stream().filter(s -> s.startsWith("remove_damage")).map(s -> s.replace("remove_damage_", "")).findFirst().orElse(null);
                        if (uuid == null || !player.getUniqueId().toString().equalsIgnoreCase(uuid))
                            return false;

                        return true;

                    }).findFirst().ifPresent(invulnerableMarker -> player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 20, false, false, false)));

                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 20);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(cooldown.contains(beyonder)) {
            beyonder.getEntity().sendMessage("§6You have to wait before you can manifest your divine kingdom in the world somewhere else.");
            return;
        }

        if(!beyonder.isGriefingEnabled()) {
            beyonder.getEntity().sendMessage("§6Enable griefing to manifest your Divine Kingdom. Use /togglegriefing.");
            return;
        }

        if(!beyonder.removeSpirituality(2000))
            return;

        cooldown.add(beyonder);

        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldown.remove(beyonder), 20 * 60 * 25);

        LivingEntity entity = beyonder.getEntity();

        List<Block> blocks = BlockUtil.getBlocksInCircleRadius(entity.getEyeLocation().getBlock(), 100, true).stream().filter(b -> b.getType().isSolid()).toList();

        Marker marker = (Marker) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.MARKER);
        marker.getScoreboardTags().add("remove_damage_" + entity.getUniqueId());
        marker.getScoreboardTags().add("radius_" + 100);
        marker.setPersistent(true);
        marker.setInvulnerable(true);

        Marker noAbilitiesMarker = (Marker) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.MARKER);
        noAbilitiesMarker.getScoreboardTags().add("no_abilities");
        noAbilitiesMarker.getScoreboardTags().add("radius_100");
        noAbilitiesMarker.getScoreboardTags().add("sequence_" + beyonder.getCurrentSequence());
        noAbilitiesMarker.getScoreboardTags().add("exclude_" + beyonder.getUuid());

        BlockUtil.getSphereBlocks(entity.getEyeLocation().clone().add(0, 35, 0), 12).forEach(b -> b.setType(Material.ORANGE_STAINED_GLASS));
        BlockUtil.getSphereBlocks(entity.getEyeLocation().clone().add(0, 35, 0), 10).forEach(b -> b.setType(Material.YELLOW_STAINED_GLASS));
        BlockUtil.getSphereBlocks(entity.getEyeLocation().clone().add(0, 35, 0), 8).forEach(b -> b.setType(Material.LIGHT));

        blocks.forEach(block -> {
            block.setType(Material.SMOOTH_QUARTZ);

            if(!block.getRelative(0, 1, 0).getType().isSolid())
                block.getRelative(0, 1, 0).setType(Material.LIGHT);
        });

        for(int i = 0; i < 6; i++) {
            Location loc = entity.getEyeLocation().clone().add(random.nextDouble(150) - 75, 255, random.nextDouble(150) - 75);
            while(!loc.getBlock().getType().isSolid() && loc.getY() > 0) {
                loc.subtract(0, 1, 0);
            }

            if(loc.getY() < 0)
                continue;

            loc.add(0, random.nextDouble(15, 26), 0);

            BlockUtil.getSphereBlocks(loc, 6).forEach(b -> b.setType(Material.ORANGE_STAINED_GLASS));
            BlockUtil.getSphereBlocks(loc, 4).forEach(b -> b.setType(Material.YELLOW_STAINED_GLASS));
            BlockUtil.getSphereBlocks(loc, 2).forEach(b -> b.setType(Material.LIGHT));
        }
    }
}
