package common.messages;

import common.Location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A response from the server to a client to confirm a reservation of a scooter
 *
 * @see Message
 */
public class ReserveScooterResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(9635347, new ReserveScooterResponse());
    }

    /**
     * The reservation code
     */
    private int code;

    /**
     * The location of the request
     */
    private Location location;

    /**
     * Default constructor
     */
    public ReserveScooterResponse() {
        code = -1;
        location = null;
    }
    /**
     * Parameterized constructor
     * @param location the location of the reservation
     * @param code the code of the reservation
     */
    public ReserveScooterResponse(Location location, int code) {
        this.location = location;
        this.code = code;
    }

    /**
     * Gets the location of the request
     * @return the location of the request
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the code of the reservation
     * @return the code of the reservation
     */
    public int getReservationCode() {
        return code;
    }

    /**
     * Serializes an object to a DataOutputStream
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        out.writeBoolean(location != null);
        if(location != null)
            location.serialize(out);
        out.writeInt(code);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        boolean hasReservation = in.readBoolean();
        Location l = hasReservation ? Location.deserialize(in) : null;
        int c = in.readInt();

        return new ReserveScooterResponse(l, c);
    }

    @Override
    public String toString() {
        return String.format("ReserveScooterResponse (code: %d)", this.code);
    }
}
