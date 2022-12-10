package server;

import common.User;
import common.messages.LoginRequest;
import common.messages.LoginResponse;
import server.messageHandling.LoginRequestHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LoginRequest request = new LoginRequest("", "");
        LoginResponse lr1 = new LoginResponse(new User("", ""));
        LoginRequestHandler handler = new LoginRequestHandler();
        Server server = new Server();
        server.start(20023);
    }
}
