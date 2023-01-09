import client.Client;
import client.IClient;
import view.CommandLine;

import java.io.IOException;

/**
 * Main client entry point
 */
public class Main {
    /**
     * Main client entry point
     *
     * @params args Ignored
     */
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine();
        try {
            IClient client = new Client();
            commandLine.shell(IClient.class, client);
        } catch(IOException e) {
            System.out.println("Could not connect to server. Terminating");
        } catch(ClassNotFoundException e) {
            System.out.println("There was a problem loading the user interface. Terminating");
        }

    }
}
