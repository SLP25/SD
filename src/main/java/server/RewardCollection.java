package server;

import common.Reward;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RewardCollection {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Set<Reward> rewards;

    public RewardCollection() {
        this.rewards = new HashSet<>();
    }

    public void addReward(Reward reward) {
        lock.writeLock().lock();

        try {
            rewards.add(reward);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeReward(Reward reward) {
        lock.writeLock().lock();

        try {
            rewards.remove(reward);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Collection<Reward> getRewards() {
        Collection<Reward> result = new HashSet<>();

        lock.readLock().lock();

        try {
            //This is thread safe because Reward is immutable
            for(Reward r : rewards) {
                result.add(r);
            }
        } finally {
            lock.readLock().unlock();
        }

        return result;
    }
}
