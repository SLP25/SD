package client;

import common.Location;
import common.Scooter;
import common.User;
import common.messages.*;
import javafx.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

//TODO:: Refactor to common interface with server facade
public class ClientFacade implements AutoCloseable {
    private static final String ip = "127.0.0.1";
    private static final int port = 20023;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    public ClientFacade() throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public User authenticate(String username, String password) throws IOException {
        LoginRequest request = new LoginRequest(username, password);

        request.serialize(out);
        out.flush();

        LoginResponse response = (LoginResponse)Message.deserialize(in);

        return response.getUser();
    }

    public User register(String username, String password) throws IOException {
        RegistrationRequest request = new RegistrationRequest(username, password);

        request.serialize(out);
        out.flush();

        RegistrationResponse response = (RegistrationResponse)Message.deserialize(in);

        return response.getUser();
    }

    public Set<Scooter> getFreeScootersInDistance(Location location, int maxDistance) throws IOException {
        FreeScootersWithinDistanceRequest request = new FreeScootersWithinDistanceRequest(location, maxDistance);

        request.serialize(out);
        out.flush();

        FreeScootersWithinDistanceResponse response = (FreeScootersWithinDistanceResponse)Message.deserialize(in);

        return response.getScooters();
    }

    //TODO:: Use reservation
    public Pair<Integer, Location> reserveScooter(Location location, int maxDistance) throws IOException {
        ReserveScooterRequest request = new ReserveScooterRequest(location, maxDistance);

        request.serialize(out);
        out.flush();

        ReserveScooterResponse response = (ReserveScooterResponse) Message.deserialize(in);

        return new Pair<>(response.getReservationCode(), response.getLocation());
    }

    public int endReservation(int id, Location location) throws IOException {
        EndReservationRequest request = new EndReservationRequest(location, id);

        request.serialize(out);
        out.flush();

        EndReservationResponse response = (EndReservationResponse) Message.deserialize(in);

        return response.getCost();
    }

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        clientSocket.close();
    }
}
