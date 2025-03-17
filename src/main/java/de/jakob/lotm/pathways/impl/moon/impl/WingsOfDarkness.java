package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class WingsOfDarkness extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();
    private final Set<Beyonder> cooldown = new HashSet<>();
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(30, 30, 30), 1.4f);

    private final PotionEffect[] effects = new PotionEffect[] {
            new PotionEffect(PotionEffectType.JUMP_BOOST, 20, 1, false, false, false),
            new PotionEffect(PotionEffectType.JUMP_BOOST, 20, 2, false, false, false),
    };

    public WingsOfDarkness(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void leftClick(Beyonder beyonder) {
        castBats(beyonder);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        castBats(beyonder);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            if(!(beyonder instanceof BeyonderPlayer)) {
                castBats(beyonder);
                return;
            }
            casting.remove(beyonder);
            return;
        }

        if(!beyonder.removeSpirituality(10)) {
            return;
        }

        casting.add(beyonder);
        LivingEntity entity = beyonder.getEntity();

        boolean isPlayer = entity instanceof Player;
        Player player = isPlayer ? (Player) entity : null;

        if(isPlayer) {
            if(player.getAllowFlight())
                return;

            player.setAllowFlight(true);
        }

        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);

        double maxFlightLength = 20 * 5 * beyonder.getCurrentMultiplier();

        new BukkitRunnable() {

            int counter = 0;
            int recordTimeout = 0;

            @Override
            public void run() {
                if(recordTimeout > 20 * 9 && abilityType == AbilityType.RECORDED) {
                    casting.remove(beyonder);
                }

                if(!entity.isValid() || !casting.contains(beyonder) || (counter % 2 == 0 && !beyonder.removeSpirituality(1))) {
                    if(isPlayer)
                        player.setAllowFlight(false);
                    LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);
                    casting.remove(beyonder);
                    cancel();
                    return;
                }

                if(entity.getLocation().getBlock().getRelative(0, 1, 0).getType().isSolid())
                    counter = 0;

                if(counter >= maxFlightLength) {
                    if(isPlayer) {
                        player.setAllowFlight(false);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            player.setAllowFlight(true);
                            counter = 0;
                        }, 20 * 3);
                    }
                }

                ParticleUtil.drawShape(entity.getEyeLocation().add(0, .15, 0).add(entity.getLocation().getDirection().normalize().multiply(-.6)), entity.getEyeLocation().getDirection().normalize(), 2.65, Particle.DUST, ParticleUtil.Shape.CLASSIC_WINGS, dustOptions);

                entity.addPotionEffects(List.of(effects));

                counter++;
                recordTimeout++;
            }
        }.runTaskTimer(plugin, 0, 1);

    }

    private void castBats(Beyonder beyonder) {
        if(cooldown.contains(beyonder) || !beyonder.removeSpirituality(10))
            return;

        casting.remove(beyonder);
        cooldown.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Vector direction = entity.getEyeLocation().getDirection().normalize();
        Location location = entity.getEyeLocation().add(direction.clone().multiply(1.5));
        World world = entity.getWorld();

        int count = 50;
        Bat[] bats = new Bat[count];

        for(int i = 0; i < count; i++) {
            Location spawnLoc = location.clone().add(random.nextDouble(-5, 5), random.nextDouble(-1, 4), random.nextDouble(-5, 5));
            Bat bat = (Bat) world.spawnEntity(spawnLoc, EntityType.BAT);

            LOTM.getInstance().getEntitiesToRemove().add(bat);

            bats[i] = bat;

            runTaskWithDuration(0, 20 * 3, () -> {
                if(bat.isDead() || !bat.isValid())
                    return;

                bat.setVelocity(direction);
                bat.setInvulnerable(true);

                ParticleSpawner.displayParticles(bat.getWorld(), Particle.DUST, bat.getLocation(), 5, 0.815, 0.8, 0.8, 0, dustOptions, 150);
                addPotionEffectToNearbyEntities(entity, .5, bat.getLocation(), world, PotionEffectType.BLINDNESS.createEffect(20, 1));
                damageNearbyEntities(1.8f, beyonder.getCurrentMultiplier(), entity, .5, bat.getLocation(), world, false, 0, 0, bats);

            }, () -> {
                if(bat.isDead() || !bat.isValid())
                    return;

                LOTM.getInstance().getEntitiesToRemove().remove(bat);
                bat.remove();
            });
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldown.remove(beyonder), 20 * 4);
    }
}
