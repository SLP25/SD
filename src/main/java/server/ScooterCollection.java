package server;

import common.Location;
import common.Scooter;
import common.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ScooterCollection {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Map<Location, TreeSet<Scooter>> scooters;

    public Scooter getClosestFreeScooter(Location l, int maxDistance) {
        lock.readLock().lock();
        try {
            Scooter sc = unsafeGetClosestScooter(l, maxDistance);

            if(sc == null) {
                return null;
            } else {
                return new Scooter(sc);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public Scooter reserveScooter(Location l, int maxDistance, User u) {
        lock.writeLock().lock();

        try {
            Scooter sc = unsafeGetClosestScooter(l, maxDistance);
            sc.reserve(u);
            return new Scooter(sc);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void freeScooter(int scooterId, Location newLocation) {
        lock.writeLock().lock();

        try {
            for(TreeSet<Scooter> sc : scooters.values()) {
                for(Scooter s : sc) {
                    if(s.getId() == scooterId) {
                        s.free(newLocation);
                        return;
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    public Set<Scooter> getFreeScootersWithinDistance(Location l, int distance) {

        Set<Scooter> ans = new TreeSet<>();

        lock.readLock().lock();
        try {
            for(Map.Entry<Location, TreeSet<Scooter>> entry : scooters.entrySet()) {
                int d = Location.distance(l, entry.getKey());
                if(d <= distance) {
                    ans.addAll(entry.getValue().stream().map(sc -> new Scooter(sc))
                            .filter(sc -> !sc.isReserved()).collect(Collectors.toSet()));
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        return ans;
    }

    private Scooter unsafeGetClosestScooter(Location l, int maxDistance) {
        Scooter ans = null;
        int minDistance = maxDistance + 1;

        for(Map.Entry<Location, TreeSet<Scooter>> entry : scooters.entrySet()) {
            int d = Location.distance(l, entry.getKey());
            if(d < minDistance) {
                minDistance = d;
                List<Scooter> available = entry.getValue().stream()
                        .filter(sc -> !sc.isReserved()).collect(Collectors.toList());

                if(!available.isEmpty())
                    ans = available.get(0);
            }
        }

        return ans;
    }
}
