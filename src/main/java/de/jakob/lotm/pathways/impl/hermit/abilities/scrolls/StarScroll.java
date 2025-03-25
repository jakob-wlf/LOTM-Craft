package de.jakob.lotm.pathways.impl.hermit.abilities.scrolls;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;

public class StarScroll extends MysticalScroll{

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(176, 112, 255), 1f);

    public StarScroll(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onUse(LivingEntity entity) {
        Location loc = getTargetLocation(entity, 25);
        World world = entity.getWorld();

        for(int i = 0; i < 14; i++) {
            Location startLoc = loc.clone().add(random.nextDouble(-8, 8), random.nextDouble(8), random.nextDouble(-8, 8));
            runTaskWithDuration(1, 5, () -> ParticleSpawner.displayParticles(world, Particle.DUST, startLoc, 20, .1, .1, .1, 0, dust, 200), null);

            Bukkit.getScheduler().runTaskLater(LOTM.getInstance(),() -> launchParticleProjectile(startLoc, loc.clone().toVector().subtract(startLoc.toVector()).normalize(), Particle.DUST, dust, 25, 5, 1, 14, entity, 11, 0, .3, 0), 5);
        }
    }
}
