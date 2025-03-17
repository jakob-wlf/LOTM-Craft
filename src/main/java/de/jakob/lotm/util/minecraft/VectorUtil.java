package de.jakob.lotm.util.minecraft;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class VectorUtil {

    public static Vector rotateAroundY(Vector vector, double angle) {
        double radians = Math.toRadians(angle);

        double x = vector.getX();
        double z = vector.getZ();

        // Calculate new X and Z coordinates using rotation matrix
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double newX = x * cos - z * sin;
        double newZ = x * sin + z * cos;

        return new Vector(newX, vector.getY(), newZ).normalize();
    }

    public static Vector rotateAroundX(Vector vector, double angle) {
        double radians = Math.toRadians(angle);

        double y = vector.getY();
        double z = vector.getZ();

        // Calculate new Y and Z coordinates using rotation matrix
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double newY = y * cos - z * sin;
        double newZ = y * sin + z * cos;

        return new Vector(vector.getX(), newY, newZ);
    }

    public static Vector rotateAroundY(Vector vector, double angle, boolean convertToRadians) {
        double finalAngle = convertToRadians ? Math.toRadians(angle) : angle;

        double x = vector.getX();
        double z = vector.getZ();

        // Calculate new X and Z coordinates using rotation matrix
        double cos = Math.cos(finalAngle);
        double sin = Math.sin(finalAngle);

        double newX = x * cos + z * sin;
        double newZ = x * -sin + z * cos;

        return new Vector(newX, vector.getY(), newZ);
    }

    public static Vector rotateAroundX(Vector vector, double angle, boolean convertToRadians) {

        double finalAngle = convertToRadians ? Math.toRadians(angle) : angle;

        double y = vector.getY();
        double z = vector.getZ();

        // Calculate new Y and Z coordinates using rotation matrix
        double cos = Math.cos(finalAngle);
        double sin = Math.sin(finalAngle);

        double newY = y * cos - z * sin;
        double newZ = y * sin + z * cos;

        return new Vector(vector.getX(), newY, newZ);
    }

    public static Vector getBackVector(Location loc) {
        final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 90))));
        final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 90))));
        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    public static Vector getBackVector(Vector orientation) {
        // Assuming "back" is the opposite direction of "forward"
        return orientation.clone().multiply(-1);
    }

    public static Vector rotateVector(Vector v, Vector orientation) {
        double angle = orientation.length();
        if (angle == 0) return v; // No rotation if the angle is zero

        // Rotate around the orientation vector (using axis-angle rotation formula)
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Axis components (x, y, z from the normalized orientation)
        double ux = orientation.getX();
        double uy = orientation.getY();
        double uz = orientation.getZ();

        double vx = v.getX();
        double vy = v.getY();
        double vz = v.getZ();

        // Applying the rotation formula to rotate the vector
        double newX = cos * vx + sin * (uy * vz - uz * vy) + (1 - cos) * (ux * (ux * vx + uy * vy + uz * vz));
        double newY = cos * vy + sin * (uz * vx - ux * vz) + (1 - cos) * (uy * (ux * vx + uy * vy + uz * vz));
        double newZ = cos * vz + sin * (ux * vy - uy * vx) + (1 - cos) * (uz * (ux * vx + uy * vy + uz * vz));

        return new Vector(newX, newY, newZ);
    }

    public static void rotateAroundDirection(Vector vec, Vector direction) {
        // Create the orthogonal vectors for the player's direction (up, right, forward)
        Vector up = new Vector(0, 1, 0); // World up vector
        Vector right = direction.clone().crossProduct(up).normalize(); // Right vector relative to the player's direction
        up = right.clone().crossProduct(direction).normalize(); // Recalculate the true up vector for the direction

        // Calculate the rotated vector using the player's right and up vectors
        Vector rotatedVec = new Vector(
                vec.getX() * direction.getX() + vec.getY() * up.getX() + vec.getZ() * right.getX(),
                vec.getX() * direction.getY() + vec.getY() * up.getY() + vec.getZ() * right.getY(),
                vec.getX() * direction.getZ() + vec.getY() * up.getZ() + vec.getZ() * right.getZ()
        );

        // Update the input vector with the rotated values
        vec.setX(rotatedVec.getX());
        vec.setY(rotatedVec.getY());
        vec.setZ(rotatedVec.getZ());
    }


    private static final double EPSILON = Math.ulp(1.0d) * 2d;

    private static boolean isSignificant(double value) {
        return Math.abs(value) >= EPSILON;
    }


    public static Location getRelativeLocation(Location loc, double forward, double right, double up) {
        Location ret = loc.clone();
        Vector direction = null;
        if (isSignificant(forward)) {
            direction = ret.getDirection();
            ret.add(direction.clone().multiply(forward));
        }
        boolean hasUp = isSignificant(up);
        if (hasUp && direction == null) direction = ret.getDirection();
        if (isSignificant(right) || hasUp) {
            Vector rightDirection;
            if (direction != null && isSignificant(Math.abs(direction.getY()) - 1)) {
                rightDirection = direction.clone();
                double factor = Math.sqrt(1 - Math.pow(rightDirection.getY(), 2)); // a shortcut that lets us not normalize which is slow
                double nx = -rightDirection.getZ() / factor;
                double nz = rightDirection.getX() / factor;
                rightDirection.setX(nx);
                rightDirection.setY(0d);
                rightDirection.setZ(nz);
            } else {
                float yaw = ret.getYaw() + 90f;
                double yawRad = yaw * (Math.PI / 180d);
                double z = Math.cos(yawRad);
                double x = -Math.sin(yawRad);
                rightDirection = new Vector(x, 0d, z);
            }
            ret.add(rightDirection.clone().multiply(right));
            if (hasUp) {
                Vector upDirection = rightDirection.crossProduct(direction);
                ret.add(upDirection.clone().multiply(up));
            }
        }
        return ret;
    }

}
