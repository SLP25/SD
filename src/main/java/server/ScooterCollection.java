package server;

import common.Location;
import common.Scooter;
import common.User;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class ScooterCollection {

    private final ReadWriteLock lock;

    private final Map<Location, TreeSet<Scooter>> freeScooters;
    private final Map<Integer, Scooter> allScooters;

    public ScooterCollection(int gridSize, int numberScooters) {
        lock = new ReentrantReadWriteLock();
        freeScooters = new TreeMap<>();
        allScooters = new TreeMap<>();

        unsafeSeedScooters(gridSize, numberScooters);
    }

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
            sc.lock();
            try {
                removeFree(sc.getLocation(), sc);
                sc.reserve(u);
                return new Scooter(sc);
            } finally {
                sc.unlock();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void freeScooter(int scooterId, Location newLocation) { //TODO:: Change exceptions
        lock.writeLock().lock();
        try {
            if(!allScooters.containsKey(scooterId))
                throw new RuntimeException("No such scooter");

            if(freeScooters.containsKey(scooterId))
                throw new RuntimeException("Scooter is free");

            Scooter sc = allScooters.get(scooterId);
            sc.lock();
            try {
                sc.free(newLocation);
            } finally {
                sc.unlock();
            }
            insertFree(newLocation, sc);
        } finally {
            lock.writeLock().unlock();
        }
    }
    public Set<Scooter> getFreeScootersWithinDistance(Location l, int distance) {

        Set<Scooter> ans = new TreeSet<>();

        lock.readLock().lock();
        try {
            for(Map.Entry<Location, TreeSet<Scooter>> entry : freeScooters.entrySet()) {
                int d = Location.distance(l, entry.getKey());
                if(d <= distance) {
                    for(Scooter sc : entry.getValue()) {
                        sc.lock();
                    }

                    try {
                        ans.addAll(entry.getValue());
                    } finally {
                        for(Scooter sc : entry.getValue()) {
                            sc.unlock();
                        }
                    }
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
        Location closest = null;

        for(Map.Entry<Location, TreeSet<Scooter>> entry : freeScooters.entrySet()) {
            int d = Location.distance(l, entry.getKey());
            if(d < minDistance) {
                minDistance = d;
                closest = entry.getKey();
            }
        }

        if(closest != null) {
            return freeScooters.get(closest).stream().collect(Collectors.toList()).get(0);
        }
        return null;
    }

    private void unsafeSeedScooters(int gridSize, int numberScooters) {
        Random r = new Random();
        for(int i = 0; i < numberScooters; i++) {
            int x = r.nextInt(gridSize);
            int y = r.nextInt(gridSize);
            Location l = new Location(x, y);
            Scooter sc = new Scooter(i, l);
            insertFree(l, sc);
            allScooters.put(i, sc);
        }
    }

    private void insertFree(Location l, Scooter sc) {
        if(!freeScooters.containsKey(l))
            freeScooters.put(l, new TreeSet<>());

        freeScooters.get(l).add(sc);
    }

    private void removeFree(Location l, Scooter sc) {
        freeScooters.get(l).remove(sc);

        if(freeScooters.get(l).size() == 0)
            freeScooters.remove(l);
    }
}
