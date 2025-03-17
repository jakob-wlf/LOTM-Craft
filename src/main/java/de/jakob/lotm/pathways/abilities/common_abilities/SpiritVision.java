package de.jakob.lotm.pathways.abilities.common_abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class SpiritVision extends Ability {

    private final Set<UUID> toggledPlayers = new HashSet<>();
    private final HashMap<UUID, Particle.DustOptions> dustOptionsForEntity = new HashMap<>();

    public SpiritVision(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        customModelData = "spirit_vision_ability";

        canBeCopied = false;
        canBeUSedByNonPlayer = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if (toggledPlayers.contains(beyonder.getEntity().getUniqueId())) {
            beyonder.getEntity().getWorld().playSound(beyonder.getEntity(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            toggledPlayers.remove(beyonder.getEntity().getUniqueId());
            return;
        }

        if(!beyonder.removeSpirituality(10))
            return;

        beyonder.getEntity().getWorld().playSound(beyonder.getEntity(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        toggledPlayers.add(beyonder.getEntity().getUniqueId());
        beyonder.getEntity().getScoreboardTags().add("see_spirits_spirit_vision");

        Pathway p = beyonder.getCurrentPathway();
        int sequence = beyonder.getCurrentSequence();

        Player player = (Player) beyonder.getEntity();

        new BukkitRunnable(){

            @Override
            public void run() {
                if(beyonder.getEntity() == null || !beyonder.getEntity().isValid() || beyonder.getCurrentSequence() != sequence || beyonder.getCurrentPathway() != p || !toggledPlayers.contains(beyonder.getEntity().getUniqueId())) {
                    if(beyonder.getEntity() != null) {
                        beyonder.getEntity().getScoreboardTags().remove("see_spirits_spirit_vision");
                    }
                    toggledPlayers.remove(beyonder.getEntity().getUniqueId());
                    cancel();
                    return;
                }

                for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                    if (entity.isValid() && entity instanceof LivingEntity && entity != player && entity.getType() != EntityType.ARMOR_STAND && !entity.getScoreboardTags().contains("spirit")) {
                        if(!dustOptionsForEntity.containsKey(entity.getUniqueId())) {
                            dustOptionsForEntity.put(entity.getUniqueId(), ParticleUtil.coloredDustOptionsSize2[random.nextInt(ParticleUtil.coloredDustOptions.length)]);
                        }

                        Particle.DustOptions dustOptions = dustOptionsForEntity.get(entity.getUniqueId());

                        player.spawnParticle(Particle.DUST, ((LivingEntity) entity).getEyeLocation(), 1, 0.2, 0.4, 0.2, 0, dustOptions);

                        double health = ((LivingEntity) entity).getHealth();

                        if(entity.getScoreboardTags().contains("health_display")) {
                            continue;
                        }

                        // Create a hologram above the entity showing its health
                        Location hologramLocation = ((LivingEntity) entity).getEyeLocation().clone().add(0, .6, 0);
                        ArmorStand hologram = entity.getWorld().spawn(hologramLocation, ArmorStand.class);

                        LOTM.getInstance().getEntitiesToRemove().add(hologram);

                        entity.addScoreboardTag("health_display");

                        hologram.setCustomName(ChatColor.RED + "❤ " + Math.round(health * 10.0) / 10.0);
                        hologram.setCustomNameVisible(true);
                        hologram.setGravity(false);
                        hologram.setVisible(false);
                        hologram.setVisibleByDefault(false);
                        hologram.setInvulnerable(true);
                        hologram.setSmall(true);
                        hologram.setMarker(true);
                        player.showEntity(LOTM.getInstance(), hologram);

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                if(!hologram.isValid() || !entity.isValid() || !entity.getScoreboardTags().contains("health_display") || !toggledPlayers.contains(player.getUniqueId()) ||entity.getScoreboardTags().contains("spirit")) {
                                    hologram.remove();
                                    entity.removeScoreboardTag("health_display");
                                    cancel();
                                    return;
                                }

                                double health = ((LivingEntity) entity).getHealth();
                                hologram.setCustomName(ChatColor.RED + "❤ " + Math.round(health * 10.0) / 10.0);
                                hologram.setFireTicks(0);

                                Location hologramLocation = ((LivingEntity) entity).getEyeLocation().clone().add(0, .6, 0);
                                hologram.teleport(hologramLocation);
                            }
                        }.runTaskTimer(plugin, 0, 2);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 4);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        String text = toggledPlayers.contains(beyonder.getUuid()) ? "§dActive" : "§dInactive";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

}
