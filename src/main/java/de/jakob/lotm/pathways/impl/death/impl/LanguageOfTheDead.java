package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class LanguageOfTheDead extends Ability {

    public LanguageOfTheDead(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        hasCooldown = true;
        cooldownTicks = 20 * 8;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = getTargetEntity(entity, 25);

        if(target == null) {
            entity.sendMessage(pathway.getColorPrefix() + "No target found.");
            onCooldown.remove(beyonder);
            return;
        }

        if(!beyonder.removeSpirituality(18))
            return;

        entity.getWorld().playSound(entity, Sound.ENTITY_WITHER_HURT, 1, 1);

        runTaskWithDuration(2, 20 * 8, () -> {
            if(!target.isValid())
                return;
            ParticleSpawner.displayParticles(target.getWorld(), Particle.LARGE_SMOKE, target.getEyeLocation().subtract(0, target.getHeight() / 2f, 0), 25, .2, target.getHeight() / 2.8, .2, 0, 200);
            ParticleSpawner.displayParticles(target.getWorld(), Particle.SOUL, target.getEyeLocation().subtract(0, target.getHeight() / 2f, 0), 25, .2, target.getHeight() / 2.8, .2, 0, 200);

            target.damage(1.5 * beyonder.getCurrentMultiplier(), entity);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 10, false, false, false));
        }, null);
    }
}
