package server;

import common.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * The server facade. Exposes all the supported functionality
 */
public class ServerFacade {
    /**
     * The size of the grid
     */
    public static int N;

    /**
     * The size of the grid
     */
    public static int D;

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

    /**
     * The method used to trigger the generation of new rewards
     */
    private Runnable runRewards;

    /**
     * Default constructor
     *
     * @implNote initializes all users and scooters with test data
     *
     * @param n the size of the grid
     * @param d the maximum distance in queries
     * @param scooterCount the number of scooters in the server
     */
    public ServerFacade(int n, int d, int scooterCount) {
        N = n;
        D = d;
        rewards = new RewardCollection();
        scooters = new ScooterCollection(scooterCount);
        reservations = new ReservationCollection();
        users = new UserCollection();
    }

    /**
     * Sets the method used to trigger reward generation
     * @param r the method used to trigger reward generation
     */
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
     * @return all free scooters within a certain distance of the given location
     */
    public Map<Location, Integer> getFreeScootersInDistance(Location location) {
        scooters.lockLocation(location, false);
        try  {
            Map<Location, Integer> ans = scooters.getFreeScootersInRange(location);
            return ans;
        } finally {
            scooters.unlockLocation(location, false);
        }
    }

    /**
     * Reserves the scooter closest to the given location
     *
     * @implNote the reservation returned is a deep copy of the one stored in the facade
     *
     * @param user the user who wants to reserve the scooter
     * @param location the location to center the search in
     * @return the reservation of the scooter
     */
    public Reservation reserveScooter(String user, Location location) {

        scooters.lockLocation(location, true);
        reservations.writeLock().lock();
        try  {
            Location l = scooters.reserveScooter(location);
            Reservation ans = new Reservation(reservations.getNumberReservations(),
                    user, l, LocalDateTime.now());
            reservations.addReservation(ans);
            return ans;
        } finally {
            scooters.unlockLocation(location, true);
            reservations.writeLock().unlock();
        }
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
                r.terminate(location);
                cost = r.getCost();

                scooters.lockLocation(location, true);
                try  {
                    scooters.freeScooter(location);
                } finally {
                    scooters.unlockLocation(location, true);
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
    public void generateRewards(int d) {
        //We use lists because having order will be useful in the end
        //when we randomly pick the index of the end location of a reward
        List<Location> emptyLocations = new ArrayList<>();
        List<Location> fullLocations = new ArrayList<>();

        scooters.lockEverything(false);
        rewards.writeLock().lock();
        try {
            Map<Location, Integer> sc = scooters.getAllScooters();
            for(Map.Entry<Location, Integer> kv: sc.entrySet()) {
                if(kv.getValue() > 1) {
                    fullLocations.add(kv.getKey());
                }
            }

            int n = ServerFacade.N;

            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(scooters.getFreeScootersInRange(new Location(i,j)) == null) {
                        emptyLocations.add(new Location(i,j));
                    }
                }
            }

            Set<Reward> r = generateRewards(emptyLocations, fullLocations);
            rewards.replaceAll(r);
            System.out.println("Generated " + rewards.size() + " rewards");
        } finally {
            scooters.unlockEverything(false);
            rewards.writeLock().unlock();
        }
    }

    /**
     * Generates the rewards given the list of empty and full locations
     *
     * Only one reward per full location will be generated, for performance reasons
     *
     * @param emptyLocations the locations where there are no scooters in range
     * @param fullLocations the locations where there are multiple scooters parked
     * @return the set of generated rewards
     */
    private Set<Reward> generateRewards(List<Location> emptyLocations, List<Location> fullLocations) {
        Set<Reward> ans = new HashSet<>();
        Random rnd = new Random();
        for(Location x : fullLocations) {
            int i = rnd.nextInt(emptyLocations.size());
            //Pick a random prize money for the reward in the range
            //[Reward.minimumPrize, Reward.maximumPrize]
            int money = rnd.nextInt(Reward.maximumPrize - Reward.minimumPrize) + Reward.minimumPrize;
            ans.add(new Reward(x, emptyLocations.get(i), money));

        }

        return ans;
    }
}
