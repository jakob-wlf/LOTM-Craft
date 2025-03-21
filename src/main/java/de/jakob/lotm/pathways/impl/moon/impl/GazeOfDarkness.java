package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class GazeOfDarkness extends Ability implements Listener {

    private final HashMap<UUID, LivingEntity> targetedEntities = new HashMap<>();
    private final GazeOfDarkness instance = this;
    public GazeOfDarkness(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        plugin.registerListener(this);
    }


    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        LivingEntity target = getTargetEntity(entity, 25);

        if(target == null) {
            targetedEntities.remove(beyonder.getUuid());
            return;
        }

        ParticleSpawner.displayParticles(world, Particle.LARGE_SMOKE, target.getEyeLocation().subtract(0, .5, 0), 25, .3, .8, .3, 0, 200);
        ParticleSpawner.displayParticles(world, Particle.WITCH, target.getEyeLocation().subtract(0, .5, 0), 25, .3, .8, .3, 0, 200);

        targetedEntities.put(beyonder.getUuid(), target);

        new BukkitRunnable() {

            int recordTimer = 0;

            @Override
            public void run() {
                if(!targetedEntities.containsKey(beyonder.getUuid()) ||
                        targetedEntities.get(beyonder.getUuid()) == null ||
                        !entity.isValid() ||
                        !targetedEntities.get(beyonder.getUuid()).isValid() ||
                        targetedEntities.get(beyonder.getUuid()).getWorld() != entity.getWorld() ||
                        targetedEntities.get(beyonder.getUuid()) != target ||
                        !beyonder.removeSpirituality(3)
                ) {
                    cancel();
                    return;
                }

                if(target.getEyeLocation().distance(entity.getEyeLocation()) > 35) {
                    targetedEntities.remove(beyonder.getUuid());
                    cancel();
                    return;
                }

                if(recordTimer > 20 * 20 && abilityType == AbilityType.RECORDED) {
                    plugin.removeListener(instance);
                    targetedEntities.remove(beyonder.getUuid());
                    cancel();
                    return;
                }

                ParticleSpawner.displayParticles(world, Particle.LARGE_SMOKE, target.getEyeLocation().subtract(0, .5, 0), 15, .3, .8, .3, 0, 200);

                LivingEntity targetEntity = getTargetEntity(entity, 30, true, 1);
                if(targetEntity == target) {
                    target.damage(3 * beyonder.getCurrentMultiplier(), entity);
                    ParticleSpawner.displayParticles(world, Particle.LARGE_SMOKE, target.getEyeLocation().subtract(0, .5, 0), 40, .3, .8, .3, 0, 200);
                    ParticleSpawner.displayParticles(world, Particle.WITCH, target.getEyeLocation().subtract(0, .5, 0), 40, .3, .8, .3, 0, 200);
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20, 0, false, false, false));

                recordTimer++;
            }
        }.runTaskTimer(plugin, 0, 4);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!targetedEntities.containsKey(event.getEntity().getUniqueId()))
            return;

        LivingEntity entity = targetedEntities.get(event.getEntity().getUniqueId());
        if(entity == null)
            return;

        ParticleSpawner.displayParticles(entity.getWorld(), Particle.LARGE_SMOKE, entity.getEyeLocation().subtract(0, .5, 0), 25, .3, .8, .3, 0, 200);
        ParticleSpawner.displayParticles(entity.getWorld(), Particle.WITCH, entity.getEyeLocation().subtract(0, .5, 0), 25, .3, .8, .3, 0, 200);
        entity.damage(event.getDamage(), event.getEntity());
    }
}
