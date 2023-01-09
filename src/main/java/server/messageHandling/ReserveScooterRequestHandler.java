package server.messageHandling;

import common.Reservation;
import common.TaggedConnection;
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
     * @param frame the incoming request
     * @param state the connection state
     * @return the appropriate response
     */
    @Override
    public Message processMessage(ServerFacade facade, TaggedConnection.Frame frame, ClientHandler.State state) {
        Message message = frame.getMessage();
        if(!(message instanceof ReserveScooterRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than registration requests");

        if(state.currentUser == null) {
            return new NotAuthenticatedResponse();
        }

        ReserveScooterRequest request = (ReserveScooterRequest)message;

        Reservation r = facade.reserveScooter(state.currentUser.getUsername(), request.getLocation());

        ReserveScooterResponse response = new ReserveScooterResponse(r.getStartLocation(), r.getId());
        return response;
    }
}
