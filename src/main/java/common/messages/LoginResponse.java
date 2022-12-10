package common.messages;

import common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LoginResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(1670684193, new LoginResponse(new User("", "")));
    }
    private User user;
    public LoginResponse(User user) {
        this.user = user == null ? null : new User(user);
    }

    public User getUser() {
        return user == null ? null : new User(user);
    }

    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        out.writeBoolean(user != null);
        if(user != null)
            user.serialize(out);
    }

    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        User u = null;
        boolean hasUser = in.readBoolean();
        if(hasUser)
            u = User.deserialize(in);

        return new LoginResponse(u);
    }
}
