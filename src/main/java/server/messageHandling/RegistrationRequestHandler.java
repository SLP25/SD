package server.messageHandling;

import common.TaggedConnection;
import common.User;
import common.messages.*;
import server.ClientHandler;
import server.ServerFacade;

import java.util.function.Consumer;

/**
 * A class implementing a handler for {@link, common.messages.RegistrationRequest}
 */
public class RegistrationRequestHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(RegistrationRequest.class, new RegistrationRequestHandler());
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
        if(!(message instanceof RegistrationRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than registration requests");

        RegistrationRequest request = (RegistrationRequest)message;

        User u = facade.register(request.getUsername(), request.getPassword());
        state.currentUser = u;

        RegistrationResponse response = new RegistrationResponse(u);
        return response;
    }
}
