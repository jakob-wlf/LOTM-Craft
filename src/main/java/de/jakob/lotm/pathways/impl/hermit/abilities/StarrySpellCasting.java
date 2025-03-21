package de.jakob.lotm.pathways.impl.hermit.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.LocationUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class StarrySpellCasting extends SelectableAbility {

    private final HashMap<Beyonder, List<Location>> conjuredStars = new HashMap<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(176, 112, 255), 1f);
    private final Particle.DustOptions dustBig = new Particle.DustOptions(Color.fromRGB(176, 112, 255), 4f);

    public StarrySpellCasting(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {"Starlight Pillar", "Starlight Cage"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 38,
                1, 25
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        if(!(beyonder instanceof BeyonderPlayer)) {
            sneakLeftClick(beyonder);
        }

        if(!conjuredStars.containsKey(beyonder)) {
            beyonder.getEntity().sendMessage(pathway.getColorPrefix() + "Conjure stars first. (Sneak + Ability Use)");
            return;
        }

        switch (ability) {
            case 0 -> castStarlightPillar(beyonder);
            case 1 -> castStarlightCage(beyonder);
        }
    }

    private void castStarlightCage(Beyonder beyonder) {
        List<Location> stars = conjuredStars.get(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 20);

        World world = targetLoc.getWorld();
        if(world == null)
            return;

        conjuredStars.remove(beyonder);

        List<Location> starLocations = LocationUtil.generateCircle(targetLoc, 2.45, stars.size());

        double speed = targetLoc.distance(entity.getEyeLocation()) / 15f;

        runTaskWithDuration(1, 15, () -> {
            for(int i = 0; i < stars.size(); i++) {
                Location star = stars.get(i);
                Location target = starLocations.get(i);

                ParticleSpawner.displayParticles(world, Particle.DUST, star, 4, 0, 0, 0, 0, dust, 200);

                if(star.distance(target) > .45) {
                    Vector direction = target.clone().toVector().subtract(star.clone().toVector()).normalize().multiply(speed);
                    star.add(direction);
                }
            }
        }, () -> runTaskWithDuration(1, 20 * 18, () -> {
            Bukkit.getScheduler().runTaskLater(plugin, () -> addPotionEffectToNearbyEntities(entity, 2.75, targetLoc, world, new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 8, false, false, false)), 6);
            restrictMovement(entity, targetLoc, 2.75);

            for(Location star : starLocations) {
                ParticleSpawner.displayParticles(world, Particle.DUST, star, 4, 0, 0, 0, 0, dust, 200);
            }
        }, null));
    }

    //TODO: Sounds
    private void castStarlightPillar(Beyonder beyonder) {
        List<Location> stars = conjuredStars.get(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location targetLoc = getTargetLocation(entity, 20);

        World world = targetLoc.getWorld();
        if(world == null)
            return;

        conjuredStars.remove(beyonder);

        List<Location> starLocations = LocationUtil.generateCircle(targetLoc, 2, stars.size());

        double speed = targetLoc.distance(entity.getEyeLocation()) / 15f;

        Bukkit.getScheduler().runTaskLater(plugin, () -> addPotionEffectToNearbyEntities(entity, 2, targetLoc, world, new PotionEffect(PotionEffectType.SLOWNESS, 35, 8, false, false, false)), 6);

        runTaskWithDuration(1, 28, () -> {
            for(int i = 0; i < stars.size(); i++) {
                Location star = stars.get(i);
                Location target = starLocations.get(i);

                ParticleSpawner.displayParticles(world, Particle.DUST, star, 4, 0, 0, 0, 0, dust, 200);

                if(star.distance(target) > .45) {
                    Vector direction = target.clone().toVector().subtract(star.clone().toVector()).normalize().multiply(speed);
                    star.add(direction);
                }

            }
        }, () -> {
            damageNearbyEntities(28, beyonder.getCurrentMultiplier(), entity, 2, targetLoc, world);
            ParticleSpawner.displayParticles(world, Particle.DUST, targetLoc.clone().add(0, 2, 0), 200, .7, 3, .7, 0, dustBig, 200);
            ParticleSpawner.displayParticles(world, Particle.END_ROD, targetLoc.clone().add(0, 2, 0), 500, .7, 3, .7, 0, 200);
        });
    }

    @Override
    public void sneakRightClick(Beyonder beyonder) {
        sneakLeftClick(beyonder);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        if(conjuredStars.containsKey(beyonder))
            return;

        LivingEntity entity = beyonder.getEntity();
        List<Location> starLocations = new ArrayList<>();
        List<Vector> relativeLocations = new ArrayList<>();

        for(int i = 0; i < 13; i++) {
            Location loc = entity.getEyeLocation().add(random.nextDouble(-1.5, 1.5), random.nextDouble(-.8, .8), random.nextDouble(-1.5, 1.5));
            Vector relative = entity.getEyeLocation().clone().toVector().subtract(loc.clone().toVector());
            starLocations.add(loc);
            relativeLocations.add(relative);
        }

        conjuredStars.put(beyonder, starLocations);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!entity.isValid()) {
                    conjuredStars.remove(beyonder);
                    return;
                }

                if(!conjuredStars.containsKey(beyonder)) {
                    cancel();
                    return;
                }

                List<Location> locations = new ArrayList<>();
                for(Vector v : relativeLocations) {
                    Location loc = entity.getEyeLocation().clone().add(v);
                    if(loc.getWorld() == null)
                        continue;
                    ParticleSpawner.displayParticles(loc.getWorld(), Particle.DUST, loc, 10, 0, 0, 0, 0, dust, 200);
                    locations.add(loc);
                }

                conjuredStars.replace(beyonder, locations);
            }
        }.runTaskTimer(plugin, 0, 4);
    }
}
