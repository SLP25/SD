package common.messages;

import common.Notification;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RewardNotification extends Message {

    static {
        Message.registerSubClass(3141592, new RewardNotification());
    }

    private final Notification notification;

    public RewardNotification() {
        this.notification = null;
    }

    public RewardNotification(Notification n) {
        this.notification = n;
    }

    public Notification getNotification() {
        return notification;
    }

    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        this.notification.serialize(out);
    }

    @Override
    protected RewardNotification deserializeMessage(DataInputStream in) throws IOException {
        return new RewardNotification(Notification.deserialize(in));
    }

    @Override
    public String toString() {
        return String.format("RewardNotification (%d rewards)", this.notification.rewards.size());
    }
}
