package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private ServerFacade facade;

    public Server() {
        facade = new ServerFacade();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while(true) {
            Socket clientSocket = serverSocket.accept();

            //Process client in a different thread
            //TODO:: Handle client exceptions gracefully
            new Thread(new ClientHandler(facade, clientSocket))
                    .start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}
