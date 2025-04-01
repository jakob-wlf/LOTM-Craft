package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Random;

public abstract class Spirit {

    protected static final Random random = new Random();

    protected static BeyonderSpirit spawnSpirit(
            Location location,
            EntityType entityType,
            boolean defaultAI,
            boolean showSpirit,
            int optimalDistance,
            double size,
            int sequence,
            Particle.DustOptions[] dustOptions,
            Ability... abilities
    ) {

        if(location.getWorld() == null)
            return null;

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entity.setVisibleByDefault(false);
        entity.setSilent(true);
        entity.getScoreboardTags().add("no_spawn");
        entity.getScoreboardTags().add("no_drop");
        entity.getScoreboardTags().add("dead");
        entity.getAttribute(Attribute.SCALE).setBaseValue(size);
        entity.setVisualFire(false);
        entity.setFireTicks(0);

        BeyonderSpirit beyonderSpirit = LOTM.getInstance().createBeyonderSpirit(entity.getUniqueId(), null, sequence);

        if(beyonderSpirit == null)
            return null;

        beyonderSpirit.setDefaultAI(defaultAI);
        beyonderSpirit.setDustAmount(20);
        beyonderSpirit.setShowEntity(showSpirit);
        if(dustOptions != null)
            beyonderSpirit.setDust(dustOptions);
        beyonderSpirit.setOptimalDistance(optimalDistance);
        beyonderSpirit.setDustSizes(new double[] {.35, entity.getHeight() / 2, .35});
        for(Ability ability : abilities) {
            beyonderSpirit.addAbility(ability);
        }

        return beyonderSpirit;
    }

}
