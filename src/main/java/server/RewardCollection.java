package server;

import common.Notification;
import common.Reward;

import common.Location;
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
    private final Set<Reward> rewards;
    private final SubscribableQueue<Notification> queue;

    /**
     * Default constructor
     */
    public RewardCollection() {
        this.rewards = new HashSet<>();
        this.queue = new SubscribableQueue<>();
    }

    /**
     * Adds the reward to the collection
     * @param r the reward
     */
    public void add(Reward r) {
        this.rewards.add(r);
        this.queue.push(new Notification(r));
    }

    public Reward isApplicable(Location start, Location end) {
        for(Reward r : rewards) {
            if(r.getStartLocation().equals(start) && r.getEndLocation().equals(end)) {
                return r;
            }
        }
        return null;
    }

    public void remove(Reward r) {
        this.rewards.remove(r);
    }

    /**
     * Adds all rewards to the collection
     * @param rs the rewards
     */
    public void addAll(Collection<Reward> rs) {
        if (rs.size() > 0) {
            this.rewards.addAll(rs);
            this.queue.push(new Notification(rs));
        }
    }

    /**
     * Replaces all rewards with the given ones
     * @param rs the new rewards
     */
    public void replaceAll(Collection<Reward> rs) {
        this.rewards.clear();
        this.addAll(rs);
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
        return new HashSet<>(rewards);
    }

    /**
     * Gets a subscription to the update queue
     * @return A subscription to the update queue
     */
    public SubscribableQueue<Notification>.Subscription getSubscription() {
        return this.queue.getSubscription();
    }
}
