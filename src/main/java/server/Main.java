package server;

import common.messages.Message;
import server.messageHandling.LoginRequestHandler;
import common.ClassLoader;

import java.io.IOException;
import java.util.Arrays;

/**
 * Main server entry point
 */
public class Main {
    /**
     * Load the needed classes.
     * @see Message
     */
    private static void loadClasses() {
        ClassLoader.loadClasses(Message.class.getPackage().getName(),
                Arrays.asList(new String[]{"Message", "Exception"}));
        ClassLoader.loadClasses(LoginRequestHandler.class.getPackage().getName(), Arrays.asList("IMessageHandler"));
    }

    /**
     * Main server entry point
     *
     * @params args Ignored
     */
    public static void main(String[] args) throws IOException {
        loadClasses();
        Server server = new Server();
        server.start(20023);
    }
}
