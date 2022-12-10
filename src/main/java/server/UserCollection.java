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
     * Seeds the collection with data from a file.
     *
     * Runs on startup
     */
    //TODO: Throw exception
    private void seedData() {
        //I will propably be killed for this
        final String filePath = "/home/rui-oliveira02/Documents/Projetos/SD/data/users.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();

            while (line != null) {
                processFileLine(line);
                line = reader.readLine();
            }
            reader.close();
        } catch(IOException e) {
            System.out.println(e.getStackTrace().toString());
        }
    }

    /**
     * Auxiliary method of {@link, #seedData()}. Processes a single line of the file
     * containing the users.
     *
     * A line should have the format "username:password". Anything else will be deemed
     * invalid.
     *
     * @param line a line of the text file
     * @throws IOException if the line is not valid
     */
    private void processFileLine(String line) throws IOException {
        String[] split = line.split(":");

        if(split.length != 2) {
            throw new IOException("Invalid data");
        } else {
            registerUser(split[0], split[1]);
        }
    }
}
