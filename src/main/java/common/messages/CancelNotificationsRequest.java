package common.messages;

import common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CancelNotificationsRequest extends Message {
    static {
        Message.registerSubClass(167123775, new CancelNotificationsRequest());
    }
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {

    }

    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        return null;
    }
}
