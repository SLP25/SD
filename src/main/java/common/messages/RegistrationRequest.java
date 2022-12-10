package common.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A request from a client to the server to register
 * as a new user
 *
 * @see Message
 */
public class RegistrationRequest extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(1670698141, new RegistrationRequest("", ""));
    }

    /**
     * The username the client wants to register as
     */
    private String username;

    /**
     * The password provided by the client for the user
     */
    private String password;

    public RegistrationRequest() {
        username = "";
        password = "";
    }
    /**
     * Default constructor
     * @param username the username to register as
     * @param password the password of the user
     */
    public RegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username of the request
     * @return the username of the request
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password in the request
     * @return the password in the request
     */
    public String getPassword() {
        return password;
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param out the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        out.writeUTF(username);
        out.writeUTF(password);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        String user = in.readUTF();
        String pass = in.readUTF();

        return new RegistrationRequest(user, pass);
    }
}
