package server;

import common.TaggedConnection;
import common.User;
import common.messages.Message;
import server.messageHandling.IMessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ServerFacade facade;

    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    private static final Map<Class<? extends Message>, IMessageHandler> handlers = new HashMap<>();

    public ClientHandler(ServerFacade facade, Socket clientSocket) throws IOException {
        this.facade = facade;
        this.socket = clientSocket;
        this.currentUser = null;
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

    private Message processMessage(Message message) {
        System.out.println(message);
        return handlers.get(message.getClass()).processMessage(facade, message, this.currentUser, this::setCurrentUser);
    }
}
