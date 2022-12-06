package common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class User {

    private String username;
    private String password;
    private Lock lock;

    public User(String username, String password) {
        this.lock = new ReentrantLock();
        this.username = username;
        this.password = password;
    }

    public User(User user) {
        this(user.getUsername(), user.getPassword());
    }

    public String getUsername() {
        lock.lock();

        try {
            return username;
        } finally {
            lock.unlock();
        }
    }

    public boolean isPassword(String attempt) {
        return password.equals(attempt);
    }

    private String getPassword() {
        lock.lock();

        try {
            return password;
        } finally {
            lock.unlock();
        }
    }
}
