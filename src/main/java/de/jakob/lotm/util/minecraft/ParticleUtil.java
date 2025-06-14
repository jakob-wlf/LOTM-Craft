package de.jakob.lotm.util.minecraft;

import de.jakob.lotm.LOTM;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

import static de.jakob.lotm.util.minecraft.ParticleSpawner.displayParticles;

public class ParticleUtil {
    
    public static final Particle.DustOptions[] coloredDustOptions = {
            new Particle.DustOptions(Color.AQUA, 1),
            new Particle.DustOptions(Color.BLACK, 1),
            new Particle.DustOptions(Color.BLUE, 1),
            new Particle.DustOptions(Color.FUCHSIA, 1),
            new Particle.DustOptions(Color.GRAY, 1),
            new Particle.DustOptions(Color.GREEN, 1),
            new Particle.DustOptions(Color.LIME, 1),
            new Particle.DustOptions(Color.MAROON, 1),
            new Particle.DustOptions(Color.NAVY, 1),
            new Particle.DustOptions(Color.OLIVE, 1),
            new Particle.DustOptions(Color.ORANGE, 1),
            new Particle.DustOptions(Color.PURPLE, 1),
            new Particle.DustOptions(Color.RED, 1),
            new Particle.DustOptions(Color.SILVER, 1),
            new Particle.DustOptions(Color.TEAL, 1),
            new Particle.DustOptions(Color.WHITE, 1),
            new Particle.DustOptions(Color.YELLOW, 1)
    };

    public static final Particle.DustOptions[] coloredDustOptionsSize2 = {
            new Particle.DustOptions(Color.AQUA, 2),
            new Particle.DustOptions(Color.BLACK, 2),
            new Particle.DustOptions(Color.BLUE, 2),
            new Particle.DustOptions(Color.FUCHSIA, 2),
            new Particle.DustOptions(Color.GRAY, 2),
            new Particle.DustOptions(Color.GREEN, 2),
            new Particle.DustOptions(Color.LIME, 2),
            new Particle.DustOptions(Color.MAROON, 2),
            new Particle.DustOptions(Color.NAVY, 2),
            new Particle.DustOptions(Color.OLIVE, 2),
            new Particle.DustOptions(Color.ORANGE, 2),
            new Particle.DustOptions(Color.PURPLE, 2),
            new Particle.DustOptions(Color.RED, 2),
            new Particle.DustOptions(Color.SILVER, 2),
            new Particle.DustOptions(Color.TEAL, 2),
            new Particle.DustOptions(Color.WHITE, 2),
            new Particle.DustOptions(Color.YELLOW, 2)
    };
    
    public static final Particle.DustOptions[] coloredDustOptionsSize4 = {
            new Particle.DustOptions(Color.AQUA, 4),
            new Particle.DustOptions(Color.BLACK, 4),
            new Particle.DustOptions(Color.BLUE, 4),
            new Particle.DustOptions(Color.FUCHSIA, 4),
            new Particle.DustOptions(Color.GRAY, 4),
            new Particle.DustOptions(Color.GREEN, 4),
            new Particle.DustOptions(Color.LIME, 4),
            new Particle.DustOptions(Color.MAROON, 4),
            new Particle.DustOptions(Color.NAVY, 4),
            new Particle.DustOptions(Color.OLIVE, 4),
            new Particle.DustOptions(Color.ORANGE, 4),
            new Particle.DustOptions(Color.PURPLE, 4),
            new Particle.DustOptions(Color.RED, 4),
            new Particle.DustOptions(Color.SILVER, 4),
            new Particle.DustOptions(Color.TEAL, 4),
            new Particle.DustOptions(Color.WHITE, 4),
            new Particle.DustOptions(Color.YELLOW, 4)
    };

    private static final boolean X = true;
    private static final boolean O = false;

    public enum Shape {
        CLASSIC_WINGS, HORNS, WOLF_EARS
    }

    private static final boolean[][] CLASSIC_WINGS = {
            {O, O, O, O, X, O, O, O, O, O, O, X, O, O, O, O},
            {O, O, O, X, X, O, O, O, O, O, O, X, X, O, O, O},
            {O, O, X, X, O, O, O, O, O, O, O, O, X, X, O, O},
            {O, X, X, X, O, O, O, O, O, O, O, O, X, X, X, O},
            {O, X, X, X, X, O, O, O, O, O, O, X, X, X, X, O},
            {O, O, X, X, X, X, O, O, O, O, X, X, X, X, O, O},
            {O, O, X, X, X, X, X, O, O, X, X, X, X, X, O, O},
            {O, O, O, X, X, X, X, X, X, X, X, X, X, O, O, O},
            {O, O, O, O, X, X, X, X, X, X, X, X, O, O, O, O},
    };
    private static final boolean[][] HORNS = {
            {O, O, O, X, X, O, O, O, O, O, O, X, X, O, O, O},
            {O, O, X, X, X, O, O, O, O, O, O, X, X, X, O, O},
            {O, O, X, X, O, O, O, O, O, O, O, O, X, X, O, O},
            {O, O, X, X, O, O, O, O, O, O, O, O, X, X, O, O},
            {O, O, X, X, O, O, O, O, O, O, O, O, X, X, O, O},
            {O, O, O, X, X, O, O, O, O, O, O, X, X, O, O, O},
            {O, O, O, X, X, X, O, O, O, O, X, X, X, O, O, O},
    };

