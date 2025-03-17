package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.EntityUtil;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class GodSaysItsEffective extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1.75f);

    public GodSaysItsEffective(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = "notar_buff";
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            return;
        }

        if(!beyonder.removeSpirituality(48))
            return;

        if(beyonder.getEntity() instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
        }

        List<LivingEntity> affected = new ArrayList<>(beyonder.getEntity().getNearbyEntities(15, 8, 15).stream().filter(e -> EntityUtil.areOnTheSameTeam(e, beyonder.getEntity())).filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e).toList());
        affected.add(beyonder.getEntity());

        runTaskWithDuration(2, 20 * 4, () -> {
            if(beyonder.getEntity() == null || !beyonder.getEntity().isValid()) {
                return;
            }

            for(LivingEntity e : affected) {
                e.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 10, 2, false, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 3, false, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 3, false, false, false));

                Beyonder b = LOTM.getInstance().getBeyonder(e.getUniqueId());
                if(b != null) {
                    b.addMultiplierModifier(1.6, 1);
                }
            }

            ParticleSpawner.displayParticles(beyonder.getEntity().getWorld(), Particle.DUST, beyonder.getEntity().getLocation().add(0, .5, 0), 10, 10, 3, 10, 0, dust, 200);
            ParticleSpawner.displayParticles(beyonder.getEntity().getWorld(), Particle.END_ROD, beyonder.getEntity().getLocation().add(0, .5, 0), 3, 10, 3, 10, 0, 200);
        }, () -> casting.remove(beyonder));
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }
}
