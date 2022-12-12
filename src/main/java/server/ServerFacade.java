package server;

import common.User;
import common.Scooter;
import common.Location;
import common.Reservation;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

public class ServerFacade {
    private final RewardCollection rewards;
    private final ScooterCollection scooters;

    private final ReservationCollection reservations;
    private final UserCollection users;

    public ServerFacade() {
        rewards = new RewardCollection();
        scooters = new ScooterCollection(20, 20);
        reservations = new ReservationCollection();
        users = new UserCollection();
    }

    public User authenticate(String username, String password) {
        return users.loginUser(username, password);
    }

    public User register(String username, String password) {
        return users.registerUser(username, password);
    }

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

        reservations.addReservation(ans);

        reservations.writeLock().unlock();

        for(Scooter s : scooters.getScooters())
            s.unlock();

        return ans;
    }

    public void generateRewards() {

    }
}
