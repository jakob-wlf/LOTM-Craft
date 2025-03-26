package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@NoArgsConstructor
public class FireMastery extends Ability {
    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Flame Front", "Eruption", "Fireball Barrage", "Fire Transformation"};
    private final HashMap<Integer, Integer> spiritualityCost = new HashMap<>(Map.of(
            0, 180,
            1, 280,
            2, 320,
            3, 0
    ));

    private final HashMap<UUID, Particle> particleTypePerPlayer = new HashMap<>();
    private final HashMap<UUID, Boolean> transformedPlayers = new HashMap<>();

    private final Random random = new Random();

    private final Particle.DustOptions whiteDust = new Particle.DustOptions(Color.WHITE, 2.5f);
    private final Particle.DustOptions orangeDust = new Particle.DustOptions(Color.ORANGE, 2.5f);
    private final Particle.DustOptions purpleDust = new Particle.DustOptions(Color.PURPLE, 2.5f);

    public FireMastery(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder) && beyonder instanceof BeyonderPlayer)
            selectedAbilities.put(beyonder, 0);

        LivingEntity entity = beyonder.getEntity();

        if(!particleTypePerPlayer.containsKey(entity.getUniqueId())) {
            particleTypePerPlayer.put(entity.getUniqueId(), Particle.FLAME);
        }

        int selectedAbility = beyonder instanceof BeyonderPlayer ? selectedAbilities.get(beyonder) : random.nextInt(abilities.length);

        if(!(beyonder instanceof BeyonderPlayer) && selectedAbility == 3)
            selectedAbility = 0;

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(entity, selectedAbility, beyonder.getCurrentMultiplier(), beyonder.isGriefingEnabled());
    }
    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 1);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > abilities.length - 1)
            selectedAbilities.replace(beyonder, 0);
    }

    @Override
    public void sneakToggle(Beyonder beyonder) {
        if(beyonder.getCurrentSequence() > 2 || !beyonder.getEntity().isOnGround())
            return;


        LivingEntity entity = beyonder.getEntity();

        if(!particleTypePerPlayer.containsKey(entity.getUniqueId())) {
            particleTypePerPlayer.put(entity.getUniqueId(), Particle.FLAME);
            return;
        }

        if(particleTypePerPlayer.get(entity.getUniqueId()) == Particle.FLAME)
            particleTypePerPlayer.replace(entity.getUniqueId(), Particle.SOUL_FIRE_FLAME);
        else if(particleTypePerPlayer.get(entity.getUniqueId()) == Particle.SOUL_FIRE_FLAME)
            particleTypePerPlayer.replace(entity.getUniqueId(), Particle.FLAME);
    }

    private void castAbility(LivingEntity entity, int ability, double multiplier, boolean griefing) {
        switch(ability) {
            case 0 -> createFlameFront(entity.getEyeLocation().getDirection().normalize(), entity, multiplier, griefing);
            case 1 -> createEruption(entity, multiplier, griefing);
            case 2 -> createFireballBarrage(entity.getEyeLocation().getDirection().normalize(), entity, multiplier);
            case 3 -> fireTransformation(entity, multiplier);
        }
    }

    private void fireTransformation(LivingEntity livingEntity, double multiplier) {
        if(!(livingEntity instanceof Player player))
            return;
        if (transformedPlayers.containsKey(player.getUniqueId())) {
            player.setAllowFlight(transformedPlayers.get(player.getUniqueId()));
            player.setInvisible(false);
            transformedPlayers.remove(player.getUniqueId());
            return;
        }

        World world = player.getWorld();

        transformedPlayers.put(player.getUniqueId(), player.getAllowFlight());

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvisible(true);

        Particle.DustOptions whiteDust = new Particle.DustOptions(Color.WHITE, 1f);

        BeyonderPlayer beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);
        if(beyonder == null)
            return;

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 9 && abilityType == AbilityType.RECORDED) {
                    player.setAllowFlight(transformedPlayers.get(player.getUniqueId()));
                    player.setInvisible(false);
                    transformedPlayers.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                Particle.DustOptions dustOptions = particleTypePerPlayer.get(player.getUniqueId()) == Particle.SOUL_FIRE_FLAME ? new Particle.DustOptions(Color.PURPLE, 1f) : new Particle.DustOptions(Color.ORANGE, 1f);

                if(!transformedPlayers.containsKey(player.getUniqueId())) {
                    cancel();
                    return;
                }

                if(!player.isValid() ||!beyonder.removeSpirituality(1)) {
                    player.setAllowFlight(transformedPlayers.get(player.getUniqueId()));
                    player.setInvisible(false);
                    transformedPlayers.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                player.setFlying(true);

                Location loc = player.getEyeLocation();
                world.spawnParticle(particleTypePerPlayer.get(player.getUniqueId()), loc, 35, 1, 1, 1, 0, null, true);
                world.spawnParticle(Particle.DUST, loc, 15, 1, 1, 1, 0, dustOptions, true);
                world.spawnParticle(Particle.DUST, loc, 15, 1, 1, 1, 0, whiteDust, true);

                world.playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 1, random.nextFloat(.4f, 1));
                world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, .05f, random.nextFloat(.4f, 1));

                damageNearbyEntities(2, multiplier, livingEntity, 1.5, loc, world, true, 50);
                counter++;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void createBlazingSpear(@NotNull Vector spearDirection, Location loc, @NotNull LivingEntity entity, double damageMultiplier, boolean griefing) {
        World world = entity.getWorld();

        final int length = 5;

        world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        int time = random.nextInt(6, 120);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                ParticleUtil.createSpear(
                        loc,
                        spearDirection,
                        length,
                        particleTypePerPlayer.get(entity.getUniqueId())
                );

                if(counter > time)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 0);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                ParticleUtil.createSpear(
                        loc,
                        spearDirection,
                        length,
                        particleTypePerPlayer.get(entity.getUniqueId())
                );

                Location currentTipLoc = loc.clone().add(spearDirection.clone().normalize().multiply(length));

                loc.add(spearDirection);

                if(damageNearbyEntities(20, damageMultiplier, entity, 3.5, currentTipLoc, world, true, 50)) {
                    cancel();
                    return;
                }

                if(currentTipLoc.getBlock().getType().isSolid()) {
                    world.createExplosion(currentTipLoc, 5, griefing, false, entity);
                    cancel();
                    return;
                }

                if(counter > 600) {
                    cancel();
                }
            }
        }.runTaskTimer(LOTM.getInstance(), time, 0);
    }

    private void createFireballBarrage(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier) {
        Location loc = entity.getEyeLocation().add(direction.clone().multiply(3));

        LivingEntity target = getTargetEntity(entity, 100);
        Location destination = target != null ? target.getEyeLocation() : getTargetBlock(entity, 100).getLocation();

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                Location fireBallLoc = loc.clone().add(
                        random.nextDouble(-6, 6),
                        random.nextDouble(-1, 8),
                        random.nextDouble(-6, 6)
                );

                Vector fireBallDirection = destination.clone().subtract(fireBallLoc).toVector().normalize();

                launchFireball(fireBallDirection, fireBallLoc, entity, damageMultiplier, false);

                if(counter > 32)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 3);
    }

    private void launchFireball(@NotNull Vector direction, @NotNull Location location, @NotNull LivingEntity entity, double damageMultiplier, boolean forceParticles) {
        SmallFireball fireball = (SmallFireball) entity.getWorld().spawnEntity(location, EntityType.SMALL_FIREBALL);
        fireball.setDirection(direction);
        fireball.setAcceleration(direction);

        World world = fireball.getWorld();

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));
        Particle.DustOptions dustOptions = particleTypePerPlayer.get(entity.getUniqueId()) == Particle.SOUL_FIRE_FLAME ? new Particle.DustOptions(Color.PURPLE, 1.5f) : new Particle.DustOptions(Color.ORANGE, 1.5f);
        Particle.DustOptions whiteDust = new Particle.DustOptions(Color.WHITE, 1.5f);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                if(!fireball.isValid()) {
                    cancel();
                    return;
                }

                world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), fireball.getLocation().add(0, .5, 0), 20, .25, .25, .25, 0, null, forceParticles);
                world.spawnParticle(Particle.SMOKE, fireball.getLocation().add(0, .6, 0), 10, .3, .3, .3, 0, null, forceParticles);
                world.spawnParticle(Particle.DUST, fireball.getLocation().add(0, .6, 0), 5, .3, .3, .3, 0, dustOptions, forceParticles);
                world.spawnParticle(Particle.DUST, fireball.getLocation().add(0, .6, 0), 5, .3, .3, .3, 0, whiteDust, forceParticles);

                if(damageNearbyEntities(24, damageMultiplier, entity, 2.25, fireball.getLocation(), world, true, 20 * 30)) {
                    world.playSound(location, Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, random.nextFloat(.1f, .4f));
                    fireball.remove();
                    cancel();
                }

                if(counter > 20 * 30)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void createEruption(@NotNull LivingEntity entity, double damageMultiplier, boolean griefing) {
        LivingEntity target = getTargetEntity(entity, 100);
        Location destination = target != null ? target.getEyeLocation() : getTargetBlock(entity, 100).getLocation();

        World world = entity.getWorld();

        world.playSound(destination, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
        world.playSound(destination, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

        Particle.DustOptions dustOptions = particleTypePerPlayer.get(entity.getUniqueId()) == Particle.SOUL_FIRE_FLAME ? purpleDust : orangeDust;

        for(int i = 0; i < 6; i++) {
            world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), destination, 300, 2, 6, 2, .01, null, true);
            world.spawnParticle(Particle.DUST, destination, 50, 2, 6, 2, .6, dustOptions, true);
            world.spawnParticle(Particle.DUST, destination, 50, 2, 6, 2, .6, whiteDust, true);
            world.spawnParticle(Particle.LARGE_SMOKE, destination, 80, 2, 6, 2, .01, null, true);
            world.spawnParticle(Particle.EXPLOSION, destination, 5, 2, 6, 2, 0, null, true);
        }

        damageNearbyEntities(40, damageMultiplier, entity, 4, destination, world, true, 50);

        world.createExplosion(destination, 9, griefing, griefing, entity);
    }

    private void createFlameFront(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier, boolean griefing) {
        direction.setY(0);
        direction.normalize();

        Location loc = entity.getEyeLocation().add(direction).add(direction);

        Vector perpVector = VectorUtil.rotateAroundY(direction, 90);

        World world = entity.getWorld();

        world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.6f, 1f));
        world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.6f, 1f));

        Particle.DustOptions dustOptions = particleTypePerPlayer.get(entity.getUniqueId()) == Particle.SOUL_FIRE_FLAME ? purpleDust : orangeDust;

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), loc, 200, 6, 6, 6, 0);
                world.spawnParticle(Particle.SMOKE, loc, 80, 6, 6, 6, 0);
                world.spawnParticle(Particle.DUST, loc, 25, 6, 6, 6, 0, dustOptions);
                world.spawnParticle(Particle.DUST, loc, 25, 6, 6, 6, 0, whiteDust);

                if(griefing) {
                    for(int i = -3; i < 3; i++) {
                        for(int j = -8; j < 8; j++) {
                            Location fireLoc = loc.clone().add(0, i, 0).add(perpVector.clone().multiply(j));
                            if(!fireLoc.getBlock().getType().isSolid() && fireLoc.clone().subtract(0, 1, 0).getBlock().getType().isSolid() && random.nextInt(5) == 0)
                                fireLoc.getBlock().setType(Material.FIRE);
                        }
                    }
                }

                damageNearbyEntities(15, damageMultiplier, entity, 9, loc, world, true, 50);

                loc.add(direction);

                if(counter > 300)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 0);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        String suffix = beyonder.getCurrentSequence() > 2 ? "" : particleTypePerPlayer.get(player.getUniqueId()) == Particle.SOUL_FIRE_FLAME ? " §7(Purple)" : " §7(Red)";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4" + abilities[selectedAbilities.get(beyonder)] + suffix));
    }
}