    private static final boolean[][] WOLF_EARS = {
            {O, O, O, X, O, O, O, O, O, O, O, O, O, O, X, O, O, O},
            {O, O, X, X, X, O, O, O, O, O, O, O, O, X, X, X, O, O},
            {O, O, X, X, X, O, O, O, O, O, O, O, O, X, X, X, O, O},
            {O, X, X, X, X, X, O, O, O, O, O, O, X, X, X, X, X, O},
            {O, X, X, X, X, X, X, O, O, O, O, X, X, X, X, X, X, O},
            {O, X, X, X, X, X, X, X, O, O, X, X, X, X, X, X, X, O},
            {O, X, X, X, X, X, X, X, O, O, X, X, X, X, X, X, X, O},
    };


    private static final boolean[][] TREE = {
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, X, X, X, X, X, X, X, X, O, O, O, O},
            {O, O, O, X, X, X, X, X, X, X, X, X, X, O, O, O},
            {O, O, O, X, X, X, X, X, X, X, X, X, X, O, O, O},
            {O, O, O, X, X, X, X, X, X, X, X, X, X, O, O, O},
            {O, O, O, X, X, X, X, X, X, X, X, X, X, O, O, O},
            {O, O, O, O, X, X, X, X, X, X, X, X, O, O, O, O},
            {O, O, O, O, O, X, X, X, X, X, X, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, O, X, X, X, X, O, O, O, O, O, O},
            {O, O, O, O, O, X, X, X, X, X, X, O, O, O, O, O},
            {O, O, O, X, X, X, X, X, X, X, X, X, X, O, O, O},
    };


    public static void createParticleSphere(Location center, double radius, int detail, Particle particle) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < detail; i++) {
            double theta = Math.acos(1 - 2 * Math.random()); // Uniform distribution over the sphere
            double phi = 2 * Math.PI * Math.random();

            double x = radius * Math.sin(theta) * Math.cos(phi);
            double y = radius * Math.sin(theta) * Math.sin(phi);
            double z = radius * Math.cos(theta);

            Location particleLocation = center.clone().add(x, y, z);
            displayParticles(world, particle, particleLocation, 1, 0, 0, 0, 0, 250);
        }
    }

    public static void createParticleSphere(Location center, double radius, int detail, Particle particle, Particle.DustOptions dustOptions) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < detail; i++) {
            double theta = Math.acos(1 - 2 * Math.random());
            double phi = 2 * Math.PI * Math.random();

            double x = radius * Math.sin(theta) * Math.cos(phi);
            double y = radius * Math.sin(theta) * Math.sin(phi);
            double z = radius * Math.cos(theta);

            Location particleLocation = center.clone().add(x, y, z);
            displayParticles(world, particle, particleLocation, 1, 0, 0, 0, 0, dustOptions, 450);
        }
    }

    public static void drawShape(Location center, Vector directionFacing, double size, Particle particle, Shape shape, Particle.DustOptions dustOptions) {
        World world = center.getWorld();
        if (world == null) return;

        boolean[][] selectedShape = switch (shape) {
            case HORNS -> HORNS;
            case WOLF_EARS -> WOLF_EARS;
            default -> CLASSIC_WINGS;
        };

        Vector right = new Vector(-directionFacing.getZ(), 0, directionFacing.getX()).normalize();
        Vector up = new Vector(0, 1, 0);

        int width = selectedShape[0].length;
        int height = selectedShape.length;

        double scale = size * 0.1;
        Vector startOffset = right.clone().multiply(-width * scale * 0.5).add(up.clone().multiply(height * scale * 0.5));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (selectedShape[y][x]) {
                    Vector offset = right.clone().multiply(x * scale).subtract(up.clone().multiply(y * scale));
                    if(particle == Particle.DUST && dustOptions != null) {
                        displayParticles(world, particle, center.clone().add(startOffset).add(offset), 1, 0, 0, 0, 0, dustOptions, 200);
                    } else {
                        displayParticles(world, particle, center.clone().add(startOffset).add(offset), 1, 0, 0, 0, 0, 200);
                    }
                }
            }
        }
    }

    public static void drawTree(Location center, double size, Particle trunkParticle, Particle leavesParticle, Particle.DustOptions dustOptionsTrunk, Particle.DustOptions dustOptionsLeaves) {
        World world = center.getWorld();
        if (world == null) return;



        drawTreeSide(world, center.clone().add(0, 3.8, 0), new Vector(1, 0, 0), size, trunkParticle, leavesParticle, dustOptionsTrunk, dustOptionsLeaves);
        drawTreeSide(world, center.clone().add(0, 3.8, 0), new Vector(0, 0, 1), size, trunkParticle, leavesParticle, dustOptionsTrunk, dustOptionsLeaves);
    }

    private static void drawTreeSide(World world, Location center, Vector directionFacing, double size, Particle trunkParticle, Particle leavesParticle, Particle.DustOptions dustOptionsTrunk, Particle.DustOptions dustOptionsLeaves) {
        Vector right = new Vector(-directionFacing.getZ(), 0, directionFacing.getX()).normalize();
        Vector up = new Vector(0, 1, 0);

        int width = TREE[0].length;
        int height = TREE.length;

        double scale = size * 0.1;
        Vector startOffset = right.clone().multiply(-width * scale * 0.5).add(up.clone().multiply(height * scale * 0.5));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (TREE[y][x]) {
                    Vector offset = right.clone().multiply(x * scale).subtract(up.clone().multiply(y * scale));

                    Particle particle = y <= 7 ? leavesParticle : trunkParticle;
                    Particle.DustOptions dustOptions = y <= 7 ? dustOptionsLeaves : dustOptionsTrunk;

                    if(particle == Particle.DUST && dustOptions != null) {
                        displayParticles(world, particle, center.clone().add(startOffset).add(offset), 1, 0, 0, 0, 0, dustOptions, 200);
                    } else {
                        displayParticles(world, particle, center.clone().add(startOffset).add(offset), 1, 0, 0, 0, 0, 200);
                    }
                }
            }
        }
    }
    
    public static void drawParticleSphere(Location center, double radius, Particle particle, Particle.DustOptions dustOptions, int detail, double particleOffset) {
        World world = center.getWorld();
        if (world == null) return;

        // Iterate over sphere coordinates with steps controlled by the 'detail' parameter
        for (int phiStep = 0; phiStep <= detail; phiStep++) {
            double phi = Math.PI * phiStep / detail;
            for (int thetaStep = 0; thetaStep <= detail; thetaStep++) {
                double theta = 2 * Math.PI * thetaStep / detail;

                // Convert spherical coordinates to Cartesian coordinates
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(phi) * Math.sin(theta);

                // Create a vector and add it to the center location
                Vector offset = new Vector(x, y, z);
                Location particleLocation = center.clone().add(offset);

                // Spawn the particle at the calculated location
                if (particle == Particle.DUST && dustOptions != null) {
                    world.spawnParticle(particle, particleLocation, 1, particleOffset, particleOffset, particleOffset, 0, dustOptions, true);
                } else {
                    world.spawnParticle(particle, particleLocation, 1, particleOffset, particleOffset, particleOffset, 0, null, true);
                }
            }
        }
    }

    public static void createSpike(Location origin, double height, double baseWidth, Particle particle, Particle.DustOptions dustOptions, double offsetX, double offsetY, double offsetZ, double stepSize) {
        World world = origin.getWorld();
        if (world == null) {
            return;
        }

        for (double y = 0; y < height; y+= stepSize) {
            double layerWidth = (baseWidth / height) * (height - y);

            for (double x = -layerWidth / 2; x <= layerWidth / 2; x += stepSize) {
                for (double z = -layerWidth / 2; z <= layerWidth / 2; z += stepSize) {
                    Location particleLocation = origin.clone().add(x, y, z);

                    world.spawnParticle(particle, particleLocation, 1, offsetX, offsetY, offsetZ, 0, dustOptions);
                }
            }
        }
    }

    public static void createParticleSword(Location origin, Vector direction, double length, Particle particle, double stepSize, Particle.DustOptions dustOptions) {
        World world = origin.getWorld();
        if (world == null) {
            return;
        }

        if (stepSize <= 0) {
            return;
        }

        direction.normalize();

        Vector perpendicular = getPerpendicular(direction).normalize();


        drawLine(origin, origin.clone().add(direction.clone().multiply(length)), particle, stepSize, world, dustOptions, 6, .3);
        drawLine(
                origin.clone().add(perpendicular.clone().multiply(.15)),
                origin.clone().add(direction.clone().multiply(length - length / 12)).add(perpendicular.clone().multiply(.15)),
                particle, stepSize, world, dustOptions, 6, .3
        );
        drawLine(origin.clone().add(perpendicular.clone().multiply(-.15)),
                origin.clone().add(direction.clone().multiply(length - length / 12)).add(perpendicular.clone().multiply(-.15)),
                particle, stepSize, world, dustOptions, 6, .3
        );

        Location handleLocation = origin.clone().add(direction.clone().multiply(length / 4.5));

        drawLine(
                handleLocation.clone().add(perpendicular.clone().multiply(length / 4.5)),
                handleLocation.clone().add(perpendicular.clone().multiply(-length / 4.5)),
                particle, stepSize, world, dustOptions, 8, .3
        );
    }

    public static void drawParticleCircle(@NotNull Location center, double radius, Particle particle, Particle.DustOptions dustOptions, int detail, double particleOffset, int count) {
        World world = center.getWorld();
        if (world == null) return;

        double increment = (2 * Math.PI) / detail; // Higher detail -> smoother circle

        for (int i = 0; i < detail; i++) {
            double angle = i * increment;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location particleLocation = center.clone().add(x, 0, z);

            // Check if the particle is DUST and apply DustOptions, otherwise just spawn the particle normally
            if (particle == Particle.DUST) {
                world.spawnParticle(particle, particleLocation, count, particleOffset, particleOffset, particleOffset, 0, dustOptions);
            } else {
                world.spawnParticle(particle, particleLocation, count, particleOffset, particleOffset, particleOffset, 0);
            }
        }
    }

    public static void createSpear(Location origin, Vector direction, double length, Particle particle) {
        createSpear(origin, direction, length, particle, null);
    }

    public static void displayOvalOutline(World world, Particle particle, Location center,
                                          double width, double length, double height,
                                          Vector lengthDirection, Particle.DustOptions dustOptions,
                                          double detailFactor, double offset) {
        // Normalize the given lengthDirection vector to define the length axis.
        Vector lengthAxis = lengthDirection.clone().normalize();

        // Choose a reference vector that is not parallel to the length axis.
        Vector reference = new Vector(0, 1, 0);
        if (Math.abs(lengthAxis.dot(reference)) > 0.99) {
            reference = new Vector(1, 0, 0);
        }

        // Compute two perpendicular axes to form an orthonormal basis:
        // widthAxis will represent the oval's width direction,
        // heightAxis will represent the oval's height direction.
        Vector widthAxis = lengthAxis.clone().crossProduct(reference).normalize();
        Vector heightAxis = widthAxis.clone().crossProduct(lengthAxis).normalize();

        // Determine the step size for iterating over the ellipsoid's surface.
        double baseStep = 0.5;
        double step = baseStep / detailFactor;

        // Tolerance for checking if a point lies on the surface of the ellipsoid.
        // You can adjust this tolerance based on the desired outline thickness.

        // Loop over a 3D grid spanning the bounds of the ellipsoid.
        for (double w = -width / 2; w <= width / 2; w += step) {
            for (double h = -height / 2; h <= height / 2; h += step) {
                for (double l = -length / 2; l <= length / 2; l += step) {
                    double normW = w / (width / 2);
                    double normH = h / (height / 2);
                    double normL = l / (length / 2);
                    double equationValue = normW * normW + normH * normH + normL * normL;

                    // Only draw a particle if the point is close to the ellipsoid surface.
                    if (Math.abs(equationValue - 1) < step) {
                        Location particleLocation = center.clone();
                        particleLocation.add(widthAxis.clone().multiply(w));
                        particleLocation.add(heightAxis.clone().multiply(h));
                        particleLocation.add(lengthAxis.clone().multiply(l));

                        // Spawn the particle with the given options.
                        if (particle == Particle.DUST && dustOptions != null) {
                            ParticleSpawner.displayParticles(world, particle, particleLocation, 1,
                                    offset, offset, offset, 0, dustOptions, 200);
                        } else {
                            ParticleSpawner.displayParticles(world, particle, particleLocation, 1,
                                    offset, offset, offset, 0, 200);
                        }
                    }
                }
            }
        }
    }

    public static void displayFilledOval(World world, Particle particle, Location center,
                                         double width, double length, double height,
                                         Vector lengthDirection, Particle.DustOptions dustOptions, double detailFactor, double offset) {
        // Normalize the given lengthDirection vector to define the length axis.
        Vector lengthAxis = lengthDirection.clone().normalize();

        // Choose a reference vector that is not parallel to the length axis.
        Vector reference = new Vector(0, 1, 0);
        if (Math.abs(lengthAxis.dot(reference)) > 0.99) {
            reference = new Vector(1, 0, 0);
        }

        // Compute two perpendicular axes to form an orthonormal basis:
        // widthAxis will represent the oval's width direction,
        // heightAxis will represent the oval's height direction.
        Vector widthAxis = lengthAxis.clone().crossProduct(reference).normalize();
        Vector heightAxis = widthAxis.clone().crossProduct(lengthAxis).normalize();

        // Determine the step size for iterating over the ellipsoid volume.
        // A higher detailFactor means a smaller step and more particles.
        double baseStep = 0.5;
        double step = baseStep / detailFactor;

        // Loop over a 3D grid spanning the bounds of the ellipsoid.
        // The ellipsoid is centered at 'center', and the equation to test if a point (w,h,l) is inside is:
        // (w/(width/2))^2 + (h/(height/2))^2 + (l/(length/2))^2 <= 1.
        for (double w = -width / 2; w <= width / 2; w += step) {
            for (double h = -height / 2; h <= height / 2; h += step) {
                for (double l = -length / 2; l <= length / 2; l += step) {
                    double normW = w / (width / 2);
                    double normH = h / (height / 2);
                    double normL = l / (length / 2);
                    if (normW * normW + normH * normH + normL * normL <= 1) {
                        // Compute the particle location by offsetting from the center.
                        Location particleLocation = center.clone();
                        particleLocation.add(widthAxis.clone().multiply(w));
                        particleLocation.add(heightAxis.clone().multiply(h));
                        particleLocation.add(lengthAxis.clone().multiply(l));

                        // Delegate particle spawning to ParticleSpawner's methods.
                        if (particle == Particle.DUST && dustOptions != null) {
                            ParticleSpawner.displayParticles(world, particle, particleLocation, 1, offset, offset, offset, 0, dustOptions, 200);
                        } else {
                            ParticleSpawner.displayParticles(world, particle, particleLocation, 1, offset, offset, offset, 0, 200);
                        }
                    }
                }
            }
        }
    }


    public static void displayParticleRectangle(World world, Particle particle, Location center, double width, double height, Vector facing, Particle.DustOptions dustOptions, double detailFactor) {
        // Normalize the facing vector to use as the plane's normal.
        Vector normal = facing.clone().normalize();

        // Choose a reference vector not parallel to the normal.
        Vector reference = new Vector(0, 1, 0);
        if (Math.abs(normal.dot(reference)) > 0.99) {
            reference = new Vector(1, 0, 0);
        }

        // Compute two perpendicular vectors to span the plane of the rectangle.
        Vector right = normal.clone().crossProduct(reference).normalize();
        Vector up = right.clone().crossProduct(normal).normalize();

        // Determine step size using the detail factor.
        // A higher detailFactor results in a smaller step size, and hence a denser grid.
        double baseStep = 0.5;
        double step = baseStep / detailFactor;

        // Iterate over the grid covering the rectangle.
        for (double x = -width / 2; x <= width / 2; x += step) {
            for (double y = -height / 2; y <= height / 2; y += step) {
                // Compute the location for the current particle.
                Location particleLocation = center.clone();
                particleLocation.add(right.clone().multiply(x));
                particleLocation.add(up.clone().multiply(y));

                // Delegate particle display to the ParticleSpawner class.
                if (particle == Particle.DUST && dustOptions != null) {
                    ParticleSpawner.displayParticles(world, particle, particleLocation, 1, 0, 0, 0, 0, dustOptions, 200);
                } else {
                    ParticleSpawner.displayParticles(world, particle, particleLocation, 1, 0, 0, 0, 0, 200);
                }
            }
        }
    }



    public static void createSpear(Location origin, Vector direction, double length, Particle particle, Particle.DustOptions dustOptions) {
        World world = origin.getWorld();
        if (world == null) return;

        Vector dir = direction.clone().normalize();

        // Calculate perpendicular vectors for circle and spearhead
        Vector perp1 = getPerpendicular(dir).normalize().multiply(-1);
        Vector perp2 = dir.clone().crossProduct(perp1).normalize();

        // Define lengths for spearhead, circle, and shaft
        double spearheadLength = length * 0.2;
        double circleLength = length * 0.1;
        double shaftLength = length - spearheadLength - circleLength;

        // Define spearhead parameters
        Location tip = origin.clone().add(dir.clone().multiply(length));
        double spearheadRadius = spearheadLength * 0.35;

        // Draw triangular spearhead (3 lines)
        for (int i = 0; i < 3; i++) {
            double angle = Math.toRadians(i * -120);
            Vector offset = perp1.clone().multiply(Math.cos(angle) * spearheadRadius)
                    .add(perp2.clone().multiply(Math.sin(angle) * spearheadRadius));
            Location point = tip.clone().add(dir.clone().multiply(-spearheadLength)).add(offset);
            drawLine(tip, point, particle, 0.1, world, dustOptions, 1, 0);
        }

        // Draw circular section behind spearhead
        Location circleCenter = tip.clone().add(dir.clone().multiply(-spearheadLength - (circleLength / 2)));
        double circleRadius = circleLength * 0.2;
        drawCircle(circleCenter, dir, circleRadius, particle, 20, world, dustOptions);

        // Draw shaft
        Location shaftStart = origin.clone();
        Location shaftEnd = origin.clone().add(dir.clone().multiply(shaftLength));
        drawLine(shaftStart, shaftEnd, particle, 0.2, world, dustOptions, 2, 0);
    }

    public static void drawLine(Location start, Location end, Particle particle, double step, World world, @Nullable Particle.DustOptions dustOptions, int count, double particleOffset) {
        Vector direction = end.clone().subtract(start).toVector();
        double distance = direction.length();
        Vector stepVector = direction.normalize().multiply(step);

        Location current = start.clone();
        for (double traveled = 0; traveled <= distance; traveled += step) {
            world.spawnParticle(particle, current, count, particleOffset, particleOffset, particleOffset, 0, dustOptions);
            current.add(stepVector);
        }
    }

    public static void drawLine(Location start, Vector direction, Particle particle, double step, World world, @Nullable Particle.DustOptions dustOptions, int count, double particleOffset, double length) {
        Vector stepVector = direction.normalize().multiply(step);

        Location current = start.clone();
        for (double traveled = 0; traveled <= length; traveled += step) {
            world.spawnParticle(particle, current, count, particleOffset, particleOffset, particleOffset, 0, dustOptions);
            current.add(stepVector);
        }
    }

    public static void drawLine(Location start, Location end, Particle particle, double step, World world, @Nullable Particle.DustOptions dustOptions, int count, double particleOffset, double length) {
        Vector direction = end.clone().subtract(start).toVector();
        Vector stepVector = direction.normalize().multiply(step);

        Location current = start.clone();
        for (double traveled = 0; traveled <= length; traveled += step) {
            world.spawnParticle(particle, current, count, particleOffset, particleOffset, particleOffset, 0, dustOptions);
            current.add(stepVector);
        }
    }

    private static void drawCircle(Location center, Vector normal, double radius, Particle particle, int points, World world, Particle.DustOptions dust) {
        Vector perp1 = getPerpendicular(normal).normalize().multiply(radius);
        Vector perp2 = normal.clone().crossProduct(perp1).normalize().multiply(radius);

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            Vector point = perp1.clone().multiply(x).add(perp2.clone().multiply(y));
            Location particleLocation = center.clone().add(point);
            if(particle != Particle.DUST)
                world.spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0, null, true);
            else
                world.spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0, dust, true);
        }
    }

    public static void drawCircle(Location center, Vector direction, double radius, Particle particleType, Particle.DustOptions dustOptions, int detail, double randomness) {
        if (center == null || direction == null || particleType == null) {
            throw new IllegalArgumentException("Center, direction, and particle type cannot be null.");
        }

        if (particleType == Particle.DUST && dustOptions == null) {
            throw new IllegalArgumentException("DustOptions must be provided for DUST particle type.");
        }

        // Normalize the direction vector
        direction = direction.clone().normalize();

        // Find two perpendicular vectors to the given direction
        Vector ortho1 = new Vector(-direction.getY(), direction.getX(), 0).normalize();
        if (ortho1.length() == 0) {
            ortho1 = new Vector(0, -direction.getZ(), direction.getY()).normalize();
        }

        Vector ortho2 = direction.clone().crossProduct(ortho1).normalize();

        // Calculate the step size based on detail
        double step = 2 * Math.PI / detail;

        for (int i = 0; i < detail; i++) {
            double angle = i * step;
            // Calculate point on the circle using ortho1 and ortho2
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;

            Vector point = ortho1.clone().multiply(x).add(ortho2.clone().multiply(y));
            Location particleLocation = center.clone().add(point);

            if(center.getWorld() == null)
                return;

            ParticleSpawner.displayParticles(center.getWorld(), particleType, particleLocation, 1, randomness, randomness, randomness, 0, dustOptions, 250);
        }
    }

    public static void drawCircle(Location center, Vector direction, double radius, Particle particleType, Particle.DustOptions dustOptions, int detail) {
        drawCircle(center, direction, radius, particleType, dustOptions, detail, 0);
    }

    private static Vector getPerpendicular(Vector vector) {
        if (Math.abs(vector.getY()) > 0.9) {
            return new Vector(1, 0, 0);
        } else {
            return vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        }
    }

    public static void createArc(Location location, Vector direction, Particle particleType, Particle.DustOptions dustOptions, double radius, int points) {
        if (location == null || direction == null || particleType == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location, direction, and particleType cannot be null");
        }

        // Normalize the direction vector to ensure consistent scaling
        direction = direction.normalize();

        // Calculate orthogonal vectors for the arc plane
        Vector orthogonal1 = getOrthogonalVector(direction);

        for (int i = 0; i <= points; i++) {
            // Calculate the angle for the current point in radians
            double angle = (Math.PI / 2) * ((double) i / points); // Quarter circle

            // Calculate the position on the arc
            Vector arcPoint = direction.clone().multiply(radius * Math.cos(angle))
                    .add(orthogonal1.clone().multiply(radius * Math.sin(angle)));

            // Transform arcPoint into world coordinates
            Location particleLocation = location.clone().add(arcPoint);

            // Spawn the particle
            if (particleType == Particle.DUST && dustOptions != null) {
                location.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, dustOptions);
            } else {
                location.getWorld().spawnParticle(particleType, particleLocation, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Spawns a rotating, cone-shaped particle tornado at a given location.
     *
     * @param particle      The Particle type to display (e.g., Particle.FLAME, Particle.REDSTONE).
     * @param dustOptions   DustOptions (only if particle == Particle.REDSTONE; can be null otherwise).
     * @param height        How tall the tornado is (in blocks).
     * @param bottomRadius  The radius at the bottom (base) of the tornado.
     * @param topRadius     The radius at the top of the tornado.
     * @param duration      How long the tornado lasts (in ticks). (20 ticks = 1 second by default)
     * @param detail        Controls how many particle points get spawned each tick (higher = denser).
     * @param pSpeed        The particle speed (the "extra" parameter in spawnParticle).
     */
    public static BukkitTask spawnParticleTornado(Particle particle,
                                            Particle.DustOptions dustOptions,
                                            double height,
                                            double bottomRadius,
                                            double topRadius,
                                            long duration,
                                            int detail,
                                            double pSpeed,
                                            UUID locationUUID,
                                            int delay) {

        return spawnParticleTornado(particle, dustOptions, height, bottomRadius, topRadius, duration, detail, pSpeed, 1.0, locationUUID, delay);
    }

    /**
     * Spawns a rotating, cone-shaped particle tornado at a given location.
     *
     * @param particle      The Particle type to display (e.g., Particle.FLAME, Particle.REDSTONE).
     * @param dustOptions   DustOptions (only if particle == Particle.REDSTONE; can be null otherwise).
     * @param height        How tall the tornado is (in blocks).
     * @param bottomRadius  The radius at the bottom (base) of the tornado.
     * @param topRadius     The radius at the top of the tornado.
     * @param duration      How long the tornado lasts (in ticks). (20 ticks = 1 second by default)
     * @param detail        Controls how many particle points get spawned each tick (higher = denser).
     * @param pSpeed        The particle speed (the "extra" parameter in spawnParticle).
     */
    public static BukkitTask spawnParticleTornado(Particle particle,
                                                  Particle.DustOptions dustOptions,
                                                  double height,
                                                  double bottomRadius,
                                                  double topRadius,
                                                  long duration,
                                                  int detail,
                                                  double pSpeed,
                                                  double speed,
                                                  UUID locationUUID,
                                                  int delay) {

        // How many full revolutions the tornado makes in one second
        final double revolutionsPerSecond = speed;

        // The total number of ticks we want the effect to run
        final long totalTicks = duration;

        // Schedule a repeating task that spawns particles every tick

        return new BukkitRunnable() {
            double currentTick = 0.0;

            Location location = LocationProvider.getLocation(locationUUID);

            @Override
            public void run() {
                location = LocationProvider.getLocation(locationUUID);

                // Cancel if we've reached the total duration
                if (currentTick >= totalTicks || location == null) {
                    cancel();
                    return;
                }

                // Each tick, we calculate how far we've rotated
                // (revolutionsPerSecond * 2π per second) => /20 for per tick
                double angleIncrement = (2.0 * Math.PI * revolutionsPerSecond) / 20.0;
                double baseAngle = currentTick * angleIncrement;

                // We'll spawn 'detail' points each tick to control density
                for (int i = 0; i < detail; i++) {
                    // fraction goes from 0 (bottom) to 1 (top)
                    double fraction = (double) i / detail;

                    // Y offset based on how tall the tornado is
                    double yOffset = fraction * height;

                    // Current radius transitions from bottomRadius to topRadius
                    double currentRadius = bottomRadius + fraction * (topRadius - bottomRadius);

                    // Add a small spiral effect by offsetting the angle further
                    double angle = baseAngle + (Math.PI * fraction);

                    // Convert polar to Cartesian coordinates
                    double x = currentRadius * Math.cos(angle);
                    double z = currentRadius * Math.sin(angle);

                    // The final location for this particle
                    Location spawnLoc = location.clone().add(x, yOffset, z);
                    if(spawnLoc.getWorld() == null) return;

                    displayParticles(spawnLoc.getWorld(), particle, spawnLoc, 1, 0.1, 0.1, 0.1, pSpeed, dustOptions, 180);
                }

                currentTick+=2;
            }
        }.runTaskTimer(LOTM.getInstance(), delay, 2);
    }

    /**
     * Spawns a quarter-circle arc of particles oriented toward the given direction,
     * starting "behind & above" the given location and ending "forward & below".
     *
     * @param location    Base location.
     * @param direction   The direction (forward) the arc should face.
     * @param particle    Particle type.
     * @param dustOptions If particle is REDSTONE, optionally provide DustOptions (color/size).
     * @param detail      Number of steps (particles) in the arc.
     */
    public static void spawnQuarterCircleArc(
            Location location,
            Vector direction,
            Particle particle,
            @Nullable Particle.DustOptions dustOptions,
            int detail
    ) {
        if (location.getWorld() == null || detail <= 0) {
            return;
        }

        World world = location.getWorld();

        // Normalize 'direction' so it only represents orientation
        Vector dir = direction.clone().normalize();

        // Pick a global up that isn't almost parallel with dir
        Vector globalUp = new Vector(0, 1, 0);
        if (Math.abs(dir.dot(globalUp)) > 0.95) {
            // If direction is nearly vertical, pick a different "up"
            globalUp = new Vector(1, 0, 0);
        }

        // Compute local axes:
        // right = forward × globalUp
        // up    = right   × forward
        Vector right = dir.clone().crossProduct(globalUp).normalize();
        Vector up    = right.clone().crossProduct(dir).normalize();

        // This is the radius of the arc in "local" space
        double radius = 1.0;

        // We want to go from angle = π/2 (behind+above) down to 0 (forward+below).
        // That "reverses" the direction of the arc’s belly.
        for (int i = 0; i <= detail; i++) {
            double fraction = i / (double) detail;
            // Angle goes from π/2 at fraction=0, down to 0 at fraction=1
            double angle = (1.0 - fraction) * (Math.PI / 2);

            // localX < 0 => behind, localX > 0 => forward
            // localY > 0 => above,  localY < 0 => below
            // Subtract 0.5 so the final point is below/forward
            double localX = radius * (Math.cos(angle) - 0.5);
            double localY = radius * (Math.sin(angle) - 0.5);

            // Convert (localX, localY) into world space using dir (forward) + up
            Vector offset = dir.clone().multiply(localX).add(up.clone().multiply(localY));

            Location point = location.clone().add(offset);

            // Spawn the particle (handle DustOptions if REDSTONE)
            if (particle == Particle.DUST && dustOptions != null) {
                world.spawnParticle(particle, point, 1, 0, 0, 0, 0, dustOptions);
            } else {
                world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
            }
        }
    }

    private static Vector getOrthogonalVector(Vector vector) {
        // Returns a vector orthogonal to the input vector
        if (vector.getX() != 0) {
            return new Vector(-(vector.getY() + vector.getZ()) / vector.getX(), 1, 1).normalize();
        } else if (vector.getY() != 0) {
            return new Vector(1, -(vector.getX() + vector.getZ()) / vector.getY(), 1).normalize();
        } else {
            return new Vector(1, 1, -(vector.getX() + vector.getY()) / vector.getZ()).normalize();
        }
    }

    public static void createParticleCube(World world, Location center, int radius, boolean showToPlayerOnly, @Nullable Player player, Particle particle) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }

        int startX = center.getBlockX() - radius;
        int startY = center.getBlockY() - radius;
        int startZ = center.getBlockZ() - radius;

        int endX = center.getBlockX() + radius;
        int endY = center.getBlockY() + radius;
        int endZ = center.getBlockZ() + radius;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    // Check if the block is on the boundary of the cube
                    boolean isBoundary = x == startX || x == endX || y == startY || y == endY || z == startZ || z == endZ;

                    if (isBoundary) {
                        if (showToPlayerOnly) {
                            if (player != null) {
                                player.spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
                            }
                        } else {
                            world.spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }
}
