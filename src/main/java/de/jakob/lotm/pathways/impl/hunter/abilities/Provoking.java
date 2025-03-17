package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
public class Provoking extends Ability {

    Set<UUID> provokedEntities = new HashSet<>();

    public Provoking(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(20))
            return;

        LivingEntity caster = beyonder.getEntity();

        double multiplier = beyonder.getCurrentMultiplier();

        World world = caster.getWorld();

        Random random = new Random();

        world.playSound(caster.getEyeLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 1, random.nextFloat(.6f, 1));
        world.playSound(caster.getEyeLocation(), Sound.ENTITY_WOLF_GROWL, 1, random.nextFloat(.6f, 1));

        world.spawnParticle(Particle.SMOKE, caster.getEyeLocation(), 25, .4, .4, .4, 0);

        for(LivingEntity entity : getNearbyLivingEntities(caster, 12 * multiplier, caster.getEyeLocation(), world)) {
            if(provokedEntities.contains(entity.getUniqueId()))
                continue;

            provokedEntities.add(entity.getUniqueId());

            int time = (int) (20 * Math.round(4f * multiplier));
            int amplifier = Math.min(1, (int) Math.round(multiplier * .2));

            PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, time, amplifier);
            PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, time, amplifier);

            entity.addPotionEffect(weakness);
            entity.addPotionEffect(slowness);

            if(entity instanceof Mob)
                ((Mob) entity).setTarget(caster);

            Beyonder beyonderTarget = LOTM.getInstance().getBeyonder(entity.getUniqueId());
            if(beyonderTarget != null) {
                beyonderTarget.addMultiplierModifier(.7, time);
            }

            world.playSound(entity.getEyeLocation(), Sound.ENTITY_VILLAGER_NO, 1, random.nextFloat(.6f, 1));

            world.spawnParticle(Particle.ANGRY_VILLAGER, entity.getEyeLocation(), 15, .2, .4, .2, 0);

            new BukkitRunnable() {

                @Override
                public void run() {
                    provokedEntities.remove(entity.getUniqueId());
                }
            }.runTaskLater(LOTM.getInstance(), time);
        }
    }
}
