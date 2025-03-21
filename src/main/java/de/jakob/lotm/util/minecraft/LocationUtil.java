package de.jakob.lotm.util.minecraft;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {
    public static List<Location> generateCircle(Location center, double radius, int amount) {
        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            double angle = 2 * Math.PI * i / amount; // Evenly distribute points
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(center.getWorld(), x, center.getY(), z);
            locations.add(loc);
        }

        return locations;
    }
}
