package server;

import common.Reward;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The collection of all rewards currently active in the server
 */
public class RewardCollection extends LockableCollection {
    /**
     * All rewards currently active in the server
     */
    private Set<Reward> rewards;

    /**
     * Default constructor
     */
    public RewardCollection() {
        this.rewards = new HashSet<>();
    }

    /**
     * Replaces all rewards with the given ones
     * @param rs the new rewards
     */
    public void replaceAll(Collection<Reward> rs) {
        this.rewards = new HashSet<>();
        for(Reward r : rs) {
            this.rewards.add(r);
        }
    }

    /**
     * Gets the total number of rewards active in the server
     * @return the total number of rewards active in the server
     */
    public int size() {
        return rewards.size();
    }

    /**
     * Gets all rewards active in the server
     * @return all rewards active in the server
     */
    public Collection<Reward> getRewards() {
        Collection<Reward> result = new HashSet<>();

        for(Reward r : rewards) {
            result.add(r);
        }


        return result;
    }
}
