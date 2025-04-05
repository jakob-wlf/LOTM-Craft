package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class Wandering extends SelectableAbility {

    private Map<String, String> worldLookup;

    public Wandering(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        canBeCopied = false;
        canBeUSedByNonPlayer = false;
    }

    @Override
    protected void initAbilities() {
        worldLookup = new HashMap<>();
        List<String> worlds = Bukkit.getWorlds().stream().map(w -> StringUtils.capitalize(w.getName().replace("_", " "))).toList();
        for(World world : Bukkit.getWorlds()) {
            worldLookup.put(StringUtils.capitalize(world.getName().replace("_", " ")), world.getName());
        }
        abilities = worlds.toArray(new String[0]);
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>();
        for(int i = 0; i < abilities.length; i++) {
            spiritualityCost.put(i, 100);
        }
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        Player player = (Player) beyonder.getEntity();
        String worldName = abilities[ability];
        if(!worldLookup.containsKey(worldName)) {
            player.sendMessage("§cWorld not found!");
            return;
        }

        World world = Bukkit.getWorld(worldLookup.get(worldName));
        if(world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldLookup.get(worldName)));
            if(world == null) {
                player.sendMessage("§cWorld not found!");
                return;
            }
        }

        Location spawnLocation = getLocation(world, player);

        if(world == player.getWorld())
            return;

        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 3, 1);

        player.teleport(spawnLocation);
    }

    @NotNull
    private Location getLocation(World world, Player player) {
        Location spawnLocation = new Location(world, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        if(world.getName().equalsIgnoreCase("world_nether")) {
            spawnLocation = new Location(world, player.getLocation().getX() / 8, player.getLocation().getY(), player.getLocation().getZ() / 8);
        }
        if(player.getWorld().getName().equalsIgnoreCase("world_nether")) {
            spawnLocation = new Location(world, player.getLocation().getX() * 8, player.getLocation().getY(), player.getLocation().getZ() * 8);
        }
        return spawnLocation;
    }
}
