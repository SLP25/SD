package server;

import common.User;
import common.messages.Message;
import server.messageHandling.IMessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ServerFacade facade;

    private static final Map<Class<? extends Message>, IMessageHandler> handlers = new HashMap<>();

    public ClientHandler(ServerFacade facade, Socket clientSocket) {
        this.facade = facade;
        this.clientSocket = clientSocket;
    }

    public static void registerHandler(Class<? extends Message> messageClass, IMessageHandler handler) {
        if(handlers.containsKey(messageClass)) {
            throw new RuntimeException("Already registered handler");
        }

        handlers.put(messageClass, handler);
    }

    @Override
    public void run() {

        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream((clientSocket.getInputStream()));
        ) {
            Message curMessage = null;
            do {
                System.out.println("Aqui");
                curMessage = Message.deserialize(in);
                if(curMessage != null) {
                    Message response = processMessage(curMessage);
                    response.serialize(out);
                    out.flush();
                }
            } while(curMessage != null);
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }

    private Message processMessage(Message message) {
        System.out.println(message.getClass());
        return handlers.get(message.getClass()).processMessage(facade, message);
    }
}
