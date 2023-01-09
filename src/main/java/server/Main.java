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
        int n, d, scooters;
        try {
            n = Integer.valueOf(args[0]);
            d = Integer.valueOf(args[1]);
            scooters = Integer.valueOf(args[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid arguments. Expected 3 integers N, D and SCOOTERS. Terminating");
            return;
        }

        if(d == 0 || n % (2*d) != 0) {
            System.out.println("Invalid arguments: 2*D must be a divisor of N");
            return;
        }

        Server server = new Server(n, d, scooters);
        server.start(20023);
    }
}
