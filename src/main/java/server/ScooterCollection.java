package server;

import common.Location;
import common.Scooter;
import common.User;
import sun.misc.Lock;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class ScooterCollection extends LockableCollection {

    private final Map<Location, TreeSet<Scooter>> freeScooters;
    private final Map<Integer, Scooter> scooters;

    public ScooterCollection(int gridSize, int numberScooters) {
        freeScooters = new TreeMap<>();
        scooters = new TreeMap<>();

        seedScooters(gridSize, numberScooters);
    }

    public void addScooter(Scooter sc) {
        scooters.put(sc.getId(), sc);
    }

    public Scooter getScooter(int id) {
        return scooters.get(id);
    }

    public Collection<Scooter> getScooters() {
        return scooters.values();
    }

    public int getNumberScooters() {
        return scooters.size();
    }

    private void seedScooters(int gridSize, int numberScooters) {
        Random r = new Random();
        for(int i = 0; i < numberScooters; i++) {
            int x = r.nextInt(gridSize);
            int y = r.nextInt(gridSize);
            Location l = new Location(x, y);
            Scooter sc = new Scooter(i, l);
            addScooter(sc);
        }
    }
}
