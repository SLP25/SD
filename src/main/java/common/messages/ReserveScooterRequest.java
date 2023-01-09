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
     * The location of the request
     */
    private Location location;

    /**
     * Default constructor
     */
    public ReserveScooterRequest() {
        location = new Location(0,0);
    }
    /**
     * Parameterized constructor
     * @param l the location of the request
     */
    public ReserveScooterRequest(Location l) {
        location = l;
    }

    /**
     * Gets the location of the request
     * @return the location of the request
     */
    public Location getLocation() {
        return location;
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

        return new ReserveScooterRequest(l);
    }

    @Override
    public String toString() {
        return String.format("ReserveScooterRequest (location: %s)", this.location);
    }
}
