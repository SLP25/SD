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
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        CommandLine commandLine = new CommandLine();
        IClient client = new Client();
        commandLine.shell(IClient.class, client);
    }
}
