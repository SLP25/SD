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
public class EndReservationResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(97343245, new EndReservationResponse());
    }

    /**
     * The cost of the reservation
     */
    private int cost;

    /**
     * Default constructor
     */
    public EndReservationResponse() {
        cost = 0;
    }

    /**
     * Parameterized constructor
     * @param c the cost of the reservation
     */
    public EndReservationResponse(int c) {
        cost = c;
    }

    /**
     * Gets the cost of the reservation
     * @return the cost of the reservation
     */
    public int getCost() {
        return cost;
    }

    /**
     * Serializes an object to a DataOutputStream
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        out.writeInt(cost);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        int c = in.readInt();

        return new EndReservationResponse(c);
    }

    @Override
    public String toString() {
        return String.format("EndReservationResponse (cost: %d)", this.cost);
    }
}
