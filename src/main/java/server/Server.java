package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
     * The thread responsible for generating rewards
     */
    private Thread rewardThread;

    /**
     * Instance responsible for generating rewards
     */
    private final RewardGenerator rewardGenerator;

    /**
     * Default constructor
     */
    public Server(int n, int d, int scooterCount) {
        facade = new ServerFacade(n, d, scooterCount);
        rewardGenerator = new RewardGenerator(facade, 40);
        facade.setRunRewards(rewardGenerator::setAwake);

    }

    /**
     * Starts the server
     * @param port the port to listen on
     * @throws IOException if starting the socket fails
     */
    public void start(int port) throws IOException {
        new Thread(rewardGenerator).start();
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
