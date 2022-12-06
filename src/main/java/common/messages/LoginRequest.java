package common.messages;

import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginRequest extends Message {
    static {
        Message.registerSubClass(3754202, new LoginRequest("", ""));
    }

    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    @Override
    protected void serializeMessage(ObjectOutputStream stream) throws IOException {
        stream.writeUTF(username);
        stream.writeUTF(password);
    }

    @Override
    protected Message deserializeMessage(ObjectInputStream stream) throws IOException {
        String user = stream.readUTF();
        String pass = stream.readUTF();

        return new LoginRequest(user, pass);
    }
}
