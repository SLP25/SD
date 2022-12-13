package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main server wrapper class
 */
public class Server {
    /**
     * The socket the server is listening on
     */
    private ServerSocket serverSocket;

    /**
     * The facade of the server functionality
     */
    private ServerFacade facade;

    /**
     * Default constructor
     */
    public Server() {
        facade = new ServerFacade();
    }

    /**
     * Starts the server
     * @param port the port to listen on
     * @throws IOException if starting the socket fails
     */
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

    /**
     * Stops the server
     * @throws IOException if closing the socket failed
     */
    public void stop() throws IOException {
        serverSocket.close();
    }
}
