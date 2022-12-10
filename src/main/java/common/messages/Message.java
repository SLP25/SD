package common.messages;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An abstract class representing a message between the server
 * and the client (in any direction, meaning both from server to client and
 * client to server).
 *
 * This class is responsible for serializing and deserializing the messages, at
 * least by identified which message type it is (each message type is a subclass
 * of Message), and delegating the (de)serialization to that particular subclass.
 *
 * To implement this, each subclass must register itself in the superclass with a
 * unique identifier, which should be a random integer, to avoid collisions.
 */
public abstract class Message {
    /**
     * The correspondence between the registered subclasses and their
     * respective unique identifier
     */
    private static Map<Integer, Message> registeredSubClasses = new HashMap<>();

    /**
     * The mapping of subclasses and their respective identifiers. Inverse of
     * {@link Message#registeredSubClasses}
     */
    public static Map<Class<? extends Message>,Integer> codes = new HashMap<>();

    /**
     * Register a subclass with its superclass
     * @param code the unique identifier of the subclass. Should be a randomly selected (hardcoded) integer
     * @param subclass an instance of the subclass to register
     */
    public static void registerSubClass(int code, Message subclass) {
        if(registeredSubClasses.containsKey(code))
            throw new AlreadyRegisteredException("Code " + code + " already exists");

        if(codes.containsKey((subclass.getClass())))
            throw new AlreadyRegisteredException("Class " + subclass.getClass().toString() + " already registered");

        registeredSubClasses.put(code, subclass);
        codes.put(subclass.getClass(), code);
    }

    /**
     * Serializes an object to a DataOutputStream
     *
     * @implNote this method should be implemented by the subclass, and refers
     * to the serialization of the subclass only, i.e., no message code should
     * be written
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    protected abstract void serializeMessage(DataOutputStream out) throws IOException;

    /**
     * Deerializes an object from a DataInputStream
     *
     * @implNote this method should be implemented by the subclass, and refers
     * to the serialization of the subclass only, i.e., no message code should
     * be written
     *
     * @param in the given DataInputStream
     * @throws IOException if reading to the stream failed
     */
    protected abstract Message deserializeMessage(DataInputStream in) throws IOException;

    /**
     * Serializes an object to a DataOutputStream
     *
     * @implNote this method only determines the subclass of the Message being serialized,
     * and delegates the serialization to that subclass, i.e., it only writes the subclass
     * code to the stream
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(codes.get(this.getClass()));
        this.serializeMessage(out);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @implNote this method only determines the subclass of the Message being deserialized,
     * and delegates the deserialization to that subclass, i.e., it only reads the subclass
     * code to the stream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    public static Message deserialize(DataInputStream in) throws IOException {
        int code = in.readInt();
        System.out.println(registeredSubClasses.size());
        if(!registeredSubClasses.containsKey(code))
            throw new IOException("No sub class with code " + code);

        return registeredSubClasses.get(code).deserializeMessage(in);
    }
}
