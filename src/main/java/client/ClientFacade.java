package client;

import common.*;
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
    private Demultiplexer conn;

    public ClientFacade() throws IOException {
        Socket clientSocket = new Socket(ip, port);
        conn = new Demultiplexer(new TaggedConnection(clientSocket));
        conn.start();
    }

    public User authenticate(String username, String password) throws IOException, InterruptedException {
        LoginRequest request = new LoginRequest(username, password);

        conn.send(1, request);

        LoginResponse response = (LoginResponse)conn.receive(1);

        return response.getUser();
    }

    public User register(String username, String password) throws IOException, InterruptedException {
        RegistrationRequest request = new RegistrationRequest(username, password);

        conn.send(1, request);

        RegistrationResponse response = (RegistrationResponse)conn.receive(1);
        return response.getUser();
    }

    public Set<Scooter> getFreeScootersInDistance(Location location, int maxDistance)
            throws IOException, InterruptedException {
        FreeScootersWithinDistanceRequest request = new FreeScootersWithinDistanceRequest(location, maxDistance);

        conn.send(1, request);

        FreeScootersWithinDistanceResponse response = (FreeScootersWithinDistanceResponse)conn.receive(1);

        return response.getScooters();
    }

    //TODO:: Use reservation
    public Pair<Integer, Location> reserveScooter(Location location, int maxDistance)
            throws IOException, InterruptedException {
        ReserveScooterRequest request = new ReserveScooterRequest(location, maxDistance);

        conn.send(1, request);

        ReserveScooterResponse response = (ReserveScooterResponse)conn.receive(1);

        return new Pair<>(response.getReservationCode(), response.getLocation());
    }

    public int endReservation(int id, Location location) throws IOException, InterruptedException {
        EndReservationRequest request = new EndReservationRequest(location, id);

        conn.send(1, request);

        EndReservationResponse response = (EndReservationResponse)conn.receive(1);

        return response.getCost();
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
