package common.messages;

import common.Location;
import common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A request where a client inquires the server regarding the available scooter within a certain
 * distance of a location
 */
public class FreeScootersWithinDistanceRequest extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(1670707048, new FreeScootersWithinDistanceRequest());
    }

    /**
     * The location to be the center of the search
     */
    private Location location;

    /**
     * The maximum distance from the location
     */
    private int maxDistance;

    /**
     * Default constructor
     */
    public FreeScootersWithinDistanceRequest() {
        location = null;
        maxDistance = 0;
    }

    /**
     * Parameterized constructor
     * @param l the location to be the center of the search
     * @param distance the maximum distance from the location
     */
    public FreeScootersWithinDistanceRequest(Location l, int distance) {
        this.location = l;
        this.maxDistance = distance;
    }

    /**
     * Gets the location to be the center of the search
     * @return the location to be the center of the search
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the maximum distance from the location
     * @return the maximum distance from the location
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Serializes the object into a DataOutputStream
     *
     * @implNote The stream is not flushed after writing to it
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
        int distance = in.readInt();

        return new FreeScootersWithinDistanceRequest(l, distance);
    }
}
