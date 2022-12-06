package server;

import common.User;

public class ServerFacade {
    private final RewardCollection rewards;
    private final ScooterCollection scooters;
    private final UserCollection users;

    public ServerFacade() {
        rewards = new RewardCollection();
        scooters = new ScooterCollection();
        users = new UserCollection();
    }

    public boolean authenticate(String username, String password) {
        return users.loginUser(username, password) != null;
    }

    public void register(String username, String password) {
        try {
            users.registerUser(username, password);
        } catch(RuntimeException e) {
            //Silence exception. The user can't know that registration failed
            //because of a taken username, as to avoid user enumeration attacks
        }
    }


    public void generateRewards() {

    }
}
