package common.messages;

import common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A message corresponding to the response of the server to a {@link, common.messages.LoginRequest}
 */
public class RegistrationResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(1670698175, new RegistrationResponse(new User("", "")));
    }

    /**
     * The user who successfully registered (composition) / null if registration failed
     */
    private User user;

    /**
     * Default constructor
     */
    public RegistrationResponse() {
        user = null;
    }

    /**
     * Parameterized constructor
     * @param user the user who successfully registered / null if registration failed
     */
    public RegistrationResponse(User user) {
        this.user = user == null ? null : new User(user);
    }

    /**
     * Gets the user who successfully registered / null if registration failed
     * @return  the user who successfully registered / null if registration failed
     */
    public User getUser() {
        return user == null ? null : new User(user);
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
        out.writeBoolean(user != null);
        if(user != null)
            user.serialize(out);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        User u = null;
        boolean hasUser = in.readBoolean();
        if(hasUser)
            u = User.deserialize(in);

        return new RegistrationResponse(u);
    }
}
