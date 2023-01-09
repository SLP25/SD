package common.messages;

import common.Location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RewardsWithinDistanceRequest extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(1673298240, new RewardsWithinDistanceRequest());
    }

    /**
     * The location to be the center of the search
     */
    private final Location location;

    /**
     * Default constructor
     */
    public RewardsWithinDistanceRequest() {
        location = null;
    }

    /**
     * Parameterized constructor
     * @param l the location to be the center of the search
     */
    public RewardsWithinDistanceRequest(Location l) {
        this.location = l;
    }

    /**
     * Gets the location to be the center of the search
     * @return the location to be the center of the search
     */
    public Location getLocation() {
        return location;
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
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected RewardsWithinDistanceRequest deserializeMessage(DataInputStream in) throws IOException {
        Location l = Location.deserialize(in);

        return new RewardsWithinDistanceRequest(l);
    }

    @Override
    public String toString() {
        return String.format("RewardsWithinDistanceRequest (location: %s)", this.location.toString());
    }
}