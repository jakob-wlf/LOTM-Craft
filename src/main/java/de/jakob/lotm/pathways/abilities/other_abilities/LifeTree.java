package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class LifeTree extends Ability {

    private final Set<Beyonder>  cooldown = new HashSet<>();

    private final Particle.DustOptions trunkDust = new Particle.DustOptions(Color.fromRGB(99, 67, 42), 1.6f);
    private final Particle.DustOptions leavesDust = new Particle.DustOptions(Color.fromRGB(94, 230, 117), 1.6f);

    public LifeTree(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    //TODO: Sounds

    @Override
    public void useAbility(Beyonder beyonder) {
        if(cooldown.contains(beyonder))
            return;

        cooldown.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getLocation();

        runTaskWithDuration(4, 20 * 15, () ->{
            ParticleUtil.drawTree(location, 5, Particle.DUST, Particle.DUST, trunkDust, leavesDust);
            ParticleUtil.drawCircle(location.clone().add(0, .2, 0), new Vector(0, 1,0), 13, Particle.END_ROD, null, 40);
            ParticleSpawner.displayParticles(entity.getWorld(), Particle.HAPPY_VILLAGER, location.clone().add(0, 1, 0), 15, 10, 10, 10, 0, 100);

            List<LivingEntity> allies = getNearbyLivingEntities(null, 13, location, entity.getWorld()).stream().filter(entity1 -> EntityUtil.areOnTheSameTeam(entity1, entity)).toList();
            for(LivingEntity ally : allies) {
                if(ally.getHealth() <= .95) {
                    ally.setHealth(ally.getHealth() + .05);
                }
                ally.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 3, false, false, false));
            }
        }, () -> cooldown.remove(beyonder));
    }
}
