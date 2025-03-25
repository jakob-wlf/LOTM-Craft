package de.jakob.lotm.util.minecraft;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedParticle;
import de.jakob.lotm.LOTM;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ParticleSpawner {

    private static ProtocolManager protocolManager = null;

    public static void initializeProtocolLib() {
        if(LOTM.getInstance().isProtocolLibEnabled())
            protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public static void displayParticlesWithPackets(World world, Particle particle, Location location, int amount, double offsetX, double offsetY, double offsetZ, double speed, Particle.DustOptions dustOptions, int maxDistance) {
        if (!LOTM.getInstance().isProtocolLibEnabled()) {
            displayParticles(world, particle, location, amount, offsetX, offsetY, offsetZ, speed, dustOptions, maxDistance);
            return;
        }

        PacketContainer packet = protocolManager.createPacket(com.comphenix.protocol.PacketType.Play.Server.WORLD_PARTICLES);

        if (particle == Particle.DUST && dustOptions != null) {
            Color color = dustOptions.getColor();
            int red = Math.max(1, color.getRed()); // Ensure red is never 0
            int green = color.getGreen();
            int blue = color.getBlue();
            float size = dustOptions.getSize();

            packet.getNewParticles().write(0, WrappedParticle.create(Particle.DUST, new Particle.DustOptions(Color.fromRGB(red, green, blue), size))); //ParticleOptions

            packet.getDoubles().write(0, location.getX()); // x
            packet.getDoubles().write(1, location.getY()); // y
            packet.getDoubles().write(2, location.getZ()); // z

            packet.getFloat().write(0, (float) offsetX);  // xDist
            packet.getFloat().write(1, (float) offsetY);  // yDist
            packet.getFloat().write(2, (float) offsetZ);  // zDist
            packet.getFloat().write(3, (float) speed);    // speed

            packet.getIntegers().write(0, amount); // count

            packet.getBooleans().write(0, false); // overrideLimiter
            packet.getBooleans().write(1, true);  // alwaysShow
        } else {
            Optional<EnumWrappers.Particle> protocolParticle = getProtocolParticle(particle);

            if (protocolParticle.isEmpty()) {
                System.err.println("Unsupported particle: " + particle.name());
                return;
            }

            packet.getParticles().write(0, protocolParticle.get());

            // Set location
            packet.getFloat().write(0, (float) location.getX());
            packet.getFloat().write(1, (float) location.getY());
            packet.getFloat().write(2, (float) location.getZ());

            // Set offsets and speed
            packet.getFloat().write(3, (float) offsetX);
            packet.getFloat().write(4, (float) offsetY);
            packet.getFloat().write(5, (float) offsetZ);
            packet.getFloat().write(6, (float) speed);

            packet.getIntegers().write(0, amount);

            packet.getBooleans().write(0, false);
            packet.getBooleans().write(1, true);
        }

        for (Player player : world.getPlayers()) {
            if (player.getWorld().equals(world) && player.getLocation().distanceSquared(location) < maxDistance * maxDistance) {
                try {
                    protocolManager.sendServerPacket(player, packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Optional<EnumWrappers.Particle> getProtocolParticle(Particle particle) {
        try {
            return Optional.of(EnumWrappers.Particle.valueOf(particle.name()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }



    public static void displayParticles(World world, Particle particle, Location location, int amount, double offsetX, double offsetY, double offsetZ, double speed, int maxDistance) {
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null, true);
            }
        }
    }

    public static void displayParticles(World world, Particle particle, Location location, int amount, double offsetX, double offsetY, double offsetZ, double speed, Particle.DustOptions dustOptions, int maxDistance) {
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                if(particle == Particle.DUST)
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, dustOptions, true);
                else
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null, true);
            }
        }
    }

    public static void displayParticles(World world, Particle particle, Location location, int amount, double offsetX, double offsetY, double offsetZ, double speed, Color color, int maxDistance) {
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                if(particle == Particle.ENTITY_EFFECT)
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, color, true);
                else
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null, true);
            }
        }
    }

    public static void displayForcedDust(World world, Particle particle, Location location, int amount, double offsetX, double offsetY, double offsetZ, double speed, Particle.DustOptions dust) {
        if(particle == Particle.DUST)
            world.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, dust, true);
        else
            world.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null, true);
    }

    public static void displayParticles(World world, Particle particle, Location location, int amount, double offsetX, double offsetY, double offsetZ, double speed, BlockData blockdata, int maxDistance) {
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                if(particle == Particle.BLOCK || particle == Particle.BLOCK_MARKER)
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, blockdata, true);
                else
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null, true);
            }
        }
    }

    public static void displayParticles(World world, Particle particle, double x, double y, double z, int amount, double offsetX, double offsetY, double offsetZ, double speed, int maxDistance) {
        Location location = new Location(world, x, y, z);
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null,true);
            }
        }
    }

    public static void displayParticles(World world, Particle particle, double x, double y, double z, int amount, double offsetX, double offsetY, double offsetZ, double speed, Particle.DustOptions dustOptions, int maxDistance) {
        Location location = new Location(world, x, y, z);
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                if(particle == Particle.DUST)
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, dustOptions, true);
                else
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, true);
            }
        }
    }

    public static void displayParticles(World world, Particle particle, double x, double y, double z, int amount, double offsetX, double offsetY, double offsetZ, double speed, BlockData blockdata, int maxDistance) {
        Location location = new Location(world, x, y, z);
        for(Player player : world.getPlayers()) {
            if(player.getWorld() == world && player.getLocation().distance(location) < maxDistance) {
                if(particle == Particle.BLOCK || particle == Particle.BLOCK_MARKER)
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, blockdata, true);
                else
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, null, true);
            }
        }
    }

}
