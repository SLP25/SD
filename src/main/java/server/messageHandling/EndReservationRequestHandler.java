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
public class EndReservationRequestHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(EndReservationRequest.class, new EndReservationRequestHandler());
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
        if(!(message instanceof EndReservationRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than registration requests");

        EndReservationRequest request = (EndReservationRequest) message;

        int cost = facade.endReservation(user.getUsername(), request.getReservationCode(), request.getLocation());

        EndReservationResponse response = new EndReservationResponse(cost);
        return response;
    }
}
