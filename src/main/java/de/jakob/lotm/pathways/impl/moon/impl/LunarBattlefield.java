package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class LunarBattlefield extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();
    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(232, 26, 63), 2.3f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(163, 24, 49), 2.3f);

    public LunarBattlefield(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        casting.add(beyonder);

        if(!beyonder.removeSpirituality(50)) {
            return;
        }

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getLocation().add(0, .2, 0);

        List<Location> lightBlocks = BlockUtil.getBlocksInCircleRadius(location.clone().subtract(0, .2, 0).getBlock(), 20, false).stream().filter(block -> block.getType() == Material.AIR).map(Block::getLocation).toList();
        lightBlocks.forEach(loc -> loc.getBlock().setType(Material.LIGHT));

        LOTM.getInstance().getBlocksToRemove().addAll(lightBlocks);

        int radius = beyonder.getCurrentSequence() > 4 ? 20 : 35;

        playSound(beyonder, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        playSound(beyonder, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 40) {
                    casting.remove(beyonder);
                }

                if(!casting.contains(beyonder)) {
                    lightBlocks.forEach(LOTM.getInstance().getBlocksToRemove()::remove);
                    lightBlocks.forEach(loc -> loc.getBlock().setType(Material.AIR));
                    entity.getScoreboardTags().remove("lunar");
                    cancel();
                    return;
                }

                if(counter % 20 == 0) {
                    List<LivingEntity> nearbyEntities = getNearbyLivingEntities(entity, radius, location, world);
                    for(LivingEntity target : nearbyEntities) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 4, 1, false, false, false));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 4, 1, false, false, false));

                        Beyonder beyonderTarget = LOTM.getInstance().getBeyonder(target.getUniqueId());
                        if(beyonderTarget == null)
                            continue;

                        beyonderTarget.removeSpirituality(Math.round(beyonder.getCurrentMaxSpirituality() / 50f));
                        beyonderTarget.addMultiplierModifier(.75, 35);
                    }

                    if(entity.getLocation().distance(location) < radius) {
                        entity.getScoreboardTags().add("lunar");
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 3, false, false, false));
                        beyonder.addMultiplierModifier(1.3, 35);
                    }
                    else {
                        entity.getScoreboardTags().remove("lunar");
                    }
                }

                ParticleSpawner.displayParticles(world, Particle.DUST, location, 150, radius, .2, radius, 0, dust, 250);
                ParticleSpawner.displayParticles(world, Particle.DUST, location, 150, radius, .2, radius, 0, dust2, 250);
                ParticleUtil.createParticleSphere(location.clone().add(0, 10.5, 0), 2, 100, Particle.DUST, dust);
                ParticleUtil.createParticleSphere(location.clone().add(0, 10.5, 0), 2, 40, Particle.DUST, dust2);

                counter+=5;
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }
}
