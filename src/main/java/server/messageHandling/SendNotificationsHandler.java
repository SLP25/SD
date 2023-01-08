package server.messageHandling;

import common.Notification;
import common.TaggedConnection;
import common.messages.Message;
import common.messages.ReserveScooterRequest;
import common.messages.RewardNotification;
import common.messages.SendNotificationsRequest;
import server.ClientHandler;
import server.ServerFacade;
import server.SubscribableQueue;

import java.io.IOException;

public class SendNotificationsHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(SendNotificationsRequest.class, new ReserveScooterRequestHandler());
    }

    /**
     * Method responsible for processing the request and computing the response of the server
     * @param facade the facade of the server
     * @param frame the incoming request
     * @param state the connection state
     * @return the appropriate response
     */
    @Override
    public Message processMessage(ServerFacade facade, TaggedConnection.Frame frame, ClientHandler.State state) {
        Message message = frame.getMessage();
        if (state.subscription == null) {
            SubscribableQueue<Notification>.Subscription sub = facade.getRewardSubscription();
            state.subscription = sub;

            new Thread(() -> {
                for (Notification n : sub) {
                    try {
                        state.connection.send(frame.getTag(), new RewardNotification(n));
                    } catch (IOException e) {
                        System.out.println("Exception sending notification: " + e.getMessage());
                    }
                }
            }).start();
        }

        return null;
    }
}
