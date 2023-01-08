package server.messageHandling;

import common.Notification;
import common.TaggedConnection;
import common.messages.CancelNotificationsRequest;
import common.messages.Message;
import common.messages.RewardNotification;
import common.messages.SendNotificationsRequest;
import server.ClientHandler;
import server.ServerFacade;
import server.SubscribableQueue;

public class CancelNotificationsHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(CancelNotificationsRequest.class, new CancelNotificationsHandler());
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
        if (state.subscription != null) {
            state.subscription.close();
            state.subscription = null;
        }

        return null;
    }
}
