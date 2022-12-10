package server.messageHandling;

import common.User;
import common.messages.Message;
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
     * @param message the incoming request
     * @param user the current user
     * @param setUser a method used to set the user who made the request. Useful to set the current user on
     *                login requests
     * @return the appropriate response
     */
    Message processMessage(ServerFacade facade, Message message, User user, Consumer<User> setUser);
}
