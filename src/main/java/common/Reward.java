package common;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * A reward (monetary) for moving a scooter from one location to another.
 *
 * @implNote It is an immutable class
 */
public final class Reward {
    /**
     * The minimum monetary value of a reward
     */
    public static final int minimumPrize = 10000;

    /**
     * The maximum monetary value of a reward
     */
    public static final int maximumPrize = 10000;
    /**
     * The start location
     */
    private final Location startLocation;

    /**
     * The final location
     */
    private final Location endLocation;

    /**
     * The amount of money the user will receive by claiming this reward
     */
    private final int money;

    /**
     * Parameterized constructor
     * @param startLocation the starting location
     * @param endLocation the ending location
     * @param money the amount of money the user will receive by claiming this reward
     */
    public Reward(Location startLocation, Location endLocation, int money) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.money = money;
    }

    /**
     * Gets the starting location
     * @return the starting location
     */
    public Location getStartLocation() {
        return startLocation;
    }

    /**
     * Gets the ending location
     * @return the ending location
     */
    public Location getEndLocation() {
        return endLocation;
    }

    /**
     * Gets the amount of money the user will receive by claiming this reward
     * @return the amount of money the user will receive by claiming this reward
     */
    public int getMoney() {
        return money;
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    public static Reward deserialize(DataInputStream in) throws IOException {
        Location s = Location.deserialize(in);
        Location e = Location.deserialize(in);
        int m = in.readInt();
        return new Reward(s, e, m);
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
        startLocation.serialize(out);
        endLocation.serialize(out);
        out.writeInt(money);
    }

    @Override
    public String toString() {
        return String.format("Start: %s, End: %s, Money: %d", startLocation.toString(), endLocation.toString(), money);
    }
}
