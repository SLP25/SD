package server;

import common.Reward;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RewardCollection extends LockableCollection {
    private Set<Reward> rewards;

    public RewardCollection() {
        this.rewards = new HashSet<>();
    }

    public void replaceAll(Collection<Reward> rs) {
        this.rewards = new HashSet<>();
        for(Reward r : rs) {
            this.rewards.add(new Reward(r));
        }
    }

    public int size() {
        return rewards.size();
    }

    public Collection<Reward> getRewards() {
        Collection<Reward> result = new HashSet<>();

        for(Reward r : rewards) {
            result.add(r);
        }


        return result;
    }
}
