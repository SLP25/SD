package server;

import common.TaggedConnection;
import common.User;
import common.messages.Message;
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
     * The socket of the client connection
     */
    private final Socket socket;

    /**
     * The server facade. Exposes all functionality
     */
    private final ServerFacade facade;

    /**
     * The user logged in
     */
    private User currentUser;

    /**
     * Sets the user currently logged in
     * @param user then new user currently logged in
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

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
     * Parameterized constructor
     * @param facade the server facade
     * @param clientSocket the socket the client is connected in
     */
    public ClientHandler(ServerFacade facade, Socket clientSocket) {
        this.facade = facade;
        this.socket = clientSocket;
        this.currentUser = null;
    }

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
     * Runs the interaction with the client
     */
    @Override
    public void run() {
        try (
            TaggedConnection conn = new TaggedConnection(socket)
        ){
            Message curMessage = null;
            do {
                TaggedConnection.Frame f = conn.receive();
                curMessage = f.getMessage();
                if (curMessage != null) {
                    Message response = processMessage(curMessage);
                    conn.send(f.getTag(), response);
                }
            } while (curMessage != null);
        } catch(EOFException e) {
            return; //Client closed, terminate normally
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Processes the incoming message, determining the appropriate response to it
     * @param message the incoming message
     * @return the outgoing message
     */
    private Message processMessage(Message message) {
        System.out.println(message);
        return handlers.get(message.getClass()).processMessage(facade, message, this.currentUser, this::setCurrentUser);
    }
}
