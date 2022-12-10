package common.messages;

import common.Location;
import common.Scooter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * A response to the {@link, #FreeScootersWithinDistanceRequest} request
 */
public class FreeScootersWithinDistanceResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(16707, new FreeScootersWithinDistanceResponse());
    }

    /**
     * The set of scooters within the given distance (composition)
     */
    private Set<Scooter> scooters;


    /**
     * Default constructor
     */
    public FreeScootersWithinDistanceResponse() {
        scooters = new TreeSet<>();
    }

    /**
     * Parameterized constructor
     * @param sc the scooters in range
     */
    public FreeScootersWithinDistanceResponse(Set<Scooter> sc) {
        scooters = new TreeSet<>();
        for(Scooter s : sc)
            scooters.add(new Scooter(s));
    }

    /**
     * Gets the scooters in range
     * @return the scooters in range (composition
     */
    public Set<Scooter> getScooters() {
        Set<Scooter> ans = new TreeSet<>();

        for(Scooter s : scooters)
            ans.add(new Scooter(s));

        return ans;
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
        out.writeInt(scooters.size());

        for(Scooter sc : scooters)
            sc.serialize(out);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        int count = in.readInt();

        Set<Scooter> sc = new TreeSet<>();

        for(int i = 0; i < count; i++)
            sc.add(Scooter.deserialize(in));

        return new FreeScootersWithinDistanceResponse(sc);
    }
}
