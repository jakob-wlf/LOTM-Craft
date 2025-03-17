package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.pathways.DoorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@NoArgsConstructor
public class TravelersDoor extends Ability implements Listener {
    private final HashMap<Player, GameMode> previousGameModes = new HashMap<>();
    private final ArrayList<Player> cooldowns = new ArrayList<>();
    private final HashMap<Player, ItemDisplay> displays = new HashMap<>();
    private final ArrayList<Player> isTyping = new ArrayList<>();

    public TravelersDoor(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        canBeUSedByNonPlayer = false;

        LOTM.getInstance().registerListener(this);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!previousGameModes.containsKey(player)) return;

        Beyonder beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);

        if(beyonder == null) {
            return;
        }

        endTransport(player);
        openDoor(beyonder, 20 * 6, true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!isTyping.contains(player)) return;

        event.setCancelled(true);
        isTyping.remove(player);

        String message = event.getMessage();
        String[] split = message.split(" ");

        if(split.length != 3) {
            player.sendMessage("§cInvalid coordinates.");
            return;
        }

        double x, y, z;

        try {
            x = Double.parseDouble(split[0]);
            y = Double.parseDouble(split[1]);
            z = Double.parseDouble(split[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid coordinates.");
            return;
        }

        Location location = new Location(player.getWorld(), x, y, z);

        Beyonder beyonder = LOTM.getInstance().getBeyonderPlayer(player.getUniqueId(), false);

        if(beyonder == null) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                openDoor(beyonder, 20 * 12, false, location);
            }
        }.runTaskLater(LOTM.getInstance(), 1);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        prepareAbility(beyonder);
    }

    @Override
    public void sneakRightClick(Beyonder beyonder) {
        if(previousGameModes.containsKey((Player) beyonder.getEntity()) || cooldowns.contains((Player) beyonder.getEntity()))
            return;

        Player player = (Player) beyonder.getEntity();
        player.sendMessage("§bType in the coordinates you'd like to go:");
        isTyping.add(player);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        openDoor(beyonder, 20 * 6, false);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        if(previousGameModes.containsKey((Player) beyonder.getEntity()) || cooldowns.contains((Player) beyonder.getEntity()))
            return;

        Player player = (Player) beyonder.getEntity();
        player.sendMessage("§bType in the coordinates you'd like to go:");
        isTyping.add(player);
    }

    @Override
    public void leftClick(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(25))
            return;

        if(previousGameModes.containsKey((Player) beyonder.getEntity()) || cooldowns.contains((Player) beyonder.getEntity()))
            return;

        beyonder.getEntity().getWorld().playSound(beyonder.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .8f);

        transportEntity((Player) beyonder.getEntity());
    }

    private void openDoor(Beyonder beyonder, int duration, boolean behind) {
        openDoor(beyonder, duration, behind, null);
    }

    private void openDoor(Beyonder beyonder, int duration, boolean behind, @Nullable Location destination) {
        if(!beyonder.removeSpirituality(25))
            return;

        Location location = behind ? beyonder.getEntity().getLocation().add(beyonder.getEntity().getLocation().getDirection().multiply(-1.5)) : getTargetBlock(beyonder.getEntity(), 5).getLocation().subtract(0, 1.5, 0);

        while (location.getBlock().getType().isSolid()) {
            location.add(0, .5, 0);
        }

        location.add(0, 1.5, 0);

        World world = location.getWorld();
        if(world == null) return;

        world.playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 1, .1f);
        world.playSound(location, Sound.AMBIENT_CAVE, 1, .1f);

        ItemDisplay display = DoorUtil.createDoor(
                DoorUtil.DoorType.WARPED,
                location,
                1f,
                beyonder.getEntity().getLocation().getDirection().setY(0).normalize(),
                duration,
                true,
                true
        );

        if(display == null) return;

        if(displays.containsKey((Player) beyonder.getEntity())) {
            ItemDisplay previousDisplay = displays.get((Player) beyonder.getEntity());
            if(previousDisplay.isValid()) {
                previousDisplay.remove();
            }
            displays.remove((Player) beyonder.getEntity());
        }

        displays.put((Player) beyonder.getEntity(), display);

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!display.isValid()) {
                    cancel();
                    return;
                }

                final List<Entity> nearbyEntities = display.getNearbyEntities(2, 2, 2);
                for(Entity entity : nearbyEntities) {
                    if (entity instanceof Player player && !cooldowns.contains(player)) {
                        if(destination == null) {
                            if(previousGameModes.containsKey(player)) endTransport(player);
                            else transportEntity(player);
                        }
                        else {
                            player.teleport(destination);
                            cooldowns.add(player);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cooldowns.remove(player);
                                }
                            }.runTaskLater(LOTM.getInstance(), 20 * 3);
                            openDoor(beyonder, 20 * 6, true);
                        }
                    }
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 4);
    }

    private void transportEntity(Player player) {
        cooldowns.add(player);

        previousGameModes.put(player, player.getGameMode());
        LOTM.getInstance().getPreviousGameModes().put(player.getUniqueId(), player.getGameMode());

        player.setGameMode(GameMode.SPECTATOR);

        new BukkitRunnable() {

            final Random random = new Random();

            @Override
            public void run() {
                if(!previousGameModes.containsKey(player)) {
                    cancel();
                    return;
                }


                player.setVelocity(player.getLocation().getDirection().normalize().multiply(1));
                for(int i = 0; i < 3; i++) {
                    Particle.DustOptions dustOptions = ParticleUtil.coloredDustOptionsSize4[random.nextInt(ParticleUtil.coloredDustOptionsSize4.length)];
                    player.spawnParticle(Particle.DUST, player.getEyeLocation().add(player.getLocation().getDirection().multiply(5)), 2, 4, 4, 4, dustOptions);
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);

        new BukkitRunnable() {

            @Override
            public void run() {
                cooldowns.remove(player);
            }
        }.runTaskLater(LOTM.getInstance(), 20 * 3);
    }

    private void endTransport(Player player) {
        if(!previousGameModes.containsKey(player))
            return;

        cooldowns.add(player);

        player.setVelocity(player.getLocation().getDirection().normalize());

        player.setGameMode(previousGameModes.get(player));
        previousGameModes.remove(player);
        LOTM.getInstance().getPreviousGameModes().remove(player.getUniqueId());

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .8f);

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player);
            }
        }.runTaskLater(LOTM.getInstance(), 20 * 3);
    }


}
