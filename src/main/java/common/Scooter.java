package common;

import java.io.*;

/**
 * A scooter. Scooters can be placed across the grid and
 * be reserved / moved by users.
 */
public class Scooter extends Lockable implements Comparable<Scooter> {

    /**
     * The unique identifier of the scooter
     */
    private final int id;

    /**
     * The current location of the scooter
     */
    private Location location;

    /**
     * The user the scooter is allocated to (null if the scooter
     * is not being used by any user)
     */
    private User user;

    /**
     * Creates a scooter with no user allocated to it
     * @param id the unique id of the scooter
     * @param location the current location of the scooter
     */
    public Scooter(int id, Location location) {
        super();
        this.id = id;
        this.location = location;
        this.user = null;
    }

    /**
     * Creates a scooter allocated to a user
     * @param id the unique id of the scooter
     * @param location the current location of the scooter
     * @param user the user the scooter is allocated to
     */
    public Scooter(int id, Location location, User user) {
        super();
        this.id = id;
        this.location = location;
        this.user = user;
    }

    /**
     * Copy constructor.
     * Creates a deep copy of the given scooter
     * @param scooter the scooter to copy
     */
    public Scooter(Scooter scooter) {
        super();
        this.id = scooter.getId();
        this.location = scooter.getLocation();
        this.user = scooter.getUser();
    }

    /**
     * Gets the unique identifier of the current scooter
     * @return the unique identifier of the current scooter
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user the scooter is allocated to
     * @return the user the scooter is allocated to
     */
    private User getUser() {
        return user;
    }

    /**
     * Gets the current location of the scooter
     * @return the current location of the scooter
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets whether the scooter is allocated to a user
     * @return whether the scooter is allocated to a user
     */
    public boolean isReserved() {
        return user != null;
    }

    /**
     * Allocates the scooter to a user
     * @param user the user to allocate the scooter to
     */
    public void reserve(User user) {
        this.user = user;
    }

    /**
     * Deallocates the scooter and parks it at a new location
     * @param newLocation the new location of the scooter
     */
    public void free(Location newLocation) {
        this.location = newLocation;
        this.user = null;
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
        out.writeInt(id);
        location.serialize(out);
        out.writeBoolean(user != null);
        if(user != null)
            user.serialize(out);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    public static Scooter deserialize(DataInputStream in) throws IOException {
        int id = in.readInt();
        Location location = Location.deserialize(in);

        boolean hasUser = in.readBoolean();
        User user = null;
        if(hasUser)
            user = User.deserialize(in);

        return new Scooter(id, location, user);
    }

    /**
     * Compares the given scooter with the current one.
     * A scooter is greater than another if and only if its identifier is greater
     *
     * @param sc the scooter to compare to
     * @return > 0 if the current scooter is greater, 0 if equal, <0 if lesser
     */
    public int compareTo(Scooter sc) {
        return this.getId() - sc.getId();
    }

    /**
     * Creates a deep copy of the current object
     * @return a deep copy of the current object
     */
    @Override
    public Object clone() {
        return new Scooter(this);
    }

    @Override
    public String toString() {
        return String.format("Id: %d, Location: %s, User: %s", id, location.toString(), user == null ? "null" :
                user.toString());
    }
}
