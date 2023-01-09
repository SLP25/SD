package server;

import common.Notification;
import common.Reward;
import common.TaggedConnection;
import common.User;
import common.messages.Message;
import common.messages.RewardNotification;
import server.messageHandling.IMessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for handling a client connection. Instances of this class should
 * execute in their own, separate thread
 */
public class ClientHandler implements Runnable {

    /**
     * A map of all message handlers indexed by the class of the message they can process.
     *
     * Similarly to serializing messages, processing them in an elegant manner required the creation
     * of an interface for classes responsible for parsing certain messages. Each class implementing
     * the {@link, IMessageHandler} interface is responsible for parsing one and one message class
     * only.
     *
     * When processing a message, all this class does is determine (using this map), which handler should
     * it call based on the class of the incoming message. This allows this class to be simpler, and the splitting
     * the complex message handling amongst a greater number of simpler classes, resulting in easier to maintain code
     *
     * @see IMessageHandler
     */
    private static final Map<Class<? extends Message>, IMessageHandler> handlers = new HashMap<>();

    /**
     * Registers the handler of a message class. Refer to {@link, #handlers}
     * @param messageClass the class of the message the handler can process
     * @param handler an instance of the class responsible for handling the message
     */
    public static void registerHandler(Class<? extends Message> messageClass, IMessageHandler handler) {
        if(handlers.containsKey(messageClass)) {
            throw new RuntimeException("Already registered handler");
        }

        handlers.put(messageClass, handler);
    }



    /**
     * The socket of the client connection
     */
    private final Socket socket;

    /**
     * The server facade. Exposes all functionality
     */
    private final ServerFacade facade;

    /**
     * Parameterized constructor
     * @param facade the server facade
     * @param clientSocket the socket the client is connected in
     */
    public ClientHandler(ServerFacade facade, Socket clientSocket) {
        this.facade = facade;
        this.socket = clientSocket;
    }


    /**
     * Contains all stateful information about the connection
     */
    public static class State implements AutoCloseable {

        /**
         * The tagged connection to the client
         */
        public final TaggedConnection connection;

        /**
         * The user logged in
         */
        public User currentUser;

        /**
         * The notification subscription (null if the user isn't subscribed)
         */
        public SubscribableQueue<Notification>.Subscription subscription;

        public State(Socket socket) throws IOException {
            this.connection = new TaggedConnection(socket);
        }

        @Override
        public void close() throws IOException {
            connection.close();
            if (subscription != null)
                subscription.close();
        }
    }

    /**
     * Runs the interaction with the client
     */
    @Override
    public void run() {
        try (
            State state = new State(this.socket)
        ) {
            System.out.println("New client connected");

            Message curMessage = null;
            do {
                TaggedConnection.Frame f = state.connection.receive();
                curMessage = f.getMessage();
                if (curMessage != null) {
                    Message response = processMessage(f, state);
                    if (response != null)
                        state.connection.send(f.getTag(), response);
                }
            } while (curMessage != null);
        } catch(EOFException e) {
            System.out.println("Client disconnected");
        } catch(IOException e) {
            System.out.println("Client disconnected abruptly: " + e.getMessage());
        }
    }

    /**
     * Processes the incoming message, determining the appropriate response to it
     * @param frame the incoming message
     * @return the outgoing message
     */
    private Message processMessage(TaggedConnection.Frame frame, State state) {
        Message response = handlers.get(frame.getMessage().getClass()).processMessage(facade, frame, state);

        System.out.println("Received message: " + frame.getMessage().toString());
        if (response != null)
            System.out.println("Sending response: " + response.toString());

        return response;
    }
}
