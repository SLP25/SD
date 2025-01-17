package common.messages;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * A request from a client to the server to authenticate
 * as a user
 *
 * @see Message
 */
public class LoginRequest extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(3754202, new LoginRequest("", ""));
    }

    /**
     * The username the client wants to log in as
     */
    private String username;

    /**
     * The password provided by the client for the user
     */
    private String password;

    public LoginRequest() {
        username = "";
        password = "";
    }
    /**
     * Default constructor
     * @param username the username to login as
     * @param password the password of the user
     */
    public LoginRequest(String username, String password) {
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
     * Serializes an object to a DataOutputStream
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
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

        return new LoginRequest(user, pass);
    }

    @Override
    public String toString() {
        return String.format("LoginRequest (username: '%s', password: '%s')", this.username, this.password);
    }
}
