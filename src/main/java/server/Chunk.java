package server;

import common.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Chunk extends LockableCollection {

    private final Location topLeftCorner;
    private final Map<Location, Integer> scooters;
    public Chunk(Location tl) {
        topLeftCorner = tl;
        scooters = new HashMap<>();
    }

    public Location getLocationCoords(Location l) {
        int x = l.getX() - topLeftCorner.getX();
        int y = l.getY() - topLeftCorner.getY();

        return new Location(x, y);
    }

    public Location getClosestFreeScooter(Location target) {
        int maxDistance = ServerFacade.D + 1;
        Location ans = null;

        for(Location l : scooters.keySet()) {
            int d = Location.distance(l, target);
            if(d < maxDistance) {
                maxDistance = d;
                ans = l;
            }
        }

        return ans;
    }

    public Map<Location, Integer> getAllScooters() {
        return scooters;
    }

    public Map<Location, Integer> getFreeScootersInRange(Location target) {
        Map<Location, Integer> ans = new TreeMap<>();

        for(Map.Entry<Location, Integer> kv : scooters.entrySet()) {
            int d = Location.distance(target, kv.getKey());
            if(d <= ServerFacade.D) {
                ans.put(kv.getKey(), kv.getValue());
            }
        }

        return ans;
    }

    public Location reserveScooter(Location target) {
        Location l = getClosestFreeScooter(target);

        if(l != null) {
            int val = scooters.get(l);
            val--;
            if(val == 0) {
                scooters.remove(l);
            } else {
                scooters.put(l, val);
            }
        }
        return l;
    }

    public void freeScooter(Location l) {
        if(!scooters.containsKey(l)) {
            scooters.put(l, 0);
        }

        int val = scooters.get(l);
        val++;

        scooters.put(l, val);
    }
}
