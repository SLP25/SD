package common.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CancelNotificationsRequest extends Message {

    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {

    }

    @Override
    protected Message deserializeMessage(DataInputStream in) throws IOException {
        return null;
    }
}
