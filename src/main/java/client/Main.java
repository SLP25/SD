package client;

import common.messages.LoginRequest;
import common.messages.Message;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        //magia negra do felicio


        try (
                Socket clientSocket = new Socket("127.0.0.1", 20023);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            LoginRequest request = new LoginRequest("vasques", "password1234");
            request.serialize(out);
            out.flush();

            Message response = Message.deserialize(in);
            System.out.println("Resposta obtida");
            LoginRequest lr = (LoginRequest)response;
            System.out.println(lr.getUsername());
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }
}
