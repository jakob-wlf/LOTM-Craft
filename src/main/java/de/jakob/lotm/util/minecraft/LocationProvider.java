package de.jakob.lotm.util.minecraft;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class LocationProvider {

    private static final HashMap<UUID, Location> locations = new HashMap<>();

    public static Location getLocation(UUID uuid) {
        return locations.getOrDefault(uuid, null);
    }

    public static void setLocation(UUID uuid, Location location) {
        locations.put(uuid, location);
    }

    public static void removeLocation(UUID uuid) {
        locations.remove(uuid);
    }

}
