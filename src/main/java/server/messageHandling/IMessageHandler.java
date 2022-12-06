package server.messageHandling;

import common.messages.Message;
import server.ServerFacade;

@FunctionalInterface
public interface IMessageHandler {
    Message processMessage(ServerFacade facade, Message message);
}
