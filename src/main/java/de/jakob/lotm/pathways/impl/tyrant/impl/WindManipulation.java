package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@NoArgsConstructor
public class WindManipulation extends Ability {
    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Wind Blade", "Flight", "Boost", "Wind Binding"};
    private final HashMap<Integer, Integer> spiritualityCost = new HashMap<>(Map.of(
            0, 10,
            1, 10,
            2, 10,
            3, 10,
            4, 10,
            5, 10
    ));;
    private final ArrayList<Beyonder> isFlying = new ArrayList<>();


    private final Random random = new Random();

    public WindManipulation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder) && beyonder instanceof BeyonderPlayer)
            selectedAbilities.put(beyonder, 0);

        int selectedAbility = beyonder instanceof BeyonderPlayer ? selectedAbilities.get(beyonder) : random.nextInt(abilities.length);

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(beyonder, selectedAbility);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > abilities.length - 1)
            selectedAbilities.replace(beyonder, 0);
    }

    private void castAbility(Beyonder beyonder, int ability) {
        switch(ability) {
            case 0 -> castWindBlade(beyonder);
            case 1 -> castFlight(beyonder);
            case 2 -> castBoost(beyonder);
            case 3 -> castWindBinding(beyonder);
        }
    }

    private void castWindBinding(Beyonder beyonder) {
        World world = beyonder.getEntity().getWorld();

        LivingEntity target = getTargetEntity(beyonder.getEntity(), 20);

        if (target == null) {
            return;
        }

        if(target.getScoreboardTags().contains("wind_bound"))
            return;

        target.addScoreboardTag("wind_bound");

        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 25, 8, false, false));

        for (int i = 0; i < 10; i++) {
            new BukkitRunnable() {
                final double spiralRadius = target.getWidth();

                double spiral = 0;
                double height = 0;
                double spiralX;
                double spiralZ;

                double counter = 20 * 25;

                @Override
                public void run() {
                    Location entityLoc = target.getLocation().clone();
                    entityLoc.add(0, -0.75, 0);

                    if(!target.isValid())
                        counter = 0;

                    counter-=2;
                    if (counter <= 0) {
                        target.removeScoreboardTag("wind_bound");
                        cancel();
                        return;
                    }

                    spiralX = spiralRadius * Math.cos(spiral);
                    spiralZ = spiralRadius * Math.sin(spiral);
                    spiral += 0.25;
                    height += .025;
                    if (height >= 2.3) {
                        height = 0;
                    }

                    ParticleSpawner.displayParticles(world, Particle.EFFECT, spiralX + entityLoc.getX(), height + entityLoc.getY(), spiralZ + entityLoc.getZ(), 5, 0, 0, 0, 0, 100);
                }
            }.runTaskTimer(plugin, i * 15, 2);
        }
    }

    private void castBoost(Beyonder beyonder) {
        if(!beyonder.getEntity().isOnGround())
            return;

        if(isFlying.contains(beyonder)) {
            return;
        }

        beyonder.getEntity().setVelocity(beyonder.getEntity().getLocation().getDirection().normalize().multiply(5));
        ParticleSpawner.displayParticles(beyonder.getEntity().getWorld(), Particle.CLOUD, beyonder.getEntity().getLocation(), 50, .05, .05, .05, .65, 128);
    }

    private void castFlight(Beyonder beyonder) {
        if(!(beyonder instanceof BeyonderPlayer))
            return;

        Player player = (Player) beyonder.getEntity();

        if(isFlying.contains(beyonder)) {
            isFlying.remove(beyonder);
            return;
        }

        if(player.getAllowFlight())
            return;

        player.setAllowFlight(true);
        player.setFlying(true);
        isFlying.add(beyonder);
        LOTM.getInstance().getRemoveAllowFlight().add(beyonder);

        new BukkitRunnable() {
            int counter = 0;
            int recordTimeout = 0;

            @Override
            public void run() {
                if(recordTimeout > 20 * 8 && abilityType == AbilityType.RECORDED)
                    isFlying.remove(beyonder);

                if(counter % 10 == 0 && !beyonder.removeSpirituality(10)) {
                    isFlying.remove(beyonder);
                    counter = 0;
                }

                if(!isFlying.contains(beyonder)) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    LOTM.getInstance().getRemoveAllowFlight().remove(beyonder);
                    cancel();
                    return;
                }

                counter++;
                recordTimeout++;
            }
        }.runTaskTimer(plugin, 0, 1);

        for (int i = 0; i < 12; i++) {
            new BukkitRunnable() {
                double spiralRadius = .1;

                double spiral = 0;
                double height = 0;
                double spiralX;
                double spiralZ;

                @Override
                public void run() {
                    if (!isFlying.contains(beyonder)) {
                        cancel();
                        return;
                    }

                    Location entityLoc = player.getLocation().clone();
                    entityLoc.add(0, -0.75, 0);

                    spiralX = spiralRadius * Math.cos(spiral);
                    spiralZ = spiralRadius * Math.sin(spiral);
                    spiral += 0.25;
                    height += .025;
                    spiralRadius += .015;
                    if (height >= 2.3) {
                        height = 0;
                        spiralRadius = .1;
                    }

                    if (entityLoc.getWorld() == null)
                        return;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getWorld() != entityLoc.getWorld() || player.getLocation().distance(entityLoc) > 100)
                            continue;
                        player.spawnParticle(Particle.EFFECT, spiralX + entityLoc.getX(), height + entityLoc.getY(), spiralZ + entityLoc.getZ(), 2, 0, 0, 0, 0);
                    }
                }
            }.runTaskTimer(plugin, i * 15, 2);
        }
    }

    private void castWindBlade(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        Vector direction = entity.getEyeLocation().getDirection().normalize();
        Location loc = entity.getEyeLocation().add(0, 1.5, 0);
        World world = loc.getWorld();

        if (world == null)
            return;

        loc.add(direction.clone().multiply(2));

        world.playSound(loc, Sound.ENTITY_ARROW_SHOOT, 1, 1);

        new BukkitRunnable() {
            int counter = 40;

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld() != loc.getWorld() || p.getLocation().distance(loc) > 100)
                        continue;
                    drawBlade(loc, p, direction);
                }

                if (loc.getBlock().getType().isSolid() && beyonder.isGriefingEnabled()) {
                    if (loc.getBlock().getType().getHardness() < 0 || loc.getBlock().getType().getHardness() > .7)
                        counter = 0;
                    else
                        loc.getBlock().setType(Material.AIR);
                }

                if(damageNearbyEntities(14, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 2.5, loc, world, false, 0))
                    counter = 0;

                loc.add(direction);

                counter-=2;
                if (counter <= 0)
                    cancel();
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    private void drawBlade(Location loc, Player drawPlayer, Vector direction) {
        Vector dir = direction.clone();
        dir.setY(0);
        dir.normalize().multiply(-.5);

        Random random = new Random();

        for (double d = 0; d < 1.75; d += .15) {
            drawPlayer.spawnParticle(Particle.EFFECT, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.25, d))), 1, 0, 0, 0, 0);
            if (random.nextInt(4) == 0)
                drawPlayer.spawnParticle(Particle.CLOUD, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.5, d))), 1, 0, 0, 0, 0);
        }
        for (double d = 0; d > -1.75; d -= .15) {
            drawPlayer.spawnParticle(Particle.EFFECT, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.25, d * -1))), 1, 0, 0, 0, 0);
            if (random.nextInt(4) == 0)
                drawPlayer.spawnParticle(Particle.CLOUD, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.5, d))), 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);


        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§9" + abilities[selectedAbilities.get(beyonder)]));
    }
}
