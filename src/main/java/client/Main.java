package client;

import common.ClassLoader;
import common.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
        //magia negra do felicio

        ClassLoader.loadClasses(Message.class.getPackage().getName(),
                Arrays.asList(new String[]{"Message", "Exception"}));
        try (
                Socket clientSocket = new Socket("127.0.0.1", 20023);
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        ) {
            RegistrationRequest request = new RegistrationRequest("batata", "password1234");
            request.serialize(out);
            out.flush();

            Message response = Message.deserialize(in);
            System.out.println("Resposta obtida");
            RegistrationResponse lr = (RegistrationResponse)response;
            System.out.println(lr.getUser());
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }
}
