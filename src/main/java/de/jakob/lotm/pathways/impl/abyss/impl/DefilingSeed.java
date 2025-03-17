package de.jakob.lotm.pathways.impl.abyss.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class DefilingSeed extends Ability {
    public DefilingSeed(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity target = getTargetEntity(beyonder.getEntity(), 25);
        if(target == null) return;

        if(target.getScoreboardTags().contains("defiling_seed")) return;

        if(!beyonder.removeSpirituality(55))
            return;

        playSound(beyonder, Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1, false);
        playSound(beyonder, Sound.ENTITY_WITHER_HURT, 1, .75f, false);

        Beyonder beyonderTarget = LOTM.getInstance().getBeyonder(target.getUniqueId());
        if(beyonderTarget != null && beyonderTarget.getCurrentSequence() <= beyonder.getCurrentSequence()) {
            if(beyonderTarget.getCurrentSequence() < beyonder.getCurrentSequence() || random.nextInt(5) == 0) {
                beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2, false, false, false));
                beyonder.getEntity().damage(18);
                return;
            }
        }

        target.getScoreboardTags().add("defiling_seed");

        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(80, 20, 20), 1f);

        if(beyonder.getEntity() instanceof Player player) {
            player.spawnParticle(Particle.DUST, target.getEyeLocation().subtract(0, .4, 0), 120, .5, .8, .5, dust);
            player.spawnParticle(Particle.WITCH, target.getEyeLocation().subtract(0, .4, 0), 20, .2, .2, .2, 0);
        }

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 10 * 60 || !target.isValid()) {
                    target.getScoreboardTags().remove("defiling_seed");
                    cancel();
                    return;
                }

                if(random.nextInt(8) == 0) {
                    target.damage(10 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
                    ParticleSpawner.displayParticles(target.getWorld(), Particle.DUST, target.getEyeLocation().subtract(0, .4, 0), 120, .5, .8, .5, 0, dust, 200);
                    ParticleSpawner.displayParticles(target.getWorld(), Particle.WITCH, target.getEyeLocation().subtract(0, .4, 0), 20,.2, .2, .2, 0, 200);
                }

                if(random.nextInt(80) == 0) {
                    if(beyonderTarget != null)
                        beyonderTarget.looseControl(.2);
                }

                if(random.nextInt(16) == 0) {
                    if(beyonderTarget != null)
                        beyonderTarget.useRandomAbility();
                }

                if(random.nextInt(500) == 0) {
                    target.damage(18 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
                    target.setVelocity((new Vector(random.nextDouble(-1, 1), random.nextDouble( 1), random.nextDouble(-1, 1)).normalize()));
                    ParticleSpawner.displayParticles(target.getWorld(), Particle.DUST, target.getEyeLocation().subtract(0, .4, 0), 20,.2, .2, .2, 0, dust, 200);
                    ParticleSpawner.displayParticles(target.getWorld(), Particle.WITCH, target.getEyeLocation().subtract(0, .4, 0), 20,.2, .2, .2, 0, 200);
                    ParticleSpawner.displayParticles(target.getWorld(), Particle.EXPLOSION, target.getEyeLocation().subtract(0, .4, 0), 2,.2, .2, .2, 0, 200);
                }

                counter++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
