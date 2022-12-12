package client;

import common.ClassLoader;
import common.Location;
import common.messages.*;
import common.*;
import javafx.scene.SubScene;

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

            FreeScootersWithinDistanceRequest fswdr = new FreeScootersWithinDistanceRequest(new Location(10, 10), 100);
            fswdr.serialize(out);
            out.flush();

            response = Message.deserialize(in);
            System.out.println("Resposta obtida");

            FreeScootersWithinDistanceResponse fs = (FreeScootersWithinDistanceResponse)response;
            System.out.println(fs.getScooters().size());
            for(Scooter s : fs.getScooters()) {
                System.out.println(s.toString());
            }

            ReserveScooterRequest rsr = new ReserveScooterRequest(new Location(10, 10), 50);
            rsr.serialize(out);
            out.flush();
            response = Message.deserialize(in);
            ReserveScooterResponse rsrr = (ReserveScooterResponse)response;

            System.out.println(rsrr.getReservationCode());
            System.out.println(rsrr.getLocation().toString());

            Thread.sleep(5000);
            EndReservationRequest err = new EndReservationRequest( new Location(0,0),rsrr.getReservationCode());
            err.serialize(out);
            out.flush();

            response = Message.deserialize(in);
            EndReservationResponse errr = (EndReservationResponse)response;
            System.out.println(errr.getCost());
        } catch(IOException e) {
            System.out.println(e.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
