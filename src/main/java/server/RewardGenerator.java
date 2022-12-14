package server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RewardGenerator implements Runnable {
    private final Lock lock;
    private final Condition condition;

    private boolean awake;

    private int distance;

    private final ServerFacade facade;
    public RewardGenerator(ServerFacade facade, int d) {
        this.awake = true;
        this.facade = facade;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        distance = d;
    }

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
