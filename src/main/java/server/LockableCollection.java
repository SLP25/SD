package server;

import common.Lockable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class LockableCollection {
    private ReadWriteLock lock;

    public LockableCollection() {
        lock = new ReentrantReadWriteLock();
    }

    public Lock readLock() {
        return lock.readLock();
    }

    public Lock writeLock() {
        return lock.writeLock();
    }
}
