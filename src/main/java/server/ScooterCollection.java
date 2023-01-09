package server;

import common.Location;

import java.util.*;

/**
 * The collection of all scooters in the map
 *
 */
public class ScooterCollection extends LockableCollection {

    /**
     * The matrix of chunks the map is split in
     * @see Chunk
     */
    private final Chunk[][] grid;

    /**
     * The number of chunks (along an axis) the grid was split in
     *
     * @see Chunk
     */
    private final int numberChunks;

    /**
     * Parameterized constructor
     * @param numberScooters the number of scooters to seed the map with
     */
    public ScooterCollection(int numberScooters) {
        numberChunks = ServerFacade.N / (2 * ServerFacade.D);
        grid = new Chunk[numberChunks][numberChunks];

        for(int i = 0; i < numberChunks; i++) {
            for(int j = 0; j < numberChunks; j++) {
                grid[i][j] = new Chunk(new Location(i * 2 * ServerFacade.D, j * 2 * ServerFacade.D));
            }
        }

        seedScooters(numberScooters);
    }

    /**
     * Gets all free scooters in range of the given location
     * @param target the given location
     * @return all free scooters in range of the given location: a map of location and number
     * of scooters in said location
     */
    public Map<Location, Integer> getFreeScootersInRange(Location target) {
        TreeSet<Location> chunks = getChunksToLock(target);
        Map<Location, Integer> ans = new TreeMap<>();
        for(Location l : chunks) {
            ans.putAll(grid[l.getX()][l.getY()].getFreeScootersInRange(target));
        }
        return ans;
    }

    /**
     * Reserves the closest scooter in range of the given location
     * @param target the given location
     * @return the location of the reserved scooter (null if no scooter was reserved)
     */
    public Location reserveScooter(Location target) {
        int d = ServerFacade.D + 1;
        Location sc = null;
        TreeSet<Location> chunks = getChunksToLock(target);
        for(Location l : chunks) {
            Location x = grid[l.getX()][l.getY()].getClosestFreeScooter(target);
            //No scooter in range inside chunk
            if(x == null)
                continue;
            int dist = Location.distance(target, x);
            if(dist < d) {
                sc = x;
                d = dist;
            }
        }
        if(sc == null)
            return null;
        Location chunkIndex = getChunkIndex(sc);
        return grid[chunkIndex.getX()][chunkIndex.getY()].reserveScooter(target);
    }

    /**
     * Parks a scooter in the given location
     * @param l the given location
     */
    public void freeScooter(Location l) {
        Location chunkIndex = getChunkIndex(l);
        grid[chunkIndex.getX()][chunkIndex.getY()].freeScooter(l);
    }

    /**
     * Gets the index (x, y in the grid attribute) of the chunk containing the given location
     * @param l the given location
     * @return the index (x, y in the grid attribute) of the chunk containing the given location
     */
    private Location getChunkIndex(Location l) {
        int x = l.getX() / (2 * ServerFacade.D);
        int y = l.getY() / (2 * ServerFacade.D);

        return new Location(x, y);
    }

    /**
     * Locks every chunk in the grid
     * @param write whether to acquire a write lock instead of a read lock
     */
    public void lockEverything(boolean write) {
        for(int i = 0; i < numberChunks; i++) {
            for(int j = 0; j < numberChunks; j++) {
                if(write)
                    grid[i][j].writeLock().lock();
                else
                    grid[i][j].readLock().lock();
            }
        }
    }

    /**
     * Unlocks every chunk in the grid
     * @param write whether to free a write lock instead of a read lock
     */
    public void unlockEverything(boolean write) {
        for(int i = 0; i < numberChunks; i++) {
            for(int j = 0; j < numberChunks; j++) {
                if(write)
                    grid[i][j].writeLock().unlock();
                else
                    grid[i][j].readLock().unlock();
            }
        }
    }

    /**
     * Locks every chunk needed in order to safely search an area around the given location.
     *
     * @implNote order of locking is guaranteed to be from left to right, top down
     *
     * @param target the location to search around
     * @param write whether to acquire a write lock instead of a read lock
     */
    public void lockLocation(Location target, boolean write) {
        TreeSet<Location> chunks = getChunksToLock(target);

        for(Location l : chunks) {
            if(write)
                grid[l.getX()][l.getY()].writeLock().lock();
            else
                grid[l.getX()][l.getY()].readLock().lock();
        }
    }

    /**
     * Unlocks every chunk needed in order to safely search an area around the given location.
     *
     * @implNote order of unlocking is guaranteed to be from left to right, top down
     *
     * @param target the location to search around
     * @param write whether to acquire a write lock instead of a read lock
     */
    public void unlockLocation(Location target, boolean write) {
        TreeSet<Location> chunks = getChunksToLock(target);

        for(Location l : chunks) {
            if(write)
                grid[l.getX()][l.getY()].writeLock().unlock();
            else
                grid[l.getX()][l.getY()].readLock().unlock();
        }
    }

