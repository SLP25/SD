package client;

import common.*;
import common.messages.*;
import javafx.util.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

//TODO:: Refactor to common interface with server facade
/**
 * The client facade. Exposes all the supported functionality in the server
 */
public class ClientFacade implements AutoCloseable {
    /**
     * The ip address of the server
     */
    private static final String ip = "127.0.0.1";

    /**
     * The port the server is listening on
     */
    private static final int port = 20023;

    /**
     * The connection to the server
     *
     * @see Demultiplexer
     */
    private Demultiplexer conn;

    /**
     * Default constructor
     * @throws IOException if creating the socket to the server failed
     */
    public ClientFacade() throws IOException {
        Socket clientSocket = new Socket(ip, port);
        conn = new Demultiplexer(new TaggedConnection(clientSocket));
        conn.start();
    }

    /**
     * Logs a user in the system
     *
     * @param username the username of the user to try to log in as
     * @param password the password attempt
     * @return the user who logged in. Is null if authentication failed
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public User authenticate(String username, String password) throws IOException, InterruptedException {
        LoginRequest request = new LoginRequest(username, password);

        conn.send(1, request);

        LoginResponse response = (LoginResponse)conn.receive(1);

        return response.getUser();
    }

    /**
     * Registers a new user in the system
     *
     * @param username the username to register as
     * @param password the password
     * @return the user who just registered. Is null if registration failed     *
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public User register(String username, String password) throws IOException, InterruptedException {
        RegistrationRequest request = new RegistrationRequest(username, password);

        conn.send(1, request);

        RegistrationResponse response = (RegistrationResponse)conn.receive(1);
        return response.getUser();
    }

    /**
     * Gets all free scooters within a certain distance of the given location
     *
     * @param location the location to center the search around
     * @return all free scooters within a certain distance of the given location
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public Set<Scooter> getFreeScootersInDistance(Location location)
            throws IOException, InterruptedException {
        FreeScootersWithinDistanceRequest request = new FreeScootersWithinDistanceRequest(location);

        conn.send(1, request);

        FreeScootersWithinDistanceResponse response = (FreeScootersWithinDistanceResponse)conn.receive(1);

        return response.getScooters();
    }

    //TODO:: Use reservation
    /**
     * Reserves the scooter closest to the given location
     *
     * @implNote the reservation returned is a deep copy of the one stored in the facade
     *
     * @param location the location to center the search in
     * @return the reservation code and the location of the scooter
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public Pair<Integer, Location> reserveScooter(Location location)
            throws IOException, InterruptedException {
        ReserveScooterRequest request = new ReserveScooterRequest(location);

        conn.send(1, request);

        ReserveScooterResponse response = (ReserveScooterResponse)conn.receive(1);

        return new Pair<>(response.getReservationCode(), response.getLocation());
    }

    /**
     * Ends a reservation
     * @param id the id of the reservation
     * @param location the location to park the scooter in
     * @return the price the user must pay for the reservation (-1 if ending the reservation failed)
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public int endReservation(int id, Location location) throws IOException, InterruptedException {
        EndReservationRequest request = new EndReservationRequest(location, id);

        conn.send(1, request);

        EndReservationResponse response = (EndReservationResponse)conn.receive(1);

        return response.getCost();
    }

    /**
     * Closes the connection to the server
     * @throws Exception if closing the connection failed
     */
    @Override
    public void close() throws Exception {
        conn.close();
    }
}
