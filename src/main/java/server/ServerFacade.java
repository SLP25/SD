package server;

import common.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;

/**
 * The server facade. Exposes all the supported functionality
 */
public class ServerFacade {
    private static final int N = 20;
    /**
     * The collection of rewards
     */
    private final RewardCollection rewards;

    /**
     * The collection of scooters
     */
    private final ScooterCollection scooters;

    /**
     * The collection of reservations
     */
    private final ReservationCollection reservations;

    /**
     * The collection of users
     */
    private final UserCollection users;

    private Runnable runRewards;

    /**
     * Default constructor
     *
     * @implNote initializes all users and scooters with test data
     */
    public ServerFacade() {
        rewards = new RewardCollection();
        scooters = new ScooterCollection(N, 20);
        reservations = new ReservationCollection();
        users = new UserCollection();
    }

    public void setRunRewards(Runnable r) {
        this.runRewards = r;
    }

    /**
     * Logs a user in the system
     *
     * @implNote the user returned is a deep copy of the one stored in the facade
     *
     * @param username the username of the user to try to log in as
     * @param password the password attempt
     * @return the user who logged in. Is null if authentication failed
     */
    public User authenticate(String username, String password) {
        return users.loginUser(username, password);
    }

    /**
     * Registers a new user in the system
     *
     * @implNote the user returned is a deep copy of the one stored in the facade
     *
     * @param username the username to register as
     * @param password the password
     * @return the user who just registered. Is null if registration failed
     */
    public User register(String username, String password) {
        return users.registerUser(username, password);
    }

    /**
     * Gets all free scooters within a certain distance of the given location
     *
     * @param location the location to center the search around
     * @param maxDistance the maximum distance a scooter can be of the given location
     * @return all free scooters within a certain distance of the given location
     */
    public Set<Scooter> getFreeScootersInDistance(Location location, int maxDistance) {
        Set<Scooter> ans = new TreeSet<>();

        scooters.readLock().lock();
        for(Scooter s : scooters.getScooters())
            s.lock();

        scooters.readLock().unlock();

        for(Scooter s : scooters.getScooters()) {
            if(Location.distance(s.getLocation(), location) <= maxDistance)
                ans.add(new Scooter(s));

            s.unlock();
        }

        return ans;
    }

    /**
     * Reserves the scooter closest to the given location
     *
     * @implNote the reservation returned is a deep copy of the one stored in the facade
     *
     * @param user the user who wants to reserve the scooter
     * @param location the location to center the search in
     * @param maxDistance the maximum distance between the scooter and the given location
     * @return the reservation of the scooter
     */
    public Reservation reserveScooter(String user, Location location, int maxDistance) {

        scooters.readLock().lock();
        for(Scooter s : scooters.getScooters())
            s.lock();

        Scooter sc = null;
        int distance = maxDistance + 1;
        for(Scooter s : scooters.getScooters()) {
            int d = Location.distance(s.getLocation(), location);
            if(d < distance) {
                distance = d;
                sc = s;
            }
        }

        reservations.writeLock().lock();
        scooters.readLock().unlock();

        Reservation ans = new Reservation(reservations.getNumberReservations(),
                sc.getId(), user, sc.getLocation(), LocalDateTime.now());

        for(Scooter s : scooters.getScooters())
            s.unlock();

        reservations.addReservation(ans);

        reservations.writeLock().unlock();

        runRewards.run();

        return ans;
    }

    /**
     * Ends a reservation
     * @param user the user who wants to end the reservation
     * @param id the id of the reservation
     * @param location the location to park the scooter in
     * @return the price the user must pay for the reservation (-1 if ending the reservation failed)
     */
    public int endReservation(String user, int id, Location location) {
        int cost = -1;
        reservations.readLock().lock();
        Reservation r = reservations.getReservation(id);
        if(r != null) {
            r.lock();

            //Only the user who started the reservation can end it
            if(r.getUser().equals(user)) {
                scooters.readLock().lock();
                Scooter sc = scooters.getScooter(r.getScooterId());

                if(sc != null) {
                    sc.lock();
                    sc.free(location);
                    r.terminate(location);
                    cost = r.getCost();
                    sc.unlock();
                }
            }

            r.unlock();
        }


        reservations.readLock().unlock();

        runRewards.run();

        return cost;
    }

    /**
     * Generates all the rewards in the system
     *
     * @see Reward
     */
    //TODO:: Heavy optimization
    public void generateRewards(int d) {
        int[][] grid = new int[N][N];
        for(int i = 0; i < N; i++)
            for(int j = 0; j < N; j++)
                grid[i][j] = 0;

        Set<Location> emptyLocations = new TreeSet<>();
        Set<Location> fullLocations = new TreeSet<>();
        scooters.readLock().lock();
        rewards.writeLock().lock();
        for(Scooter sc : scooters.getScooters())
            sc.lock();

        for(Scooter sc : scooters.getScooters()) {
            Location l = sc.getLocation();
            grid[l.getX()][l.getY()]++;
        }

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(grid[i][j] > 1) {
                    fullLocations.add(new Location(i, j));
                } else if(grid[i][j] == 0) {
                    boolean empty = true;
                    for(int h = -d; h <= d; h++) {
                        for(int k = -(d - Math.abs(h)); k <= (d - Math.abs(h)); k++) {
                            if(grid[i][j] != 0)
                                empty = false;
                        }
                    }

                    if(empty) {
                        emptyLocations.add(new Location(i,j));
                    }
                }
            }
        }
        Set<Reward> r = generateRewards(emptyLocations, fullLocations);

        rewards.replaceAll(r);

        //Ordem de libertar locks
        for(Scooter sc : scooters.getScooters())
            sc.unlock();
        scooters.readLock().unlock();
        rewards.writeLock().unlock();

        System.out.println("Generated " + rewards.size() + " rewards");
    }

    private Set<Reward> generateRewards(Set<Location> emptyLocations, Set<Location> fullLocations) {
        Set<Reward> ans = new HashSet<>();

        for(Location x : emptyLocations) {
            for(Location y : fullLocations) {
                ans.add(new Reward(x, y));
            }
        }

        return ans;
    }
}