    /**
     * Gets all scooters in the map
     * @return all scooters in the map (location and the number of scooters per location)
     */
    public Map<Location, Integer> getAllScooters() {
        Map<Location, Integer> ans = new TreeMap<>();
        for(int i = 0 ; i < numberChunks; i++) {
            for(int j = 0; j < numberChunks; j++) {
                ans.putAll(grid[i][j].getAllScooters());
            }
        }

        return ans;
    }

    /**
     * Gets all chunks which need to be locked in order to search around a location safely
     * @param l the given location
     * @return the indices of all chunks which need to be locked in order to search around a location safely
     */
    private TreeSet<Location> getChunksToLock(Location l) {
        /*
         * Given that a chink is a 2 * D x 2 * D square, and searches are at maximum of D distance,
         * it will only be necessary to lock 4 chunks (forming a square) in order to transverse safely.
         *
         * The 4 chunks to lock depend on the position of the given location inside its chunk: there are 4 cases
         * (as 2 * D will always be an even number, there will never be a location exactly in the middle of a chunk).
         *
         * If, for example, the location is in the top left quadrant of the chunk. From there, the chunks to the right
         * and down of the current chunk are more than D cells away, meaning there is no need to lock them. Therefore,
         * we can only lock the chunk above, to the left,  diagonally left and up to the current chunk,
         * which should also be locked. The other 3 cases are analogous.
         *
         */
        Location chunkIndex = getChunkIndex(l);
        Location chunkCoordinates = grid[chunkIndex.getX()][chunkIndex.getY()].getLocationCoords(l);

        //TreeSet to guarantee order, so that no deadlocks occur
        //from acquiring locks in the wrong order
        TreeSet<Location> ans = new TreeSet<>();
        ans.add(chunkIndex);
        //Top-left corner
        if(chunkCoordinates.getX() < ServerFacade.D && chunkCoordinates.getY() < ServerFacade.D) {
            ans.add(new Location(l.getX() - 1, l.getY()));
            ans.add(new Location(l.getX() - 1, l.getY() - 1));
            ans.add(new Location(l.getX(), l.getY() - 1));
        }
        //Top-right corner
        else if(chunkCoordinates.getX() >= ServerFacade.D && chunkCoordinates.getY() < ServerFacade.D) {
            ans.add(new Location(l.getX() + 1, l.getY()));
            ans.add(new Location(l.getX() + 1, l.getY() - 1));
            ans.add(new Location(l.getX(), l.getY() - 1));
        }
        //Bottom-left corner
        else if(chunkCoordinates.getX() >= ServerFacade.D && chunkCoordinates.getY() < ServerFacade.D) {
            ans.add(new Location(l.getX() - 1, l.getY()));
            ans.add(new Location(l.getX() - 1, l.getY() + 1));
            ans.add(new Location(l.getX(), l.getY() + 1));
        }
        //Bottom-right corner
        else if(chunkCoordinates.getX() >= ServerFacade.D && chunkCoordinates.getY() >= ServerFacade.D) {
            ans.add(new Location(l.getX() + 1, l.getY()));
            ans.add(new Location(l.getX() + 1, l.getY() + 1));
            ans.add(new Location(l.getX(), l.getY() + 1));
        }

        //Remove invalid values (out of bounds)
        TreeSet<Location> temp = new TreeSet<>();
        for(Location x : ans) {
            if(x.getX() < numberChunks && x.getX() >= 0 && x.getY() < numberChunks && x.getY() >= 0 )
                temp.add(x);
        }
        return temp;
    }


    /**
     * Seeds the map with randomly placed scooters
     * @param numberScooters the number of scooters to seed the map with
     */
    private void seedScooters(int numberScooters) {
        Random r = new Random();
        for(int i = 0; i < numberScooters; i++) {
            /*int x = r.nextInt(ServerFacade.N);
            int y = r.nextInt(ServerFacade.N);
            Location l = new Location(x, y);
            Location chunkIndex = getChunkIndex(l);
            grid[chunkIndex.getX()][chunkIndex.getY()].freeScooter(l);*/

            grid[0][0].freeScooter(new Location(0,0));
            grid[0][0].freeScooter(new Location(0,0));
            grid[0][0].freeScooter(new Location(0,0));
            grid[1][1].freeScooter(new Location(6,6));
            grid[1][1].freeScooter(new Location(6,6));
            grid[1][1].freeScooter(new Location(6,6));
        }
    }
}
