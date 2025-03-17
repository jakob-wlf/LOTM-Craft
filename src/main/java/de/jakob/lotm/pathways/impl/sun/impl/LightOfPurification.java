package de.jakob.lotm.pathways.impl.sun.impl;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

@NoArgsConstructor
public class LightOfPurification extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2.25f);

    public LightOfPurification(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(50))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getEyeLocation().add(0, .2, 0);

        entity.getWorld().playSound(loc, Sound.ENTITY_BREEZE_HURT, .4f, .6f);
        entity.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, .4f, .6f);

        AtomicDouble i = new AtomicDouble(2.5);
        runTaskWithDuration(0, 15, () -> {
            List<LivingEntity> entities = getNearbyLivingEntities(entity, i.get(), loc, entity.getWorld()).stream().filter(e -> e.getEyeLocation().distance(loc) > i.get() - 1).toList();

            entities.forEach(e -> {
                if(e.getNoDamageTicks() <= 0) {
                    e.damage(24 * beyonder.getCurrentSequence(), entity);
                    e.setNoDamageTicks(10);
                }
            });

            ParticleUtil.drawCircle(loc.clone().add(0, .5, 0), new Vector(0, 1, 0), i.get(), Particle.DUST, dust, 75, .2);
            ParticleUtil.drawCircle(loc.clone().subtract(0, .7, 0), new Vector(0, 1, 0), i.get(), Particle.DUST, dust, 75, .2);
            ParticleUtil.drawCircle(loc.clone().subtract(0, 1.5, 0), new Vector(0, 1, 0), i.get(), Particle.DUST, dust, 75, .2);

            i.addAndGet(.75);
        }, null);

    }
}
