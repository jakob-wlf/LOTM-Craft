package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class DevilTransformation extends Ability {

    private final Set<Beyonder> transformed = new HashSet<>();
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(168, 48, 35), 2f);
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(204, 123, 53), 2f);
    private final Particle.DustOptions dustOptions3 = new Particle.DustOptions(Color.fromRGB(75, 20, 20), 2.5f);
    private final Particle.DustOptions dustOptions4 = new Particle.DustOptions(Color.fromRGB(200, 170, 130), 1.5f);

    private final PotionEffect[] effects = new PotionEffect[]{
        new PotionEffect(PotionEffectType.STRENGTH, 20, 1),
                new PotionEffect(PotionEffectType.RESISTANCE, 20, 0, false, false, false),
                new PotionEffect(PotionEffectType.SPEED, 20, 1, false, false, false),
                new PotionEffect(PotionEffectType.HEALTH_BOOST, 20, 3, false, false, false),
    };

    public DevilTransformation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }


    //TODO: Add sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(transformed.contains(beyonder)) {
            LOTM.getInstance().getResetScale().remove(beyonder);
            transformed.remove(beyonder);
            return;
        }

        LivingEntity entity = beyonder.getEntity();

        transformed.add(beyonder);
        LOTM.getInstance().getResetScale().add(beyonder);
        AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
        if(attribute != null) {
            if(attribute.getBaseValue() != 1) {
                transformed.remove(beyonder);
                return;
            }
            attribute.setBaseValue(2);
        }


        new BukkitRunnable() {

            int counter = 0;
            @Override
            public void run() {
                if(counter > 20 * 5 && abilityType == AbilityType.RECORDED) {
                    transformed.remove(beyonder);
                }

                if(!beyonder.removeSpirituality(random.nextInt(2) + 1)) {
                    transformed.remove(beyonder);
                }

                if(!entity.isValid() || entity.isDead()) {
                    transformed.remove(beyonder);
                }

                if(!transformed.contains(beyonder)) {
                    AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
                    if(attribute != null)
                        attribute.setBaseValue(1);
                    cancel();
                    return;
                }

                World world = entity.getWorld();

                beyonder.addMultiplierModifier(1.2, 4);

                entity.addPotionEffects(List.of(effects));

                ParticleSpawner.displayParticles(world, Particle.DUST, entity.getEyeLocation().add(0, -1.3, 0).add(entity.getLocation().getDirection().normalize().multiply(-.5)), 8, 0.8, 1, 0.8, 0, dustOptions, 200);
                ParticleSpawner.displayParticles(world, Particle.DUST, entity.getEyeLocation().add(0, -1.3, 0).add(entity.getLocation().getDirection().normalize().multiply(-.5)), 8, 0.8, 1, 0.8, 0, dustOptions2, 200);
                ParticleSpawner.displayParticles(world, Particle.FLAME, entity.getEyeLocation().add(0, -1.3, 0).add(entity.getLocation().getDirection().normalize().multiply(-.5)), 8, 0.8, 1, 0.8, 0, 200);

                ParticleUtil.drawShape(entity.getEyeLocation().add(0, -.5, 0).add(entity.getLocation().getDirection().normalize().multiply(-1.2)), entity.getEyeLocation().getDirection().normalize(), 4, Particle.DUST, ParticleUtil.Shape.CLASSIC_WINGS, dustOptions3);
                ParticleUtil.drawShape(entity.getEyeLocation().add(0, 1, 0).add(entity.getLocation().getDirection().normalize().multiply(-.7)), entity.getEyeLocation().getDirection().normalize(), 1.5, Particle.DUST, ParticleUtil.Shape.HORNS, dustOptions4);

                counter++;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !transformed.contains(beyonder);
    }

}
