package common.messages;

import common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendNotificationsRequest extends Message {
    static {
        Message.registerSubClass(1672345, new SendNotificationsRequest());
    }
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {

    }

    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        return new SendNotificationsRequest();
    }
}
