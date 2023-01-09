package client;

import client.exceptions.NotAuthenticatedException;
import common.*;
import common.messages.*;
import utils.Pair;

import java.io.IOException;
import java.util.Map;

import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

//TODO:: Refactor to common interface with server facade
/**
 * The client facade. Exposes all the supported functionality in the server
 */
public class Client implements IClient {
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
     * Default constructor
     * @throws IOException if creating the socket to the server failed
     */
    public Client() throws IOException {
        common.ClassLoader.loadClasses(Message.class.getPackage().getName(),
                Arrays.asList(new String[]{"Message", "Exception"}));
        Socket clientSocket = new Socket(ip, port);
        conn = new Demultiplexer(new TaggedConnection(clientSocket));
        conn.start();
    }

    /**
     * Gets all free scooters within a certain distance of the given location
     *
     * @return all free scooters within a certain distance of the given location
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public Map<Location, Integer> getFreeScootersInDistance(Integer x, Integer y)
            throws IOException, InterruptedException, NotAuthenticatedException {

        Location location = new Location(x, y);

        FreeScootersWithinDistanceRequest request = new FreeScootersWithinDistanceRequest(location);

        conn.send(1, request);

        Message msg = conn.receive(1);
        assertAuthenticated(msg);
        FreeScootersWithinDistanceResponse response = (FreeScootersWithinDistanceResponse)msg;

        return response.getScooters();
    }

    //TODO:: Use reservation
    /**
     * Reserves the scooter closest to the given location
     *
     * @implNote the reservation returned is a deep copy of the one stored in the facade
     *
     * @param x the x coordinate of the target location
     * @param y the y coordinate of the target location
     *
     * @return the reservation code and the location of the scooter
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public Pair<Integer, Location> reserveScooter(Integer x, Integer y)
            throws IOException, InterruptedException {
        Location location = new Location(x,y);
        ReserveScooterRequest request = new ReserveScooterRequest(location);

        conn.send(1, request);
        Message msg = conn.receive(1);
        assertAuthenticated(msg);
        ReserveScooterResponse response = (ReserveScooterResponse)msg;

        return new Pair<>(response.getReservationCode(), response.getLocation());
    }

    /**
     * Ends a reservation
     * @param id the id of the reservation
     * @param x the x coordinate to the location to park the scooter in
     * @param y the y coordinate to the location to park the scooter in
     * @return the price the user must pay for the reservation (-1 if ending the reservation failed)
     * @throws IOException if connecting with the server failed
     * @throws InterruptedException if the thread is interrupted
     */
    public int endReservation(Integer id, Integer x, Integer y) throws IOException, InterruptedException {
        Location location = new Location(x, y);

        EndReservationRequest request = new EndReservationRequest(location, id);

        conn.send(1, request);

        Message msg = conn.receive(1);
        assertAuthenticated(msg);
        EndReservationResponse response = (EndReservationResponse)msg;

        return response.getCost();
    }

    private void assertAuthenticated(Message msg) throws RuntimeException {
        if(msg instanceof NotAuthenticatedResponse)
            throw new NotAuthenticatedException("Not logged in");
    }

    /**
     * Closes the connection to the server
     * @throws Exception if closing the connection failed
     */
    public void close() throws Exception {
        conn.close();
    }
}
