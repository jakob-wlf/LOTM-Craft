package de.jakob.lotm.pathways.impl.chained.abilities;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class DarknessEncroachment extends Ability {

    private final Particle.DustOptions blackDust = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 5f);

    public DarknessEncroachment(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20 * 6;
        spirituality = 25;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getLocation();

        AtomicDouble radiusAtomic = new AtomicDouble(.6);

        runTaskWithDuration(3, 20 * 4, () -> {
            double radius = radiusAtomic.getAndAdd(0.25);
            ParticleSpawner.displayParticles(Particle.DUST, location, (int) (100 * radius), radius, .05, radius, 0, blackDust, 180);
            getNearbyLivingEntities(entity, radius, location, entity.getWorld()).stream().filter(e -> e.getLocation().distance(location) > radius - .8).forEach(e -> e.damage(19 * beyonder.getCurrentMultiplier(), entity));
            addPotionEffectToNearbyEntities(entity, radius, location, entity.getWorld(), new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 2, false, false, false), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, 5, false, false, false));
        });
    }
}
