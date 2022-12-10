package common;

import java.io.*;

/**
 * A user of the platform
 */
public class User extends Lockable {

    /**
     * The username used to login
     */
    private String username;

    /**
     * The password of the user
     */
    private String password;

    /**
     * Default constructor
     * @param username the username of the user
     * @param password the password of the user
     */
    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * Copy constructor.
     *
     * Returns a deep copy of the given user.
     * @param user the user to copy
     */
    public User(User user) {
        this(user.getUsername(), user.getPassword());
    }

    /**
     * Returns the username of the current user
     * @return the username of the current user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Compares the given password with the user's actual password
     * @param attempt the attempted password
     * @return Whether the given password matches the one of the current user
     */
    public boolean isPassword(String attempt) {
        return password.equals(attempt);
    }

    /**
     * Gets the password of the current user
     * @return the password of the current user
     */
    private String getPassword() {
        return password;
    }

    /**
     * Serializes the object into a DataOutputStream
     *
     * @implNote The stream is not flushed after writing to it
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(username);
        out.writeUTF(password);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    public static User deserialize(DataInputStream in) throws IOException {
        String username = in.readUTF();
        String password = in.readUTF();

        return new User(username, password);
    }

    /**
     * Creates a deep copy of the current object
     * @return a deep copy of the current object
     */
    @Override
    public Object clone() {
        return new User(this);
    }

    @Override
    public String toString() {
        return username;
    }
}
