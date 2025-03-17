package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class BoneSmash extends Ability {

    public BoneSmash(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    public void useAbility(Beyonder beyonder) {
        final LivingEntity entity = beyonder.getEntity();
        final Location location = entity.getLocation().add(0, .25, 0);
        final World world = entity.getWorld();

        world.spawnParticle(Particle.DUST, location, 45, 1, 0, 1, 0, new Particle.DustOptions(Color.fromRGB(180, 180, 180), 1f));

        for (LivingEntity target : getNearbyLivingEntities(entity, 2f, location, world)) {
            target.damage(14 * beyonder.getCurrentMultiplier());
            if (target instanceof Mob mob)
                mob.setTarget(entity);
            target.setVelocity(new Vector(0, .7, 0));
        }
    }
}
