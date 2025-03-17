package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

@NoArgsConstructor
public class LanguageOfFoulness extends SelectableAbility {

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(130, 23, 4), 2f);
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(140, 67, 18), 2f);
    private final Particle.DustOptions dustOptions3 = new Particle.DustOptions(Color.fromRGB(60, 20, 10), 2f);

    private final Set<UUID> recentlyAffected = new HashSet<>();
    private final PotionEffect[] corruptionEffects = new PotionEffect[] {
            PotionEffectType.SLOWNESS.createEffect(80, 1),
            PotionEffectType.NAUSEA.createEffect(80, 1)
    };

    //TODO: Add sounds
    public LanguageOfFoulness(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[]{"Slow", "Corruption", "Death"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 14,
                1, 30,
                2, 50
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getLocation();

        List<LivingEntity> nearbyEntities = getNearbyLivingEntities(entity, 10, location, world).stream().filter(e -> !recentlyAffected.contains(e.getUniqueId())).toList();

        switch (ability) {
            case 0 -> castSlow(beyonder, nearbyEntities);
            case 1 -> castCorruption(beyonder, nearbyEntities);
            case 2 -> castDeath(beyonder, nearbyEntities);
        }

        recentlyAffected.addAll(nearbyEntities.stream().map(LivingEntity::getUniqueId).toList());
        Bukkit.getScheduler().runTaskLater(plugin, () -> nearbyEntities.stream().map(LivingEntity::getUniqueId).toList().forEach(recentlyAffected::remove), 200);
    }

    private void castSlow(Beyonder beyonder, List<LivingEntity> nearbyEntities) {
        runTaskWithDuration(1, beyonder.getCurrentMultiplier() * 20 * 3, () -> {
            for(LivingEntity nearbyEntity : nearbyEntities) {
                if(!nearbyEntity.isValid())
                    continue;
                nearbyEntity.setVelocity(new Vector(0, 0, 0));
                nearbyEntity.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(20, 4));
            }
        }, null);
    }

    private void castCorruption(Beyonder beyonder, List<LivingEntity> nearbyEntities) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        runTaskWithDuration(5, beyonder.getCurrentMultiplier() * 5 * 20, () -> {
            for(LivingEntity nearbyEntity : nearbyEntities) {
                if(!nearbyEntity.isValid())
                    continue;
                ParticleSpawner.displayParticles(world, Particle.DUST, nearbyEntity.getEyeLocation().subtract(0, .5, 0), 10, 0.5, 0.9, 0.5, 0, dustOptions, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, nearbyEntity.getEyeLocation().subtract(0, .5, 0), 10, 0.5, 0.9, 0.5, 0, dustOptions2, 200);
                Beyonder beyonderTarget = LOTM.getInstance().getBeyonder(nearbyEntity.getUniqueId());
                if(beyonderTarget != null) {
                    beyonderTarget.looseControl(0.08);
                }
                else {
                    nearbyEntity.addPotionEffects(List.of(corruptionEffects));
                }

            }
        }, null);
    }

    private void castDeath(Beyonder beyonder, List<LivingEntity> nearbyEntities) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        runTaskWithDuration(10, beyonder.getCurrentMultiplier() * 20, () -> {
            for(LivingEntity nearbyEntity : nearbyEntities) {
                if(!nearbyEntity.isValid())
                    continue;
                ParticleSpawner.displayParticles(world, Particle.DUST, nearbyEntity.getEyeLocation().subtract(0, .5, 0), 10, 0.5, 0.9, 0.5, 0, dustOptions3, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, nearbyEntity.getEyeLocation().subtract(0, .5, 0), 10, 0.5, 0.9, 0.5, 0, dustOptions2, 200);
                nearbyEntity.damage(7.5 * beyonder.getCurrentMultiplier(), entity);
            }
        }, null);
    }
}
