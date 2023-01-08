package common.messages;

import common.Notification;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RewardNotification extends Message {

    static {
        Message.registerSubClass(3141592, new RewardNotification());
    }

    Notification notification;

    private RewardNotification() { }

    public RewardNotification(Notification n) {
        this.notification = n;
    }

    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        //TODO
    }

    @Override
    protected RewardNotification deserializeMessage(DataInputStream in) throws IOException {
        //TODO
        return null;
    }
}
