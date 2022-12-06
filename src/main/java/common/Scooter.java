package common;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Scooter {

    private int id;
    private Location location;
    private User user;
    private Lock lock;
    public Scooter(int id, Location location) {
        this.id = id;
        this.location = location;
        this.lock = new ReentrantLock();
        this.user = null;
    }

    public Scooter(Scooter scooter) {
        this.id = scooter.getId();
        this.location = scooter.getLocation();
        this.user = scooter.getUser();
        this.lock = new ReentrantLock();
    }

    public int getId() {
        lock.lock();
        try {
            return id;
        } finally {
            lock.unlock();
        }
    }

    private User getUser() {
        return user;
    }

    public Location getLocation() {
        lock.lock();
        try {
            return location;
        } finally {
            lock.unlock();
        }
    }

    public boolean isReserved() {
        lock.lock();
        try {
            return user != null;
        } finally {
            lock.unlock();
        }
    }

    public void reserve(User user) {
        lock.lock();
        try {
            this.user = user;
        } finally {
            lock.unlock();
        }
    }
    public void free(Location newLocation) {
        lock.lock();
        try {
            this.location = newLocation;
            this.user = null;
        } finally {
            lock.unlock();
        }

    }

    public static Scooter deserialize(ObjectInputStream stream) {
        //TODO
        return null;//new Scooter();
    }

    public void serialize(ObjectOutputStream stream) {
        //TODO
    }
}
