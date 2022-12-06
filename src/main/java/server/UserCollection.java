package server;

import common.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserCollection {
    private ReadWriteLock lock;

    private Map<String, User> users;

    public UserCollection() {
        lock = new ReentrantReadWriteLock();
        users = new HashMap<>();

        seedData();
    }

    //TODO: Throw exception
    public User registerUser(String username, String password) {
        lock.writeLock().lock();
        try {
            if(users.containsKey(username))
                throw new RuntimeException("Id taken");
            User u = new User(username, password);
            users.put(username, u);
            return u;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public User loginUser(String username, String password) {
        lock.readLock().lock();
        try {
            User u = users.get(username);

            if(u != null && u.isPassword(password))
                return new User(u);
            else
                return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    //TODO: Throw exception
    private void seedData() {
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

    private void processFileLine(String line) throws IOException {
        String[] split = line.split(":");

        if(split.length != 2) {
            throw new IOException("Invalid data");
        } else {
            registerUser(split[0], split[1]);
        }
    }
}
