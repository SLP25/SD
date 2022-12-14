package server.messageHandling;

import common.Scooter;
import common.User;
import common.messages.*;
import server.ClientHandler;
import server.ServerFacade;

import java.util.Set;
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
     * @param message the incoming request
     * @param user the current user
     * @param setUser a method used to set the user who made the request. Useful to set the current user on
     *                login requests
     * @return the appropriate response
     */
    @Override
    public Message processMessage(ServerFacade facade, Message message, User user, Consumer<User> setUser) {
        if(!(message instanceof FreeScootersWithinDistanceRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than free scooters within distance requests");
        //TODO:: Check authentication
        FreeScootersWithinDistanceRequest request = (FreeScootersWithinDistanceRequest)message;

        Set<Scooter> ans = facade.getFreeScootersInDistance(request.getLocation());

        System.out.println(ans.size());
        FreeScootersWithinDistanceResponse response = new FreeScootersWithinDistanceResponse(ans);
        return response;
    }
}
