package common.messages;

import common.Location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A request from a client to ask for termination of a reservation
 *
 * @see Message
 */
public class EndReservationRequest extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(943245, new EndReservationRequest());
    }

    /**
     * The code of the reservation
     */
    private int reservationCode;

    /**
     * The new location of the scooter
     */
    private Location location;

    /**
     * Default constructor
     */
    public EndReservationRequest() {
        reservationCode = 0;
        location = new Location(0,0);
    }
    /**
     * Parameterized constructor
     * @param l the new location of the scooter
     * @param c the code of the reservation
     */
    public EndReservationRequest(Location l, int c) {
        location = l;
        reservationCode = c;
    }

    /**
     * Gets the new location of the scooter
     * @return the new location of the scooter
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the code of the reservation
     * @return the code of the reservation
     */
    public int getReservationCode() {
        return reservationCode;
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
        out.writeInt(reservationCode);
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
        int c = in.readInt();

        return new EndReservationRequest(l, c);
    }
}
