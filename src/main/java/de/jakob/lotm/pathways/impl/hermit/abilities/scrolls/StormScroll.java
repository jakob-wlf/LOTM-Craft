package de.jakob.lotm.pathways.impl.hermit.abilities.scrolls;

import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.pathways.TyrantUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StormScroll extends MysticalScroll{

    final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(30, 120, 255), 1.25f);

    public StormScroll(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onUse(LivingEntity entity) {
        Location loc = getTargetLocation(entity, 20);
        World world = entity.getWorld();

        runTaskWithDuration(7, 20 * 16,  () -> {
            world.playSound(loc, Sound.WEATHER_RAIN, 1, 1);

            ParticleSpawner.displayParticles(world, Particle.FALLING_WATER, loc, 500, 7, 7, 7, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.CLOUD, loc.clone().add(0, 9, 0), 500, 7, 0, 7, 0, 200);
            ParticleSpawner.displayParticles(world, Particle.DUST, loc, 500, 7, 7, 7, 0, dust, 200);

            if(random.nextInt(8) == 0) {
                Location l = loc.clone().add(random.nextDouble(-6, 6), 0, random.nextDouble(-6, 6));
                strikeLightning(entity, l);

            }

            damageNearbyEntities(3.75, 1, entity, 10, loc, world, false, 0, 10, false);

        }, null);
    }

    private void strikeLightning(LivingEntity entity, Location loc) {

        World world = loc.getWorld();

        if (world == null)
            return;

        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);

        damageNearbyEntities(19.5, 1, entity, 2, loc, world, false, 0, 10, false);

        while(!loc.getBlock().getType().isSolid() && loc.getY() >= 0) {
            loc.subtract(0, .5, 0);
        }
        Location top = loc.clone().add(0, 16, 0);

        Location lightningLoc = top.clone();
        int breakoutCounter = 0;
        while(breakoutCounter < 120 && !lightningLoc.getBlock().getType().isSolid()) {
            ParticleSpawner.displayParticles(world, Particle.DUST, lightningLoc, 3, 0, 0, 0, 0, TyrantUtil.blueDust, 1040);
            if(breakoutCounter > 110)
                lightningLoc.add(0, -.5, 0);
            else
                lightningLoc.add(random.nextDouble(-.25, .25), -.5, random.nextDouble(-.25, .25));
            breakoutCounter++;
        }

        for(int i = 0; i < random.nextInt(3) + 1; i++) {
            Location branchLoc = top.clone();
            int counter = 0;
            while(counter < 90 && !branchLoc.getBlock().getType().isSolid()) {
                ParticleSpawner.displayParticles(world, Particle.DUST, branchLoc, 3, 0, 0, 0, 0, TyrantUtil.blueDust2, 1040);
                branchLoc.add(random.nextDouble(-.25, .25), -.1, random.nextDouble(-.25, .25));
                counter++;
            }
        }
    }

}
