package common.messages;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Message {
    private static Map<Integer, Message> registereedSubClasses = new HashMap<>();
    public static Map<Class<? extends Message>,Integer> codes = new HashMap<>();

    public static void registerSubClass(int code, Message subclass) {
        if(registereedSubClasses.containsKey(code))
            throw new RuntimeException("Key already exists");

        if(codes.containsKey((subclass.getClass())))
            throw new RuntimeException("Class already registered");

        registereedSubClasses.put(code, subclass);
        codes.put(subclass.getClass(), code);
    }

    protected abstract void serializeMessage(ObjectOutputStream stream) throws IOException;

    protected abstract Message deserializeMessage(ObjectInputStream stream) throws IOException;

    public void serialize(ObjectOutputStream stream) throws IOException {
        stream.writeInt(codes.get(this.getClass()));
        this.serializeMessage(stream);
    }

    public static Message deserialize(ObjectInputStream stream) throws IOException {
        int code = stream.readInt();
        System.out.println(registereedSubClasses.size());
        if(!registereedSubClasses.containsKey(code))
            throw new IOException("No sub class with code " + code);

        return registereedSubClasses.get(code).deserializeMessage(stream);
    }
}
