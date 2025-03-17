package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
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
import java.util.Set;

@NoArgsConstructor
public class AvatarOfDesire extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(50, 50, 50), 3f);

    public AvatarOfDesire(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    //TODO: Add sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            LOTM.getInstance().getResetScale().remove(beyonder);
            casting.remove(beyonder);
            return;
        }

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        casting.add(beyonder);
        LOTM.getInstance().getResetScale().add(beyonder);
        AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
        if(attribute != null) {
            if(attribute.getBaseValue() != 1) {
                casting.remove(beyonder);
                return;
            }
            attribute.setBaseValue(.4);
        }


        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 5 && abilityType == AbilityType.RECORDED) {
                    casting.remove(beyonder);
                }

                if(!beyonder.removeSpirituality(1)) {
                    casting.remove(beyonder);
                }

                if(!entity.isValid() || entity.isDead()) {
                    casting.remove(beyonder);
                }

                if(!casting.contains(beyonder)) {
                    AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
                    if(attribute != null)
                        attribute.setBaseValue(1);
                    cancel();
                    return;
                }

                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 5, false, false, false));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 3, false, false, false));

                ParticleSpawner.displayParticles(world, Particle.DUST, entity.getLocation(), 200, 1, .2, 1, 0, dustOptions, 200);
                counter++;

            }
        }.runTaskTimer(plugin, 0, 2);
    }
}
