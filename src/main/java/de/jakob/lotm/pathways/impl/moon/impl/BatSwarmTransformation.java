package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;

@NoArgsConstructor
public class BatSwarmTransformation extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();

    public BatSwarmTransformation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeUSedByNonPlayer = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        if(!(beyonder instanceof BeyonderPlayer))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location location = entity.getEyeLocation();

        if(!(entity instanceof Player player))
            return;

        if(player.getAllowFlight())
            return;

        player.setAllowFlight(true);
        player.setInvisible(true);
        LOTM.getInstance().getRemoveInvisible().add(beyonder);
        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);

        HashMap<Bat, Vector> bats = new HashMap<>();
        for(int i = 0; i < 65; i++) {
            Bat bat = (Bat) world.spawnEntity(location.clone().add(random.nextDouble(-4, 4), random.nextDouble(-3.5, 3.5), random.nextDouble(-4, 4)), EntityType.BAT);
            bat.setInvulnerable(true);
            bats.put(bat, location.toVector().subtract(bat.getLocation().toVector()));
        }

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 10 && abilityType == AbilityType.RECORDED)
                    casting.remove(beyonder);

                if(counter % 20 == 0 && !beyonder.removeSpirituality(24))
                    casting.remove(beyonder);

                if(!casting.contains(beyonder)) {
                    bats.forEach((bat, vector) -> bat.remove());
                    player.setAllowFlight(false);
                    player.setInvisible(false);
                    LOTM.getInstance().getRemoveInvisible().remove(beyonder);
                    LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);
                    cancel();
                    return;
                }

                World w = entity.getWorld();
                Location loc = entity.getEyeLocation();

                bats.forEach((bat, vector) -> {
                    bat.teleport(entity.getLocation().clone().add(vector));
                });

                if(counter % 20 == 0) {
                    damageNearbyEntities(13, beyonder.getCurrentMultiplier(), entity, 4, loc, w);
                    addPotionEffectToNearbyEntities(entity, 3, loc, w, new PotionEffect(PotionEffectType.BLINDNESS, 30, 1, false, false, false));
                }

                ParticleSpawner.displayParticles(w, Particle.LARGE_SMOKE, loc, 25, 3, 3, 3, 0, 200);

                counter++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
