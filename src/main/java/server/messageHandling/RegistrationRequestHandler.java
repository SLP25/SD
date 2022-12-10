package server.messageHandling;

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
     * @param message the incoming request
     * @param setUser a method used to set the user who made the request. Useful to set the current user on
     *                login requests
     * @return the appropriate response
     */
    @Override
    public Message processMessage(ServerFacade facade, Message message, Consumer<User> setUser) {
        if(!(message instanceof RegistrationRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than login requests");

        RegistrationRequest request = (RegistrationRequest)message;

        User u = facade.register(request.getUsername(), request.getPassword());
        setUser.accept(u);
        System.out.println(u != null);

        RegistrationResponse response = new RegistrationResponse(u);
        return response;
    }
}
