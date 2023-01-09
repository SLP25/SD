package server.messageHandling;

import common.Location;
import common.TaggedConnection;
import common.User;
import common.messages.*;
import server.ClientHandler;
import server.ServerFacade;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A class implementing a handler for {@link, common.messages.FreeScootersWithinDistanceRequest}
 */
public class FreeScootersWithinDistanceRequestHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(FreeScootersWithinDistanceRequest.class, new FreeScootersWithinDistanceRequestHandler());
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
        if(!(message instanceof FreeScootersWithinDistanceRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than free scooters within distance requests");

        if(state.currentUser == null) {
            return new NotAuthenticatedResponse();
        }

        FreeScootersWithinDistanceRequest request = (FreeScootersWithinDistanceRequest)message;

        Map<Location, Integer> ans = facade.getFreeScootersInDistance(request.getLocation());

        FreeScootersWithinDistanceResponse response = new FreeScootersWithinDistanceResponse(ans);
        return response;
    }
}
