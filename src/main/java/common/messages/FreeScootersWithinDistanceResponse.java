package common.messages;

import common.Location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * A response to the {@link, #FreeScootersWithinDistanceRequest} request
 */
public class FreeScootersWithinDistanceResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(16707, new FreeScootersWithinDistanceResponse());
    }

    /**
     * All scooters in range (location and number of scooters per location)
     */
    private Map<Location, Integer> scooters;


    /**
     * Default constructor
     */
    public FreeScootersWithinDistanceResponse() {
        scooters = new TreeMap<>();
    }

    /**
     * Parameterized constructor
     * @param sc the scooters in range
     */
    public FreeScootersWithinDistanceResponse(Map<Location, Integer> sc) {
        scooters = new TreeMap<>();
        for(Map.Entry<Location, Integer> s : sc.entrySet())
            scooters.put(s.getKey(), s.getValue());
    }

    /**
     * Gets the scooters in range
     * @return All scooters in range (location and number of scooters per location)
     */
    public Map<Location, Integer> getScooters() {
        Map<Location, Integer> ans = new TreeMap<>();

        for(Map.Entry<Location, Integer> s : scooters.entrySet())
            ans.put(s.getKey(), s.getValue());

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

        for(Map.Entry<Location, Integer> s : scooters.entrySet()) {
            s.getKey().serialize(out);
            out.writeInt(s.getValue());
        }
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

        Map<Location, Integer> sc = new TreeMap<>();

        for(int i = 0; i < count; i++) {
            Location l = Location.deserialize(in);
            int c = in.readInt();
            sc.put(l, c);
        }

        return new FreeScootersWithinDistanceResponse(sc);
    }

    @Override
    public String toString() {
        return String.format("FreeScootersWithinDistanceResponse (%d unique scooter positions)", this.scooters.size());
    }
}
