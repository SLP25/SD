package server;

import common.messages.LoginRequest;
import server.messageHandling.LoginRequestHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LoginRequest request = new LoginRequest("", "");
        LoginRequestHandler rhandler = new LoginRequestHandler();
        Server server = new Server();
        server.start(20023);
    }
}
