package server.messageHandling;

import common.Reservation;
import common.User;
import common.messages.*;
import server.ClientHandler;
import server.ServerFacade;

import java.util.function.Consumer;

/**
 * A class implementing a handler for {@link, common.messages.RegistrationRequest}
 */
public class ReserveScooterRequestHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(ReserveScooterRequest.class, new ReserveScooterRequestHandler());
    }

    /**
     * Method responsible for processing the request and computing the response of the server
     * @param facade the facade of the server
     * @param message the incoming request
     * @param user the current user
     * @param setUser a method used to set the user who made the request. Useful to set the current user on
     *                login requests
     * @return
     */
    @Override
    public Message processMessage(ServerFacade facade, Message message, User user, Consumer<User> setUser) {
        if(!(message instanceof ReserveScooterRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than registration requests");

        ReserveScooterRequest request = (ReserveScooterRequest)message;

        Reservation r = facade.reserveScooter(user.getUsername(), request.getLocation());

        ReserveScooterResponse response = new ReserveScooterResponse(r.getStartLocation(), r.getId());
        return response;
    }
}
