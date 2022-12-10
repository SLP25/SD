package common;


import java.io.*;

/**
 * Represents the location of a cell in the map grid.
 *
 * The map is a grid of N rows and N columns. We will
 * represent each cell by its x and y coordinates, or by
 * the pair (x,y).
 *
 * The x-axis corresponds to the horizontal axis, increasing
 * in value from left to right; whilst the y-axis is the vertical
 * axis, increasing from top to bottom.
 *
 * This means (0,0) is the top-left cell, and (N,N), the bottom-right
 * one.
 *
 * Objects of this class are immutable, meaning no mutual exclusion
 * mechanism is needed.
 */
public final class Location implements Comparable<Location> {
    /**
     * The x coordinate of the cell
     *
     * @see Location
     */
    private final int x;

    /**
     * The y coordinate of the cell
     *
     * @see Location
     */
    private final int y;

    /**
     * Constructs a location with the given coordinates
     * @param x the x-axis coordinate
     * @param y the y-axis coordinate
     *
     * @see Location
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x coordinate of the Location
     * @return the x coordinate
     *
     * @see Location
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of the Location
     * @return the y coordinate
     *
     * @see Location
     */

    public int getY() {
        return y;
    }

    /**
     * Calculates the Manhattan distance between two locations.
     *
     * @param a the first location
     * @param b the second location
     * @return  the Manhattan distance between the given locations
     *
     * @see Location
     */
    public static int distance(Location a, Location b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Serializes the object into a DataOutputStream
     *
     * @implNote The stream is not flushed after writing to it
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(x);
        out.writeInt(y);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    public static Location deserialize(DataInputStream in) throws IOException {
        int x = in.readInt();
        int y = in.readInt();

        return new Location(x, y);
    }

    /**
     * Compares a Location to the current object.
     *
     * A location is said to be greater than another if its x coordinate
     * is greater, or, in case of a tie, its y coordinate being greater.
     *
     * For example,
     *
     * (3,5) > (2,5)
     * (3,5) > (1, 6)
     * (3,5) > (3,4)
     *
     * @param l the second location
     * @return a > 0 if the current object is greater, 0 if they are equal,
     * < 0 if the current object is less then the given one
     */
    public int compareTo(Location l) {
        if(x > l.getX())
            return 1;
        if(x < l.getX())
            return -1;

        return y - l.getY();
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }
}
