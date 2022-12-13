package server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An abstract class representing a collection which can be locked using
 * a read/write lock, allowing for thread safe accesses to it
 */
public abstract class LockableCollection {
    /**
     * The lock used
     */
    private ReadWriteLock lock;

    /**
     * Default constructor
     */
    public LockableCollection() {
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Gets the read lock of the collection
     * @return the read lock of the collection
     */
    public Lock readLock() {
        return lock.readLock();
    }

    /**
     * Gets the write lock of the collection
     * @return the write lock of the collection
     */
    public Lock writeLock() {
        return lock.writeLock();
    }
}
