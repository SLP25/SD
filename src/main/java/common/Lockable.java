package common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents an object that will be shared accross different
 * threads and is not immutable, meaning it must implement some
 * mutual exclusion mechanism.
 *
 * This is implemented in this superclass using a ReentrantLock, and
 * offers methods for locking and unlocking the object.
 *
 * None of the subclasses internal methods should call the aforementioned
 * ones. Instead, locking should be done by the class using the object.
 *
 */
public abstract class Lockable {
    /**
     * The lock object used to ensure mutual exclusion
     */
    private Lock l;

    /**
     * Default constructor
     */
    public Lockable() {
        l = new ReentrantLock();
    }

    /**
     * Locks the object. Every call to this method should have an accompanying call
     * to {@link, unlock(), unlock} method afterwards
     */
    public void lock() {
        l.lock();
    }

    /**
     * Unlocks the object. Every call to this method should have an accompanying call
     * to {@link, lock(), lock} method beforehand
     */
    public void unlock() {
        l.unlock();
    }
}
