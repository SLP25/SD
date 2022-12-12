package common.messages;

import common.Location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A request from a client to ask for reservation of the scooter closest to
 * the given location (limited in maximum distance)
 *
 * @see Message
 */
public class ReserveScooterRequest extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(9673467, new ReserveScooterRequest());
    }

    /**
     * The maximum distance the scooter must be of the given location
     */
    private int maxDistance;

    /**
     * The location of the request
     */
    private Location location;

    /**
     * Default constructor
     */
    public ReserveScooterRequest() {
        maxDistance = 0;
        location = new Location(0,0);
    }
    /**
     * Parameterized constructor
     * @param l the location of the request
     * @param d the maximum distance the scooter must be of the given location
     */
    public ReserveScooterRequest(Location l, int d) {
        location = l;
        maxDistance = d;
    }

    /**
     * Gets the location of the request
     * @return the location of the request
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the maximum distance the scooter must be of the given location
     * @return the maximum distance the scooter must be of the given location
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Serializes an object to a DataOutputStream
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        location.serialize(out);
        out.writeInt(maxDistance);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        Location l = Location.deserialize(in);
        int d = in.readInt();

        return new ReserveScooterRequest(l, d);
    }
}
