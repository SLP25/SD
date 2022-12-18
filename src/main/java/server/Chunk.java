package server;

import common.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A chunk in the map.
 *
 * A chunk in the map is 2 * D x 2 * D square region containing the information about the scooters
 * parked inside that region.
 *
 * This division was done as to improve concurrency in the server
 */
public class Chunk extends LockableCollection {

    /**
     * The coordinates of the top left corner of the chunk
     */
    private final Location topLeftCorner;

    /**
     * A map containing all scooters in the chunk. It contains all locations with scooters, alongside the number
     * of scooters in that given location
     */
    private final Map<Location, Integer> scooters;

    /**
     * Parameterized constructor
     * @param tl coordinates of the top left corner of the chunk
     */
    public Chunk(Location tl) {
        topLeftCorner = tl;
        scooters = new HashMap<>();
    }

    /**
     * Gets the coordinates of the given location relative to the current chunk
     *
     * For example, if the chunk has (10,10) as its top left corner, the location (12,13)
     * will have (2,3) as its location relative to the current chunk
     *
     * @param l the given location
     * @return the coordinates of the given location relative to the current chunk
     */
    public Location getLocationCoords(Location l) {
        int x = l.getX() - topLeftCorner.getX();
        int y = l.getY() - topLeftCorner.getY();

        return new Location(x, y);
    }

    /**
     * Gets the closest free scooter to a location in the current chunk
     * @param target the target location
     * @return the location of the closest scooter (or null if no scooter is found in range)
     */
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

    /**
     * Gets all scooters in the chunk
     * @return all scooters in the chunk (map of their location and number of scooters per location)
     */
    public Map<Location, Integer> getAllScooters() {
        return scooters;
    }

    /**
     * Gets all free scooters in range of target location inside current chunk
     * @param target the target location
     * @return a map containing all scooters in range. It contains all locations with scooters, alongside the number
     * of scooters in that given location
     */
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

    /**
     * Reserves the scooter closest to the target location
     * @param target the target location
     * @return the location of the reserved scooter (null if no scooter was found)
     */
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

    /**
     * Frees a scooter in the given location
     * @param l the given location
     */
    public void freeScooter(Location l) {
        if(!scooters.containsKey(l)) {
            scooters.put(l, 0);
        }

        int val = scooters.get(l);
        val++;

        scooters.put(l, val);
    }
}
