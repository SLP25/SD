package server.messageHandling;

import common.TaggedConnection;
import common.User;
import common.messages.LoginRequest;
import common.messages.LoginResponse;
import common.messages.Message;
import server.ClientHandler;
import server.ServerFacade;
import java.util.function.Consumer;

/**
 * A class implementing a handler for {@link, common.messages.LoginRequest}
 */
public class LoginRequestHandler implements IMessageHandler {
    //Register the class in the super class, as to allow for deserialization
    static {
        ClientHandler.registerHandler(LoginRequest.class, new LoginRequestHandler());
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
        if(!(message instanceof LoginRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than login requests");

        LoginRequest request = (LoginRequest)message;

        User u = facade.authenticate(request.getUsername(), request.getPassword());
        state.currentUser = u;
        System.out.println(u != null);

        LoginResponse response = new LoginResponse(u);
        return response;
    }
}
