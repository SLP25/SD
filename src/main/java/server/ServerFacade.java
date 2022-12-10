package server;

import common.User;
import common.Scooter;
import common.Location;

import java.util.Set;

public class ServerFacade {
    private final RewardCollection rewards;
    private final ScooterCollection scooters;
    private final UserCollection users;

    public ServerFacade() {
        rewards = new RewardCollection();
        scooters = new ScooterCollection(20, 20);
        users = new UserCollection();
    }

    public User authenticate(String username, String password) {
        return users.loginUser(username, password);
    }

    public User register(String username, String password) {
        return users.registerUser(username, password);
    }

    public Set<Scooter> getFreeScootersInDistance(Location location, int maxDistance) {
        return scooters.getFreeScootersWithinDistance(location, maxDistance);
    }


    public void generateRewards() {

    }
}
