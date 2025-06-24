package de.jakob.lotm.pathways.impl.chained.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WraithTransformation extends ToggleableAbility {

    private final Particle.DustOptions whiteDust = new Particle.DustOptions(org.bukkit.Color.fromRGB(200, 200, 200), 1.4f);

    public WraithTransformation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
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
        if(entity.getScoreboardTags().contains("lotm_is_transformed") || (entity instanceof Player player && player.getAllowFlight())) {
            casting.remove(beyonder);
            return;
        }
        LOTM.getInstance().getResetScale().add(beyonder);

        entity.getScoreboardTags().add("lotm_is_transformed");

        if(entity instanceof Player player)
            player.setAllowFlight(true);

        int strengthMultiplier = entity.getPotionEffect(PotionEffectType.STRENGTH) != null ? entity.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() + 2 : 1;
        int regenMultiplier = entity.getPotionEffect(PotionEffectType.REGENERATION) != null ? entity.getPotionEffect(PotionEffectType.REGENERATION).getAmplifier() + 2 : 1;
        int resistanceMultiplier = Math.min(3, entity.getPotionEffect(PotionEffectType.RESISTANCE) != null ? entity.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() + 1 : 1);
        int speedMultiplier = entity.getPotionEffect(PotionEffectType.SPEED) != null ? entity.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 3 : 1;

        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20, strengthMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, regenMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, resistanceMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speedMultiplier, false, false, false));
    }

    @Override
    protected void stop(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        if(entity == null)
            return;

        if(entity instanceof Player player) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setInvisible(false);
        }

        entity.getScoreboardTags().remove("lotm_is_transformed");
        LOTM.getInstance().getResetScale().remove(beyonder);

        if(!entity.isValid())
            return;

        AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
        if(attribute != null) {
            attribute.setBaseValue(1);
        }
    }

    @Override
    protected void impl(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        if(entity instanceof Player player) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setInvisible(true);

            player.spawnParticle(Particle.DUST, player.getEyeLocation().subtract(0, .4, 0), 8, .4, .45, .4, 0, whiteDust);
        }

        int strengthMultiplier = entity.getPotionEffect(PotionEffectType.STRENGTH) != null ? entity.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() : 0;
        int regenMultiplier = entity.getPotionEffect(PotionEffectType.REGENERATION) != null ? entity.getPotionEffect(PotionEffectType.REGENERATION).getAmplifier() : 0;
        int resistanceMultiplier = entity.getPotionEffect(PotionEffectType.RESISTANCE) != null ? entity.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() : 0;
        int speedMultiplier = entity.getPotionEffect(PotionEffectType.SPEED) != null ? entity.getPotionEffect(PotionEffectType.SPEED).getAmplifier() : 0;

        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20, strengthMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, regenMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, resistanceMultiplier, false, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speedMultiplier, false, false, false));
    }
}
