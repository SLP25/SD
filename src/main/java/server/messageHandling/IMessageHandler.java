package server.messageHandling;

import common.TaggedConnection;
import common.User;
import common.messages.Message;
import server.ClientHandler;
import server.ServerFacade;

import java.util.function.Consumer;

/**
 * A functional interface for handling of incoming messages. Each class implementing this interface is
 * responsible for handling a single type of request. That class should register itself to the
 * {@link, server.ClientHandler} class, so the type of request can be processed.
 */
@FunctionalInterface
public interface IMessageHandler {
    /**
     * Method responsible for processing the request and computing the response of the server
     * @param facade the facade of the server
     * @param frame the incoming request
     * @param state the connection state
     * @return the appropriate response
     */
    Message processMessage(ServerFacade facade, TaggedConnection.Frame frame, ClientHandler.State state);
}
