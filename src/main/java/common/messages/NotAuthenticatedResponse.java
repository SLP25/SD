package common.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NotAuthenticatedResponse extends Message {
    static {
        Message.registerSubClass(234663, new NotAuthenticatedResponse());
    }
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
    }

    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        return new NotAuthenticatedResponse();
    }
}
