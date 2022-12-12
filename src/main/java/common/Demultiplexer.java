package common;

import common.messages.Message;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO:: Cleanup unused buffers
public class Demultiplexer implements AutoCloseable {
    private Map<Integer, Condition> conditions;
    private Map<Integer, Queue<Message>> messageBuffers;
    private Lock l;
    private TaggedConnection conn;
    private IOException exception;

    public Demultiplexer (TaggedConnection connection) {
        conditions = new HashMap<>();
        messageBuffers = new HashMap<>();
        l = new ReentrantLock();
        conn = connection;
        exception = null;
    }

    public void start() throws IOException {
        new Thread(() -> {
            try {
                while (true) {
                    TaggedConnection.Frame frame = this.conn.receive();

                    l.lock();
                    try {
                        int tag = frame.getTag();

                        //Replies to tag that don't exist are dropped
                        if(!messageBuffers.containsKey(tag)) {
                            continue;
                        }

                        Queue<Message> q = messageBuffers.get(tag);
                        q.add(frame.getMessage());
                        conditions.get(tag).signal();
                    } finally {
                        l.unlock();
                    }
                }
            } catch (IOException e) {
                try {
                    l.lock();

                    this.exception = e;
                    this.conditions.forEach((k,v) -> v.signalAll());
                } finally {
                    l.unlock();
                }

            }
        }).start();
    }

    public void send(int tag, Message message) throws IOException{
        this.conn.send(tag, message);
    }

    public Message receive(int tag) throws IOException, InterruptedException{
        l.lock();
        try {
            Condition c = conditions.get(tag);

            if(c == null) {
                c = l.newCondition();
                conditions.put(tag, c);
                messageBuffers.put(tag, new ArrayDeque<>());
            }

            Queue<Message> buffer = messageBuffers.get(tag);

            while (buffer.isEmpty() && this.exception == null) {
                c.await();
            }

            if (!buffer.isEmpty()) {
                return buffer.poll();
            }
            else {
                throw this.exception;
            }
        } finally {
            l.unlock();
        }
    }

    public void close() throws IOException {
        this.conn.close();
    }
}
