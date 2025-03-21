package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class SpellCasting extends SelectableAbility {

    private final HashMap<Beyonder, Location> windCastingLocations = new HashMap<>();

    public SpellCasting(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {
                "Wind", "Electric Shock", "Burning", "Freezing"
        };
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 12,
                1, 10,
                2, 12,
                3, 12
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        if(!(beyonder instanceof BeyonderPlayer) && (ability == 2 || ability == 3)) {
            if(!(beyonder.getCurrentTarget().getLocation().distance(beyonder.getEntity().getLocation()) < 3))
                ability = random.nextInt(2);
        }

        switch(ability) {
            case 0 -> castWind(beyonder);
            case 1 -> castElectricShock(beyonder);
            case 2 -> castBurn(beyonder);
            case 3 -> castFreezing(beyonder);
        }
    }

    private void castFreezing(Beyonder beyonder) {
        final Location location = beyonder.getEntity().getEyeLocation().add(beyonder.getEntity().getEyeLocation().getDirection().multiply(1.5));

        beyonder.getEntity().getWorld().spawnParticle(Particle.SNOWFLAKE, location, 10, 0.1, 0.1, 0.1, .1);
        beyonder.getEntity().getWorld().spawnParticle(Particle.ITEM_SNOWBALL, location, 10, 0.1, 0.1, 0.1, 0.1);

        beyonder.getEntity().getWorld().getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
            if(entity == beyonder.getEntity())
                return;

            entity.setFreezeTicks(20 * 5);
            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(8 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            }
        });

        beyonder.getEntity().getWorld().playSound(location, Sound.BLOCK_SNOW_BREAK, 1, 1);

        if (!location.getBlock().getType().isSolid() && location.getBlock().getRelative(0, -1, 0).getType().isSolid() && beyonder.isGriefingEnabled() && random.nextBoolean()) {
            location.getBlock().setType(Material.SNOW);
        }
    }

    private void castWind(Beyonder beyonder) {
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
                    Location tempLoc = location.clone().add(0, 1.5, 0).add(random.nextInt(10) - 5, random.nextInt(6) - 3, random.nextInt(10) - 5);
                    world.spawnParticle(Particle.CLOUD, tempLoc, 0, dir.getX(), dir.getY(), dir.getZ(), .5);
                }

                world.playSound(location, Sound.ENTITY_BREEZE_IDLE_GROUND, .2f, .3f);

                for(Entity entity : world.getNearbyEntities(location, 6, 6, 6)) {
                    if(entity instanceof LivingEntity && entity != beyonder.getEntity()) {
                        entity.setVelocity(dir.clone().multiply(.3));
                    }
                }

            }
        }.runTaskTimer(plugin, 0, 2);
    }

    private void castElectricShock(Beyonder beyonder) {
        final LivingEntity target = getTargetEntity(beyonder.getEntity(), 12);

        final Location location = target == null ? getTargetBlock(beyonder.getEntity(), 12).getLocation() : target.getEyeLocation();
        final Location start = beyonder.getEntity().getEyeLocation().add(beyonder.getEntity().getEyeLocation().getDirection().multiply(.75));

        ParticleUtil.drawLine(start, location, Particle.ELECTRIC_SPARK, .3, beyonder.getEntity().getWorld(), null, 1, 0.05);

        beyonder.getEntity().getWorld().getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
            if(entity == beyonder.getEntity())
                return;

            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(5 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            }

            entity.setVelocity(beyonder.getEntity().getLocation().getDirection().normalize().multiply(1.2));
        });

        beyonder.getEntity().getWorld().playSound(location, Sound.BLOCK_COPPER_GRATE_HIT, 1, 1);
    }

    private void castBurn(Beyonder beyonder) {
        final Location location = beyonder.getEntity().getEyeLocation().add(beyonder.getEntity().getEyeLocation().getDirection().multiply(1.5));

        beyonder.getEntity().getWorld().spawnParticle(Particle.FLAME, location, 15, 0.1, 0.1, 0.1, 0.075);
        beyonder.getEntity().getWorld().spawnParticle(Particle.SMOKE, location, 6, 0.1, 0.1, 0.1, 0.1);

        beyonder.getEntity().getWorld().getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
            if(entity == beyonder.getEntity())
                return;

            entity.setFireTicks(20 * 5);
            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(9 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            }
        });

        beyonder.getEntity().getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, .7f, .7f);

        if (!location.getBlock().getType().isSolid() && !location.getBlock().getRelative(0, -1, 0).getType().isSolid() && beyonder.isGriefingEnabled() && random.nextBoolean()) {
            location.getBlock().getRelative(0, -1, 0).setType(Material.FIRE);
        }
    }
}
