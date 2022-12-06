package server.messageHandling;

import common.messages.LoginRequest;
import common.messages.Message;
import server.ClientHandler;
import server.ServerFacade;

public class LoginRequestHandler implements IMessageHandler {
    static {
        ClientHandler.registerHandler(LoginRequest.class, new LoginRequestHandler());
    }
    @Override
    public Message processMessage(ServerFacade facade, Message message) {
        if(!(message instanceof LoginRequest))
            throw new RuntimeException("Cannot process messages other than login requests");

        LoginRequest request = (LoginRequest)message;

        boolean result = facade.authenticate(request.getUsername(), request.getPassword());
        System.out.println(result);
        return request;
    }
}
