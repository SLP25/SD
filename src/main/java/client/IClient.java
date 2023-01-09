package client;

import common.Location;
import common.User;
import utils.Pair;

import java.io.IOException;
import java.util.Map;

public interface IClient {

    User authenticate(String username, String password) throws IOException, InterruptedException;
    User register(String username, String password) throws IOException, InterruptedException;
    Map<Location, Integer> getFreeScootersInDistance(Integer x, Integer y) throws IOException, InterruptedException;
    Pair<Integer, Location> reserveScooter(Integer x, Integer y)
            throws IOException, InterruptedException;
    int endReservation(Integer id, Integer x, Integer y) throws IOException, InterruptedException;

    void startNotifications() throws IOException;
    void stopNotifications() throws IOException;
}
