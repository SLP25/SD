package common.messages;

import common.Reward;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class RewardsWithinDistanceResponse extends Message {
    //Register the class in the super class, as to allow for deserialization
    static {
        Message.registerSubClass(1673298394, new RewardsWithinDistanceResponse());
    }

    /**
     * All scooters in range (location and number of scooters per location)
     */
    private final Set<Reward> rewards;


    /**
     * Default constructor
     */
    public RewardsWithinDistanceResponse() {
        rewards = null;
    }

    /**
     * Parameterized constructor
     * @param rewards rewards in range
     */
    public RewardsWithinDistanceResponse(Set<Reward> rewards) {
        this.rewards = new HashSet<>(rewards);
    }

    /**
     * Gets the rewards in range
     * @return All rewards in range (location and number of scooters per location)
     */
    public Set<Reward> getRewards() {
        return new HashSet<>(this.rewards);
    }

    /**
     * Serializes the object into a DataOutputStream
     *
     * @implNote The stream is not flushed after writing to it
     *
     * @param out the given DataOutputStream
     * @throws IOException if writing to the stream failed
     */
    @Override
    protected void serializeMessage(DataOutputStream out) throws IOException {
        out.writeInt(rewards.size());

        for (Reward r : this.rewards)
            r.serialize(out);
    }

    /**
     * Deserializes an object from a DataInputStream
     *
     * @param in the given DataInputStream
     * @throws IOException if reading from the stream failed
     */
    @Override
    protected RewardsWithinDistanceResponse deserializeMessage(DataInputStream in) throws IOException {
        int count = in.readInt();
        Set<Reward> ans = new HashSet<>();

        while (count-- > 0)
            ans.add(Reward.deserialize(in));

        return new RewardsWithinDistanceResponse(ans);
    }

    @Override
    public String toString() {
        return String.format("RewardsWithinDistanceResponse (%d rewards)", this.rewards.size());
    }
}
