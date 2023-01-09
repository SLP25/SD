package server;

import common.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class holding the collection of users of the system
 */
public class UserCollection {
    /**
     * The lock for the collection
     */
    private ReadWriteLock lock;

    /**
     * A map of users by their username
     */
    private Map<String, User> users;

    /**
     * Default constructor.
     *
     * @implNote Seeds the collection with values from a text file
     */
    public UserCollection() {
        lock = new ReentrantReadWriteLock();
        users = new HashMap<>();

        seedData();
    }

    /**
     * Registers a new user in the system
     * @param username the username
     * @param password the password
     * @return the new user / null if registration failed (username taken)
     */
    public User registerUser(String username, String password) {
        User u = null;
        lock.writeLock().lock();

        if(!users.containsKey(username)) {
            u = new User(username, password);
            users.put(username, u);
        }

        lock.writeLock().unlock();
        return u;
    }

    /**
     * Verifies the login information of a user
     * @param username the username
     * @param password the password
     * @return the user who just logged in / null if log in failed
     */
    public User loginUser(String username, String password) {
        User ans = null;
        lock.readLock().lock();
        User u = users.get(username);

        if(u != null) {
            u.lock();
            lock.readLock().unlock();

            if(u.isPassword(password))
                ans = new User(u);

            u.unlock();
        } else {
            lock.readLock().unlock();
        }


        return ans;
    }

    /**
     * Seeds the collection with hardcoded accounts (to speed up testing and debugging)
     * Runs on startup
     */
    private void seedData() {
        registerUser("bace", "password1234");
        registerUser("vasques", "password1234");
        registerUser("felicio", "password1234");
        registerUser("luis", "password1234");
    }
}
