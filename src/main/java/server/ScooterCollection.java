package server;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScooterCollection {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


}
