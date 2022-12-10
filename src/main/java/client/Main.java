package client;

import common.messages.LoginRequest;
import common.messages.Message;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        //magia negra do felicio


        try (
                Socket clientSocket = new Socket("127.0.0.1", 20023);
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
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
