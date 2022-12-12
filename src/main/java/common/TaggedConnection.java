package common;

import common.messages.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    public static class Frame {
        private final int tag;
        private final Message message;

        public Frame(int tag, Message message) {
            this.tag = tag;
            this.message = message;
        }

        public int getTag() {
            return tag;
        }

        public Message getMessage() {
            return message;
        }
    }
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private ReentrantLock sendLock;
    private ReentrantLock receiveLock;

    public TaggedConnection(Socket socket) throws IOException {
        this.sendLock = new ReentrantLock();
        this.receiveLock = new ReentrantLock();
        this.socket = socket;
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

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
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}