package server.messageHandling;

import common.User;
import common.messages.Message;
import server.ServerFacade;

import java.util.function.Consumer;

@FunctionalInterface
public interface IMessageHandler {
    Message processMessage(ServerFacade facade, Message message, Consumer<User> user);
}
