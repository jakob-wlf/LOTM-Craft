package de.jakob.lotm.pathways.impl.hermit.abilities;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.*;
import de.jakob.lotm.util.pathways.TyrantUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@NoArgsConstructor
public class SpellCasting extends SelectableAbility {

    private final HashMap<Beyonder, Location> windCastingLocations = new HashMap<>();
    private final Particle.DustOptions waterDust = new Particle.DustOptions(Color.fromRGB(58, 100, 189), 1);
    private final Particle.DustOptions goldDust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 1.75f);

    public SpellCasting(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {
                "Flames", "Wind", "Lightning", "Wave", "Purification"
        };
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 19,
                1, 14,
                2, 30,
                3, 30,
                4, 30
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {

        switch(ability) {
            case 0 -> castFlames(beyonder);
            case 1 -> castWind(beyonder);
            case 2 -> strikeLightning(beyonder);
            case 3 -> castWave(beyonder);
            case 4 -> castPurification(beyonder);
        }
    }

    private void castPurification(Beyonder beyonder) {
        if(beyonder.getEntity() instanceof Player player && beyonder instanceof BeyonderPlayer) {
            if(!player.getInventory().contains(Material.SUNFLOWER)) {
                player.sendMessage(pathway.getColorPrefix() + "You need a sunflower to cast this spell.");
                return;
            }

            ItemsUtil.removeItem(player, new ItemStack(Material.SUNFLOWER));
        }

        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getEyeLocation().add(0, .2, 0);

        entity.getWorld().playSound(loc, Sound.ENTITY_BREEZE_HURT, .4f, .6f);
        entity.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, .4f, .6f);

        AtomicDouble i = new AtomicDouble(2.5);
        runTaskWithDuration(0, 15, () -> {
            List<LivingEntity> entities = getNearbyLivingEntities(entity, i.get(), loc, entity.getWorld()).stream().filter(e -> e.getEyeLocation().distance(loc) > i.get() - 1).toList();

            entities.forEach(e -> {
                if(e.getNoDamageTicks() <= 0) {
                    e.damage(15.5 * beyonder.getCurrentSequence(), entity);
                    e.setNoDamageTicks(10);
                }
            });

            ParticleUtil.drawCircle(loc.clone().add(0, .5, 0), new Vector(0, 1, 0), i.get(), Particle.DUST, goldDust, 75, .2);
            ParticleUtil.drawCircle(loc.clone().subtract(0, .8, 0), new Vector(0, 1, 0), i.get(), Particle.DUST, goldDust, 75, .2);

            i.addAndGet(.5);
        }, null);
    }

    private void strikeLightning(Beyonder beyonder) {
        if(beyonder.getEntity() instanceof Player player && beyonder instanceof BeyonderPlayer) {
            if(!player.getInventory().contains(Material.RAW_COPPER)) {
                player.sendMessage(pathway.getColorPrefix() + "You need raw copper to cast this spell.");
                return;
            }

            ItemsUtil.removeItem(player, new ItemStack(Material.RAW_COPPER));
        }

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 19);

        World world = loc.getWorld();

        if (world == null)
            return;

        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);

        damageNearbyEntities(16, beyonder.getCurrentMultiplier(), entity, 2, loc, world);

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

    private void castFlames(Beyonder beyonder) {
        if(beyonder.getEntity() instanceof Player player && beyonder instanceof BeyonderPlayer) {
            if(!player.getInventory().contains(Material.FLINT)) {
                player.sendMessage(pathway.getColorPrefix() + "You need flint to cast this spell.");
                return;
            }

            ItemsUtil.removeItem(player, new ItemStack(Material.FLINT));
        }

        LivingEntity target = getTargetEntity(beyonder.getEntity(), 25);
        Location startLocation = target != null ? target.getLocation() : getLocationLookedAt(beyonder.getEntity(), 25, true);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();

        world.playSound(startLocation, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        damageNearbyEntities(16.5, beyonder.getCurrentMultiplier(), entity, 7.5, startLocation, world, true, 20 * 2);

        new BukkitRunnable() {
            double radius = 1.25;
            @Override
            public void run() {

                double height = .65f / (.5 * (radius - 2));

                Location loc = startLocation.clone();
                loc.setY(loc.getY() + height);

                ParticleUtil.drawParticleCircle(loc, radius, Particle.FLAME, null, 40, .5, 5);
                if(beyonder.isGriefingEnabled())
                    for(Block block : BlockUtil.getPassableBlocksInCircle(loc, radius, 50)) {
                        if(random.nextInt(8) == 0)
                            block.setType(Material.FIRE);
                    }

                radius += .25;
                if(radius > 8)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void castWind(Beyonder beyonder) {
        if(beyonder.getEntity() instanceof Player player && beyonder instanceof BeyonderPlayer) {
            if(!player.getInventory().contains(Material.FEATHER)) {
                player.sendMessage(pathway.getColorPrefix() + "You need a feather to cast this spell.");
                return;
            }

            ItemsUtil.removeItem(player, new ItemStack(Material.FEATHER));
        }

        final Location location = beyonder.getEntity().getEyeLocation();
        final World world = beyonder.getEntity().getWorld();
        final Vector dir = location.getDirection().normalize();

        windCastingLocations.put(beyonder, location);

        new BukkitRunnable() {

            int timer = 0;

            @Override
            public void run() {
                timer += 2;

                if(windCastingLocations.get(beyonder) != location) {
                    cancel();
                    return;
                }

                if(timer > 20 * 10 || !windCastingLocations.containsKey(beyonder))  {
                    windCastingLocations.remove(beyonder);
                    cancel();
                    return;
                }

                for(int i = 0; i < 8; i++) {
                    Location tempLoc = location.clone().add(0, 1.5, 0).add(random.nextInt(16) - 8, random.nextInt(6) - 3, random.nextInt(16) -8);
                    world.spawnParticle(Particle.CLOUD, tempLoc, 0, dir.getX(), dir.getY(), dir.getZ(), .5);
                }

                world.playSound(location, Sound.ENTITY_BREEZE_IDLE_GROUND, .2f, .3f);

                for(Entity entity : world.getNearbyEntities(location, 11, 11, 11)) {
                    if(entity instanceof LivingEntity && entity != beyonder.getEntity()) {
                        entity.setVelocity(dir.clone().multiply(.6));
                    }
                }

            }
        }.runTaskTimer(plugin, 0, 2);
    }

    private void castWave(Beyonder beyonder) {
        if(beyonder.getEntity() instanceof Player player && beyonder instanceof BeyonderPlayer) {
            if(!player.getInventory().contains(Material.INK_SAC)) {
                player.sendMessage(pathway.getColorPrefix() + "You need an ink sac to cast this spell.");
                return;
            }

            ItemsUtil.removeItem(player, new ItemStack(Material.INK_SAC));
        }

        LivingEntity entity = beyonder.getEntity();
        Location startLocation = entity.getLocation();

        World world = entity.getWorld();

        world.playSound(startLocation, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.55f, .8f));
        world.playSound(startLocation, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.55f, .8f));
        world.playSound(startLocation, Sound.ENTITY_PLAYER_SPLASH, 1, random.nextFloat(.55f, .8f));

        new BukkitRunnable() {
            double radius = 2;
            @Override
            public void run() {

                double height = .65f / (.5 * (radius - 2));

                Location loc = startLocation.clone();
                loc.setY(loc.getY() + height);

                ParticleUtil.drawParticleCircle(loc, radius, Particle.DUST, waterDust, 45, .5, 14);

                for(LivingEntity target : getNearbyLivingEntities(entity, radius, startLocation, entity.getWorld())) {
                    if(target.getWorld() != entity.getWorld())
                        continue;

                    double distance = target.getLocation().distance(entity.getLocation());
                    if(Math.sqrt((distance - radius) * (distance - radius)) <= 1.5) {
                        target.damage(17 * beyonder.getCurrentMultiplier(), entity);
                        target.setNoDamageTicks(14);
                        world.playSound(target.getEyeLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, 1, random.nextFloat(.6f, 1f));
                    }
                }

                radius += .5;
                if(radius > 15)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }
}
