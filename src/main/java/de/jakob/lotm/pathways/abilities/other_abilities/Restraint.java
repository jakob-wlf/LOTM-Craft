package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

@NoArgsConstructor
public class Restraint extends Ability {

    private final HashMap<Beyonder, Entity> restraintEnemies = new HashMap<>();

    public Restraint(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }


    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        restraintEnemies.remove(beyonder);

        LivingEntity target = getTargetEntity(beyonder.getEntity(), 10, EntityType.ALLAY);

        if(target == null)
            return;

        LivingEntity entity = beyonder.getEntity();
        restraintEnemies.put(beyonder, target);

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!target.isValid() || !entity.isValid() || target.getWorld() != entity.getWorld() || target.getLocation().distance(entity.getLocation()) > 25) {
                    restraintEnemies.remove(beyonder);
                    cancel();
                    return;
                }

                if(restraintEnemies.containsKey(beyonder) && restraintEnemies.get(beyonder) != target) {
                    cancel();
                    return;
                }

                target.setVelocity(new Vector(0, 0, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 1, false, false, false));

                ParticleUtil.drawCircle(target.getEyeLocation().subtract(0, .5, 0), new Vector(0, 1, 0), .8, Particle.DUST, new Particle.DustOptions(Color.fromRGB(196, 120, 33), 1f), 30);
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
