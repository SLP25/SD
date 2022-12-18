package server;

import common.Location;

import java.util.*;


public class ScooterCollection extends LockableCollection {

    private final Chunk[][] grid;
    private final int numberChunks;

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

    public Map<Location, Integer> getFreeScootersInRange(Location target) {
        TreeSet<Location> chunks = getChunksToLock(target);
        Map<Location, Integer> ans = new TreeMap<>();
        for(Location l : chunks) {
            ans.putAll(grid[l.getX()][l.getY()].getFreeScootersInRange(target));
        }
        return ans;
    }

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
        Location chunkIndex = getChunkIndex(sc);
        return grid[chunkIndex.getX()][chunkIndex.getY()].reserveScooter(target);
    }

    public void freeScooter(Location l) {
        Location chunkIndex = getChunkIndex(l);
        grid[chunkIndex.getX()][chunkIndex.getY()].freeScooter(l);
    }

    private Location getChunkIndex(Location l) {
        int x = l.getX() / (2 * ServerFacade.D);
        int y = l.getY() / (2 * ServerFacade.D);

        return new Location(x, y);
    }

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

    public void lockLocation(Location target, boolean write) {
        TreeSet<Location> chunks = getChunksToLock(target);

        for(Location l : chunks) {
            if(write)
                grid[l.getX()][l.getY()].writeLock().lock();
            else
                grid[l.getX()][l.getY()].readLock().lock();
        }
    }

    public void unlockLocation(Location target, boolean write) {
        TreeSet<Location> chunks = getChunksToLock(target);

        for(Location l : chunks) {
            if(write)
                grid[l.getX()][l.getY()].writeLock().unlock();
            else
                grid[l.getX()][l.getY()].readLock().unlock();
        }
    }

    public Map<Location, Integer> getAllScooters() {
        Map<Location, Integer> ans = new TreeMap<>();
        for(int i = 0 ; i < numberChunks; i++) {
            for(int j = 0; j < numberChunks; j++) {
                ans.putAll(grid[i][j].getAllScooters());
            }
        }

        return ans;
    }

    private TreeSet<Location> getChunksToLock(Location l) {
        Location chunkIndex = getChunkIndex(l);
        Location chunkCoordinates = grid[chunkIndex.getX()][chunkIndex.getY()].getLocationCoords(l);

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


    private void seedScooters(int numberScooters) {
        Random r = new Random();
        for(int i = 0; i < numberScooters; i++) {
            int x = r.nextInt(ServerFacade.N);
            int y = r.nextInt(ServerFacade.N);
            Location l = new Location(x, y);
            Location chunkIndex = getChunkIndex(l);
            grid[chunkIndex.getX()][chunkIndex.getY()].freeScooter(l);
        }
    }
}
