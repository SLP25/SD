package server;

import common.User;

public class ServerFacade {
    private final RewardCollection rewards;
    private final ScooterCollection scooters;
    private final UserCollection users;

    public ServerFacade() {
        rewards = new RewardCollection();
        scooters = new ScooterCollection(20, 20);
        users = new UserCollection();
    }

    public boolean authenticate(String username, String password) {
        return users.loginUser(username, password) != null;
    }

    public boolean register(String username, String password) {
        try {
            users.registerUser(username, password);
            return true;
        } catch(RuntimeException e) { //TODO:: Change exception
            return false;
        }
    }


    public void generateRewards() {

    }
}
