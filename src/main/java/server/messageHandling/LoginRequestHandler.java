package server.messageHandling;

import common.User;
import common.messages.LoginRequest;
import common.messages.LoginResponse;
import common.messages.Message;
import server.ClientHandler;
import server.ServerFacade;
import java.util.function.Consumer;

public class LoginRequestHandler implements IMessageHandler {
    static {
        ClientHandler.registerHandler(LoginRequest.class, new LoginRequestHandler());
    }
    @Override
    public Message processMessage(ServerFacade facade, Message message, Consumer<User> setUser) {
        if(!(message instanceof LoginRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than login requests");

        LoginRequest request = (LoginRequest)message;

        User u = facade.authenticate(request.getUsername(), request.getPassword());
        setUser.accept(u);
        System.out.println(u != null);

        LoginResponse response = new LoginResponse(u);
        return response;
    }
}
