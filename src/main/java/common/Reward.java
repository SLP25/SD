package common;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Reward {
    private final Location startLocation;
    private final Location endLocation;

    public Reward(Location startLocation, Location endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public static Reward deserialize(ObjectInputStream stream) {
        //TODO
        return null;//new Reward();
    }

    public void serialize(ObjectOutputStream stream) {
        //TODO
    }
}
