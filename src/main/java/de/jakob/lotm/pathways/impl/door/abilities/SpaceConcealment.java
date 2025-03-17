package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.pathways.DoorUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
public class SpaceConcealment extends Ability {

    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Target yourself", "Target Enemy"};
    private final HashMap<Beyonder, Integer> radius = new HashMap<>();
    private final HashMap<Beyonder, List<Location>> centerLocations = new HashMap<>();
    private final HashMap<Beyonder, List<Location>> blockLocations = new HashMap<>();
    private final HashMap<Beyonder, List<ItemDisplay>> doors = new HashMap<>();

    public SpaceConcealment(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!(beyonder instanceof BeyonderPlayer)) {
            selectedAbilities.put(beyonder, random.nextInt(2));
            radius.put(beyonder, random.nextInt(10) + 5);
        }

        if(beyonder.removeSpirituality(20))
            castAbility(beyonder);
    }

    private void castAbility(Beyonder beyonder) {
        LivingEntity target = getTargetEntity(beyonder.getEntity(), 20);
        Location targetLocation = target == null ? getTargetBlock(beyonder.getEntity(), 20).getLocation() : target.getLocation();
        Location loc = selectedAbilities.get(beyonder) == 0 ? beyonder.getEntity().getLocation() : targetLocation;

        int radius = this.radius.get(beyonder);

        World world = beyonder.getEntity().getWorld();

        if(centerLocations.containsKey(beyonder)) {
            List<Location> locs = centerLocations.get(beyonder);
            locs.add(loc);
        }
        else {
            List<Location> locs = new ArrayList<>();
            locs.add(loc);
            centerLocations.put(beyonder, locs);
        }

        Player player = beyonder.getEntity() instanceof Player ? (Player) beyonder.getEntity() : null;

        new BukkitRunnable() {
            List<Location> blocks = BlockUtil.createHollowCube(world, loc, radius, Material.AIR, Material.AIR, Material.BARRIER);

            int counter = 0;

            @Override
            public void run() {
                if(counter == 0) {
                    Location doorLocation = findSuitableDoorLocation(loc, radius, loc.getBlockY(), blocks);
                    if(doorLocation == null) {
                        blocks.forEach(l -> l.getBlock().setType(Material.AIR));
                        cancel();
                        return;
                    }

                    blocks.forEach(l -> {
                        if (!LOTM.getInstance().getBlocksToRemove().contains(l))
                            LOTM.getInstance().getBlocksToRemove().add(l);
                    });

                    Player[] players = beyonder.getEntity() instanceof Player ? new Player[] {(Player) beyonder.getEntity()} : new Player[0];

                    if(beyonder.getEntity() == null) {
                        counter = 0;
                        return;
                    }

                    Vector dir = doorLocation.toVector().subtract(beyonder.getEntity().getEyeLocation().toVector()).setY(0).normalize();

                    ItemDisplay door = DoorUtil.createDoor(DoorUtil.DoorType.CRIMSON, doorLocation.add(0, 1, 0).add(dir.clone().multiply(-.25)), 1.2, dir, 20 * 60 * 2, false, true, true, players);
                    if(!doors.containsKey(beyonder)) {
                        List<ItemDisplay> doorsList = new ArrayList<>();
                        doorsList.add(door);
                        doors.put(beyonder, doorsList);
                    }
                    else {
                        doors.get(beyonder).add(door);
                    }
                }

                if(counter >= 20 * 60 * 2 || !centerLocations.containsKey(beyonder) || !centerLocations.get(beyonder).contains(loc)) {
                    blocks.forEach(l -> l.getBlock().setType(Material.AIR));
                    blocks.forEach(l -> {
                        LOTM.getInstance().getBlocksToRemove().remove(l);
                    });
                    centerLocations.remove(beyonder);

                    if(doors.containsKey(beyonder))
                        doors.get(beyonder).forEach(Entity::remove);

                    cancel();
                    return;
                }

                blocks = BlockUtil.createHollowCube(world, loc, radius, Material.BARRIER, Material.AIR, Material.BARRIER);

                blockLocations.put(beyonder, blocks);

                blocks.forEach(l -> {
                    if (!LOTM.getInstance().getBlocksToRemove().contains(l))
                        LOTM.getInstance().getBlocksToRemove().add(l);
                });

                if(player != null)
                    ParticleUtil.createParticleCube(world, loc, radius, true, player, Particle.WITCH);

                for(ItemDisplay door : doors.get(beyonder)) {
                    for(LivingEntity entity : getNearbyLivingEntities(null, 1.5, door.getLocation(), world)) {
                        Vector dir = entity.getEyeLocation().getDirection().normalize();
                        if(entity.getEyeLocation().add(dir).getBlock().getType() == Material.BARRIER) {
                            entity.teleport(entity.getLocation().add(dir.setY(0).normalize().multiply(2.2)));
                        }
                    }
                }

                counter+=10;
            }
        }.runTaskTimer(plugin, 0, 10);

    }

    private Location findSuitableDoorLocation(Location center, int radius, double yValue, List<Location> blockLocations) {

        List<Location> shuffledLocations = new ArrayList<>(blockLocations);
        Collections.shuffle(shuffledLocations);

        for(Location loc : shuffledLocations) {
            if(loc.getBlockY() == yValue) {
                return loc;
            }
        }

        return null;
    }

    @Override
    public void sneakRightClick(Beyonder beyonder) {
        if(!radius.containsKey(beyonder))
            radius.put(beyonder, 4);

        radius.replace(beyonder, radius.get(beyonder) + 1);

        if(radius.get(beyonder) > 12)
            radius.replace(beyonder, 3);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) >= abilities.length)
            selectedAbilities.replace(beyonder, 0);
    }

    @Override
    public void sneakLeftClick(Beyonder beyonder) {
        if(!blockLocations.containsKey(beyonder))
            return;

        centerLocations.remove(beyonder);
        blockLocations.get(beyonder).forEach(l -> l.getBlock().setType(Material.AIR));
        doors.get(beyonder).forEach(Entity::remove);
        doors.remove(beyonder);

        blockLocations.remove(beyonder);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        if(!radius.containsKey(beyonder))
            radius.put(beyonder, 4);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§b" + abilities[selectedAbilities.get(beyonder)] + " §7- §dRadius: " + radius.get(beyonder)));
    }





}
