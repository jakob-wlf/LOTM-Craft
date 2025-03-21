package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@NoArgsConstructor
public class Pyrokinesis extends Ability {
    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Fireball", "Wall of Fire", "Flame Wave", "Giant Fireball", "Fire Ravens", "Blazing Spear"};
    private final HashMap<Integer, Integer> spiritualityCost = new HashMap<>(Map.of(
            0, 15,
            1, 22,
            2, 30,
            3, 32,
            4, 32,
            5, 32
    ));

    private final HashMap<UUID, Particle> particleTypePerPlayer = new HashMap<>();

    private final Random random = new Random();

    public Pyrokinesis(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
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

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(entity, selectedAbility, beyonder.getCurrentMultiplier(), false, beyonder.isGriefingEnabled());
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        if(beyonder.getCurrentSequence() > 6)
            return;

        sneakToggle(beyonder);

        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        LivingEntity entity = beyonder.getEntity();

        if(!particleTypePerPlayer.containsKey(entity.getUniqueId())) {
            particleTypePerPlayer.put(entity.getUniqueId(), Particle.FLAME);
        }

        int selectedAbility = selectedAbilities.get(beyonder);

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(entity, selectedAbility, beyonder.getCurrentMultiplier(), true, beyonder.isGriefingEnabled());
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > 5)
            selectedAbilities.replace(beyonder, 0);
    }

    @Override
    public void sneakToggle(Beyonder beyonder) {
        if(beyonder.getCurrentSequence() > 2)
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

    private void castAbility(LivingEntity entity, int ability, double multiplier, boolean transform, boolean griefing) {
        switch(ability) {
            case 0 -> launchFireball(entity.getEyeLocation().getDirection(), entity, multiplier, transform);
            case 1 -> createFireWall(entity.getEyeLocation().getDirection(), entity, multiplier, griefing);
            case 2 -> createFlameWave(entity, multiplier, griefing);
            case 3 -> launchGiantFireball(entity.getEyeLocation().getDirection(), entity, multiplier, griefing);
            case 4 -> createFireRavens(entity.getEyeLocation().getDirection(), entity, multiplier, griefing);
            case 5 -> createBlazingSpear(entity.getEyeLocation().getDirection(), entity, multiplier, transform, griefing);
        }
    }

    private void createBlazingSpear(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier, boolean transform, boolean griefing) {
        direction.normalize();

        Location location = entity.getEyeLocation().clone().add(direction.clone().multiply(.5)).add(VectorUtil.rotateAroundY(direction, 90).multiply(2.5)).add(0, 1, 0);
        LivingEntity target = getTargetEntity(entity, 100, EntityType.PARROT);
        Location destination = target != null ? target.getEyeLocation() : getTargetBlock(entity, 100).getLocation();

        Vector spearDirection = destination.toVector().subtract(location.toVector()).normalize().multiply(3);

        World world = entity.getWorld();

        final int length = 5;

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        ParticleUtil.createSpear(
                location,
                spearDirection,
                length,
                particleTypePerPlayer.get(entity.getUniqueId())
        );

        if(transform) {
            entity.setInvisible(true);
        }

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                ParticleUtil.createSpear(
                        location,
                        spearDirection,
                        length,
                        particleTypePerPlayer.get(entity.getUniqueId())
                );

                Location currentTipLoc = location.clone().add(spearDirection.clone().normalize().multiply(length));
                if(transform)
                    entity.teleport(currentTipLoc);

                location.add(spearDirection);

                if(damageNearbyEntities(16, damageMultiplier, entity, 2, currentTipLoc, world, true, 50)) {
                    entity.setInvisible(false);
                    cancel();
                    return;
                }

                if(currentTipLoc.getBlock().getType().isSolid()) {
                    entity.setInvisible(false);
                    if(griefing)
                        world.createExplosion(currentTipLoc, 5, true, false, entity);
                    cancel();
                    return;
                }

                if(counter > 20 * 7) {
                    entity.setInvisible(false);
                    cancel();
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 12, 2);
    }

    private void createFireWall(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier, boolean griefing) {
        direction.setY(0).normalize().multiply(3);
        Vector sideDirection = VectorUtil.rotateAroundY(direction, 90);

        World world = entity.getWorld();
        Location startLocation = entity.getLocation();

        if(griefing)
            for(int i = -2; i < 2; i++) {
                for(double j = -2; j < 2; j+=.5) {
                    Location location = startLocation.clone().add(0, i, 0).add(direction.clone().add(sideDirection.clone().multiply(j)));
                    if(!location.getBlock().isPassable() && location.clone().add(0, 1, 1).getBlock().isPassable())
                        location.clone().add(0, 1, 1).getBlock().setType(Material.FIRE);
                }
            }

        world.playSound(startLocation, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.3f, .6f));

        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                counter++;
                for(int i = 0; i < 8; i++) {
                    for(double j = -2; j < 2; j+=.5) {
                        Location location = startLocation.clone().add(direction.clone().add(new Vector(0, i, 0))).add(sideDirection.clone().multiply(j));
                        world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), location, 1, 1, .4, 1, 0);
                        if(random.nextInt(4) == 0)
                            world.spawnParticle(Particle.LARGE_SMOKE, location, 1, 1, .4, 1, 0);

                        for(LivingEntity nearbyEntity : getNearbyLivingEntities(entity, 1, location, world)) {
                            if(location.clone().add(direction).distance(nearbyEntity.getLocation()) > location.clone().add(direction.clone().multiply(-1)).distance(nearbyEntity.getLocation()))
                                nearbyEntity.setVelocity(direction.clone().multiply(-.3));
                            else
                                nearbyEntity.setVelocity(direction.clone().multiply(.3));
                            nearbyEntity.damage(3 * damageMultiplier, entity);
                            nearbyEntity.setFireTicks(20 * 30);
                            world.playSound(location, Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, random.nextFloat(.6f, 1f));
                        }
                    }
                }

                if(counter > 30 * 10) {
                    cancel();
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 2);
    }

    private void launchFireball(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier, boolean transform) {
        direction.normalize().multiply(2);
        Location location = entity.getEyeLocation().clone().add(direction).add(direction);

        SmallFireball fireball = (SmallFireball) entity.getWorld().spawnEntity(location, EntityType.SMALL_FIREBALL);
        fireball.setDirection(direction);
        fireball.setAcceleration(direction);

        World world = fireball.getWorld();

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        if(transform) {
            entity.setInvisible(true);
        }

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                counter++;

                if(!fireball.isValid()) {
                    entity.setInvisible(false);
                    cancel();
                    return;
                }

                Location fireBallLoc = fireball.getLocation();

                if(transform)
                    entity.teleport(new Location(world, fireBallLoc.getX(), fireBallLoc.getY(), fireBallLoc.getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch()));

                world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), fireball.getLocation().add(0, .5, 0), 20, .2, .2, .2, 0, null, true);
                world.spawnParticle(Particle.SMOKE, fireball.getLocation().add(0, .6, 0), 10, .5, .5, .5, 0, null, true);

                if(damageNearbyEntities(12.75, damageMultiplier, entity, 2.5, fireball.getLocation(), world, true, 20 * 30)) {
                    world.playSound(location, Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, random.nextFloat(.1f, .4f));
                    entity.setInvisible(false);
                    fireball.remove();
                    cancel();
                }

                if(counter > 20 * 8) {
                    entity.setInvisible(false);
                    fireball.remove();
                    cancel();
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void createFlameWave(@NotNull LivingEntity entity, double multiplier, boolean griefing) {
        Location startLocation = entity.getLocation();

        World world = entity.getWorld();

        world.playSound(startLocation, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        new BukkitRunnable() {
            double radius = 2;
            @Override
            public void run() {

                double height = 1f / (.5 * (radius - 2));

                Location loc = startLocation.clone();
                loc.setY(loc.getY() + height);

                ParticleUtil.drawParticleCircle(loc, radius, particleTypePerPlayer.get(entity.getUniqueId()), null, 40, .5, 5);
                if(griefing)
                    for(Block block : BlockUtil.getPassableBlocksInCircle(loc, radius, 50)) {
                        if(random.nextInt(8) == 0)
                            block.setType(Material.FIRE);
                    }

                for(LivingEntity target : getNearbyLivingEntities(entity, radius, startLocation, entity.getWorld())) {
                    if(!target.getWorld().equals(entity.getWorld()))
                        continue;
                    double distance = target.getLocation().distance(entity.getLocation());
                    if(Math.sqrt((distance - radius) * (distance - radius)) <= 1.5) {
                        if(target.getNoDamageTicks() <= 0 && EntityUtil.mayDamage(target, entity)[0]) {
                            target.damage(10 * multiplier, entity);
                            target.setNoDamageTicks(25);
                            target.setFireTicks(20 * 10);
                            world.playSound(target.getEyeLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, random.nextFloat(.6f, 1f));
                        }
                    }
                }

                radius += .5;
                if(radius > 15)
                    cancel();
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    private void createFireRavens(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier, boolean griefing) {
        direction.normalize().multiply(2.5);

        Location startLoc = entity.getEyeLocation();
        World world = entity.getWorld();

        world.playSound(startLoc, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.55f, .8f));

        LivingEntity target = getTargetEntity(entity, 100, EntityType.PARROT);
        boolean lockOntoTarget = target != null;
        Location destination = target != null ? null : getTargetBlock(entity, 100).getLocation();

        int count = Math.min(12, (int) Math.round(4 * damageMultiplier));

        Parrot[] parrots = new Parrot[count];

        for(int i = 0; i < count; i++) {
            Location loc = startLoc.clone().add(direction.clone().multiply(3)).add(random.nextDouble(-4.5, 4.5), random.nextDouble(-.5, 5), random.nextDouble(-4.5, 4.5));

            Parrot parrot = (Parrot) world.spawnEntity(loc, EntityType.PARROT);

            Parrot.Variant variant = particleTypePerPlayer.get(entity.getUniqueId()) == Particle.SOUL_FIRE_FLAME ? Parrot.Variant.BLUE : Parrot.Variant.RED;
            parrot.setVariant(variant);
            parrot.getScoreboardTags().add("no_spawn");
            parrot.setInvulnerable(true);

            parrots[i] = parrot;

            boolean isXGreater = parrot.getLocation().getX() > startLoc.getX();
            boolean isZGreater = parrot.getLocation().getZ() > startLoc.getZ();

            double initialXVelocity = isXGreater ? 1 : -1;
            double initialZVelocity = isZGreater ? 1 : -1;

            final Particle.DustOptions dustOptions = particleTypePerPlayer.get(entity.getUniqueId()) == Particle.FLAME ? new Particle.DustOptions(Color.ORANGE, 1f) : new Particle.DustOptions(Color.PURPLE, 1f);

            new BukkitRunnable() {

                int counter = 0;

                @Override
                public void run() {
                    counter++;

                    world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), parrot.getLocation(), 7, .2, .2, .2, 0, null, true);
                    world.spawnParticle(Particle.DUST, parrot.getLocation(), 4, .2, .2, .2, 0, dustOptions, true);

                    if(counter < 10) {
                        parrot.setVelocity(new Vector(initialXVelocity + random.nextDouble(-1, 1), 1 + random.nextDouble(-1, 1), initialZVelocity + random.nextDouble(-1, 1)).normalize());
                    }
                    else {
                        if(lockOntoTarget)
                            parrot.setVelocity(target.getEyeLocation().toVector().subtract(parrot.getLocation().toVector()).add(new Vector(random.nextDouble(-1, 1), random.nextDouble(-1, 1), random.nextDouble(-1, 1))).normalize());
                        else
                            parrot.setVelocity(destination.toVector().subtract(parrot.getLocation().toVector()).add(new Vector(random.nextDouble(-1, 1), random.nextDouble(-1, 1), random.nextDouble(-1, 1))).normalize());
                    }

                    if(damageNearbyEntities(6, damageMultiplier, entity, 1.6, parrot.getLocation(), world, true, 50, 0, parrots)) {
                        parrot.remove();
                        cancel();
                        return;
                    }

                    if(parrot.getLocation().add(parrot.getVelocity().clone().multiply(2)).getBlock().getType().isSolid() || (destination != null && destination.distance(parrot.getLocation()) < 2)) {
                        parrot.remove();
                        if(griefing)
                            world.createExplosion(parrot.getLocation(), .75f, true);
                        cancel();
                        return;
                    }

                    if(!parrot.isValid()) {
                        cancel();
                        return;
                    }

                    if(counter > 600) {
                        parrot.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(LOTM.getInstance(), 0, 0);
        }
    }

    private void launchGiantFireball(@NotNull Vector direction, @NotNull LivingEntity entity, double damageMultiplier, boolean griefing) {
        direction.normalize();
        Location location = entity.getEyeLocation().add(direction.clone().multiply(4));

        World world = entity.getWorld();

        world.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1, random.nextFloat(.8f, 1f));

        new BukkitRunnable() {

            int counter = 25;

            @Override
            public void run() {
                counter--;

                for(int i = 0; i < Math.min(25f / counter, 15); i++) {
                    Location loc = location.clone().add(
                            random.nextDouble(-2, 2),
                            random.nextDouble(-2, 2),
                            random.nextDouble(-2, 2)
                    );

                    Vector v = location.clone().toVector().subtract(loc.toVector()).normalize().multiply(.5);
                    new BukkitRunnable() {
                        int counter = 0;

                        @Override
                        public void run() {
                            counter++;

                            world.spawnParticle(particleTypePerPlayer.get(entity.getUniqueId()), loc, 3, .05, .05, .05, 0);

                            loc.add(v);

                            if(loc.distance(location) < .5 || counter > 50)
                                cancel();
                        }

                    }.runTaskTimer(LOTM.getInstance(), 0, 0);
                }

                if(counter <= 0) {
                    cancel();

                    Vector direction = entity.getEyeLocation().getDirection().normalize();

                    FallingBlock fallingBlock = world.spawnFallingBlock(location, Bukkit.createBlockData(Material.MAGMA_BLOCK));
                    fallingBlock.setGravity(false);

                    world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.1f, .4f));
                    world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, random.nextFloat(.1f, .4f));

                    new BukkitRunnable() {

                        int counter = 0;
                        final Particle.DustOptions dustOptions = particleTypePerPlayer.get(entity.getUniqueId()) == Particle.FLAME ? new Particle.DustOptions(Color.ORANGE, 1.5f) : new Particle.DustOptions(Color.PURPLE, 1.5f);

                        @Override
                        public void run() {
                            counter++;

                            fallingBlock.setVelocity(direction);

                            ParticleUtil.drawParticleSphere(fallingBlock.getLocation(), 1.5, particleTypePerPlayer.get(entity.getUniqueId()), null, 9, .1);
                            ParticleUtil.drawParticleSphere(fallingBlock.getLocation(), 1.5, Particle.SMOKE, null, 3, .1);

                            ParticleUtil.drawParticleSphere(fallingBlock.getLocation(), 1.5, Particle.DUST, dustOptions, 6, .1);

                            if(damageNearbyEntities(21.5, damageMultiplier, entity, 2.5, fallingBlock.getLocation(), world, true, 20 * 30)) {
                                fallingBlock.remove();
                                world.playSound(location, Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, random.nextFloat(.6f, 1f));
                                cancel();
                                return;
                            }

                            if(!fallingBlock.getLocation().clone().add(direction.clone().multiply(2)).getBlock().isPassable()) {
                                if(griefing)
                                    world.createExplosion(fallingBlock.getLocation(), 2f, true);
                                fallingBlock.remove();
                                cancel();
                                return;
                            }

                            if(counter > 100) {
                                fallingBlock.remove();
                                cancel();
                            }
                        }
                    }.runTaskTimer(LOTM.getInstance(), 0, 0);
                }
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