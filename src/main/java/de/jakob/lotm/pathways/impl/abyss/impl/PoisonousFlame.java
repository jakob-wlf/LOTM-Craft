package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class PoisonousFlame extends Ability {

    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(35, 168, 102), 1f);
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(81, 252, 147), 1f);

    public PoisonousFlame(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(12))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().normalize().multiply(1.5));
        Vector direction = entity.getEyeLocation().getDirection().normalize();
        World world = entity.getWorld();

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        damageNearbyEntities(8, beyonder.getCurrentMultiplier(), entity, 2, location, world, true, 20 * 3);
        addPotionEffectToNearbyEntities(entity, 4, location, world, PotionEffectType.POISON.createEffect(20 * 8, 1));

        runTaskWithDuration(5, 10, () -> {
            ParticleUtil.drawLine(location, direction, Particle.SOUL_FIRE_FLAME, .5, world, null, 5, .4, 2);
            ParticleUtil.drawLine(location, direction, Particle.SMOKE, .5, world, null, 20, .4, 2);
            ParticleUtil.drawLine(location, direction, Particle.DUST, .5, world, dustOptions, 20, .4, 2);
            ParticleUtil.drawLine(location, direction, Particle.DUST, .5, world, dustOptions2, 20, .4, 2);
        }, null);
    }
}
