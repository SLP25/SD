package common;

import common.messages.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A tagged connection.
 *
 * A tagged connection is a connection where each message is preceded by an integer
 * (a tag). Useful for splitting messages into different categories (for example, notifications
 * from regular requests), and allows multiple threads to share a socket.
 */
public class TaggedConnection implements AutoCloseable {
    /**
     * A wrapper class for pairs of messages and tags
     */
    public static class Frame {
        /**
         * The tag of the message
         */
        private final int tag;
        /**
         * The message itself
         */
        private final Message message;

        /**
         * Parameterized constructor
         * @param tag the tag
         * @param message the message
         */
        public Frame(int tag, Message message) {
            this.tag = tag;
            this.message = message;
        }

        /**
         * Gets the tag
         * @return the tag
         */
        public int getTag() {
            return tag;
        }

        /**
         * Gets the message
         * @return the message
         */
        public Message getMessage() {
            return message;
        }
    }

    /**
     * The underlying socket
     */
    private Socket socket;

    /**
     * The underlying DataInputStream
     */
    private DataInputStream in;

    /**
     * The underlying DataOutputStream
     */
    private DataOutputStream out;

    /**
     * The lock to synchronize sending messages
     */
    private ReentrantLock sendLock;

    /**
     * The lock to synchronize receiving messages
     */
    private ReentrantLock receiveLock;

    /**
     * Parameterized constructor
     * @param socket the socket to base the connection around
     * @throws IOException if creating the connection failed
     */
    public TaggedConnection(Socket socket) throws IOException {
        this.sendLock = new ReentrantLock();
        this.receiveLock = new ReentrantLock();
        this.socket = socket;
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    /**
     * Send a message with the given tag
     *
     * @implNote Thread safe
     *
     * @param tag the given tag
     * @param message the message to send
     * @throws IOException if sending the message failed
     */
    public void send(int tag, Message message) throws IOException {
        sendLock.lock();
        try {
            out.writeInt(tag);
            message.serialize(out);
            out.flush();
        } finally {
            sendLock.unlock();
        }
    }

    /**
     * Receives a message
     *
     * @implNote Thread safe
     *
     * @return the received message + tag
     * @throws IOException if receiving the message failed
     */
    public Frame receive() throws IOException {
        receiveLock.lock();

        try {
            int tag = in.readInt();
            Message message = Message.deserialize(in);

            return new Frame(tag, message);
        } finally {
            receiveLock.unlock();
        }
    }

    /**
     * Closes the connection
     * @throws IOException if closing the connection failed
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}