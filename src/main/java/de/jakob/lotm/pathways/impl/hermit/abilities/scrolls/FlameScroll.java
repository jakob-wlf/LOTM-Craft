package de.jakob.lotm.pathways.impl.hermit.abilities.scrolls;

import de.jakob.lotm.util.minecraft.LocationProvider;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.pathways.TyrantUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class FlameScroll extends MysticalScroll{
    public FlameScroll(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onUse(LivingEntity entity) {
        Location loc = getTargetLocation(entity, 25).subtract(0, 2, 0);

        World world = loc.getWorld();
        if(world == null)
            return;

        UUID locationUUID = UUID.randomUUID();

        LocationProvider.setLocation(locationUUID, loc);

        BukkitTask tornadoTask = ParticleUtil.spawnParticleTornado(Particle.FLAME, null, 14, 2.2, 2.2, 20 * 20, 100, 0, locationUUID, 0);
        BukkitTask tornadoTask2 = ParticleUtil.spawnParticleTornado(Particle.FLAME, null, 14, 2.2, 2.2, 20 * 20, 100, 0, locationUUID, 5);

        runTaskWithDuration(20, 20 * 8, () -> {
            damageNearbyEntities(16, 1, entity, 2.2, loc, world, true, 20 * 2, 10, false);
            world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, .5f, .85f);
        }, () -> {
            tornadoTask.cancel();
            tornadoTask2.cancel();
        });
    }
}
