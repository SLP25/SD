package server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class used for generating rewards
 *
 * Runs in its own thread
 */
public class RewardGenerator implements Runnable {
    /**
     * The lock for the generator
     */
    private final Lock lock;

    /**
     * The condition used to await for before generating rewards
     */
    private final Condition condition;

    /**
     * Whether the thread should be awake. This is needed to distinguish
     * between spurious wake-ups and signals
     */
    private boolean awake;

    /**
     * The distance
     */
    private int distance;

    /**
     * The server facade
     *
     * @see ServerFacade
     */
    private final ServerFacade facade;

    /**
     * Parameterized constructor
     * @param facade the server facade
     * @param d d
     */
    public RewardGenerator(ServerFacade facade, int d) {
        this.awake = true;
        this.facade = facade;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        distance = d;
    }

    /**
     * Triggers the reward generation thread to regenerate them
     */
    public void setAwake() {
        lock.lock();
        try {
            awake = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void run() {
        while(true) {
            lock.lock();

            try {
                while (!awake)
                    condition.await();

                facade.generateRewards(distance);

                awake = false;
            } catch(InterruptedException e) {
            }finally {
                lock.unlock();
            }
        }
    }
}
