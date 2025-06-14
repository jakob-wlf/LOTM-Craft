package de.jakob.lotm.pathways.impl.chained.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class WerewolfTransformation extends ToggleableAbility {

    private final Particle.DustOptions grayDust = new Particle.DustOptions(org.bukkit.Color.fromRGB(80, 80, 80), 1.5f);
    private final Particle.DustOptions grayDustEars = new Particle.DustOptions(org.bukkit.Color.fromRGB(110, 100, 90), .5f);

    public WerewolfTransformation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        constantSpiritualityCost = 1;
        tickDelay = 1;
        cooldownTicksAfterDisable = 35;
    }

    @Override
    protected void start(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        if(entity.getScoreboardTags().contains("lotm_is_transformed")) {
            casting.remove(beyonder);
            return;
        }

        LOTM.getInstance().getResetScale().add(beyonder);
        AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
        if(attribute != null) {
            if(attribute.getBaseValue() != 1) {
                casting.remove(beyonder);
                return;
            }
            attribute.setBaseValue(1.15);
        }

        entity.getScoreboardTags().add("lotm_is_transformed");

        int strengthMultiplier = entity.getPotionEffect(PotionEffectType.STRENGTH) != null ? entity.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() + 1 : 1;
        int speedMultiplier = entity.getPotionEffect(PotionEffectType.SPEED) != null ? entity.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 3 : 1;
        int resistanceMultiplier = Math.min(3, entity.getPotionEffect(PotionEffectType.RESISTANCE) != null ? entity.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() + 1 : 1);
        int jumpBoostMultiplier = entity.getPotionEffect(PotionEffectType.JUMP_BOOST) != null ? entity.getPotionEffect(PotionEffectType.JUMP_BOOST).getAmplifier() + 2 : 1;

        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20, strengthMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speedMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, resistanceMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20, jumpBoostMultiplier, false, false, false));
    }

    @Override
    protected void stop(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        if(entity == null || !entity.isValid())
            return;

        entity.getScoreboardTags().remove("lotm_is_transformed");

        LOTM.getInstance().getResetScale().remove(beyonder);
        AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
        if(attribute != null) {
            attribute.setBaseValue(1);
        }
    }

    @Override
    protected void impl(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();


        ParticleSpawner.displayParticles(Particle.DUST, entity.getEyeLocation().subtract(0, (entity.getHeight() * 1.15) / 2, 0), 12, .25, (entity.getHeight() / 1.75) / 2, .25, 0, grayDust, 180);
        ParticleUtil.drawShape(entity.getEyeLocation().add(0, .5, 0).add(entity.getLocation().getDirection().normalize().multiply(-.15)), entity.getEyeLocation().getDirection().normalize(), .5, Particle.DUST, ParticleUtil.Shape.WOLF_EARS, grayDustEars);

        int strengthMultiplier = entity.getPotionEffect(PotionEffectType.STRENGTH) != null ? entity.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() : 0;
        int speedMultiplier = entity.getPotionEffect(PotionEffectType.SPEED) != null ? entity.getPotionEffect(PotionEffectType.SPEED).getAmplifier() : 0;
        int resistanceMultiplier = entity.getPotionEffect(PotionEffectType.RESISTANCE) != null ? entity.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() : 0;
        int jumpBoostMultiplier = entity.getPotionEffect(PotionEffectType.JUMP_BOOST) != null ? entity.getPotionEffect(PotionEffectType.JUMP_BOOST).getAmplifier() : 0;

        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20, strengthMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speedMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, resistanceMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20, jumpBoostMultiplier, false, false, false));
    }
}
