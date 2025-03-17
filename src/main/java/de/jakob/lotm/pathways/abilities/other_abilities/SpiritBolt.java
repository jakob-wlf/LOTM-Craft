package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class SpiritBolt extends Ability {

    private final Set<Beyonder> cooldown = new HashSet<>();

    public SpiritBolt(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(cooldown.contains(beyonder))
            return;

        cooldown.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Vector direction = getTargetLocation(entity, 27).toVector().subtract(entity.getEyeLocation().toVector()).normalize();
        Location loc = entity.getEyeLocation().add(direction);

        launchParticleProjectile(loc, direction, Particle.DUST, new Particle.DustOptions(Color.fromRGB(5, 100, 230), 1), 19, 13, 1, 10, entity, 11, .3);

        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldown.remove(beyonder), 18);
    }
}
