package common;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Notification {

    public static Notification deserialize(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        List<Reward> aux = new ArrayList<>(size);

        while (size-- > 0)
            aux.add(Reward.deserialize(stream));

        return new Notification(aux);
    }

    public final Set<Reward> rewards;

    public Notification(Reward r) {
        this.rewards = new HashSet<>();
        this.rewards.add(r);
    }

    public Notification(Collection<Reward> rs) {
        this.rewards = rs.stream().collect(Collectors.toSet());
    }

    public Set<Reward> getRewards() {
        return this.rewards;
    }

    public void serialize(DataOutputStream stream) throws IOException {
        stream.writeInt(rewards.size());
        for (Reward r : rewards)
            r.serialize(stream);
    }
}
