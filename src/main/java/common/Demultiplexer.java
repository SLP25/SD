package common;

import common.messages.Message;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO:: Cleanup unused buffers
/**
 * A demultiplexer to allow multiple threads to send tagged messages, and to receive messages with a specific tag.
 * Useful for distinguishing messages from notifications and regular requests / responses
 *
 * @see TaggedConnection
 */
public class Demultiplexer implements AutoCloseable {
    /**
     * All conditions indexed by tag. Used to signal threads blocked waiting for a message
     * with the given tag
     */
    private Map<Integer, Condition> conditions;

    /**
     * All the message buffers indexed by tag. When a new message is received, it is put in this buffer
     * under the appropriate tag. When a thread requests a message with the given tag, simply get the first
     * message in the queue, or wait for a new one if it is empty
     */
    private Map<Integer, Queue<Message>> messageBuffers;
    /**
     * The lock to synchronize the buffers
     */
    private Lock l;

    /**
     * The tagged connection to construct the demultiplexer around
     */
    private TaggedConnection conn;

    /**
     * An exception. Used to store any exception that may occur during sending/receiving from
     * the connection, and to propagate it to all blocked threads waiting for data
     */
    private IOException exception;

    /**
     * Parameterized constructor
     * @param connection the tagged connection to construct the demultiplexer around
     */
    public Demultiplexer (TaggedConnection connection) {
        conditions = new HashMap<>();
        messageBuffers = new HashMap<>();
        l = new ReentrantLock();
        conn = connection;
        exception = null;
    }

    /**
     * Starts the demultiplexer
     * @throws IOException if something goes wrong during execution
     */
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

    /**
     * Send the given message with the given tag
     * @param tag the tag
     * @param message the message
     * @throws IOException if sending the message failed
     */
    public void send(int tag, Message message) throws IOException{
        this.conn.send(tag, message);
    }

    /**
     * Receives a message with the given tag
     * @param tag the tag of the target message
     * @return the received message
     * @throws IOException if receiving data from the connection failed
     * @throws InterruptedException if the thread is interrupted
     */
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

    /**
     * Closes the underlying connection of the demultiplexer
     * @throws IOException if closing the connection failed
     */
    public void close() throws IOException {
        this.conn.close();
    }
}
