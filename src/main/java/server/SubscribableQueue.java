package server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.*;

/**
 * A queue which allows for several threads to subscribe and read independently
 * The queue itself is implemented as a linked list
 *
 * @param <T> The type of the elements of the queue
 */
public class SubscribableQueue<T> {

    /**
     * A node of the queue
     */
    private class QueueElem {
        /**
         * The element in the node
         */
        private final T elem;

        /**
         * The next element in the queue
         * If null, this node is the head of the queue
         */
        private QueueElem next;

        /**
         * Creates a new node containing the specified element
         * By default, the created node is a head
         * @param elem
         */
        private QueueElem(T elem) {
            this.elem = elem;
        }
    }

    /**
     * The lock of the queue. All changes to the structure of the queue are done with this lock acquired
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Condition used to wake up all awaiting threads when a new element is pushed into the queue
     */
    private final Condition cond = lock.newCondition();

    /**
     * The head of the queue. Is initialized with a dummy node.
     * Any subscription to the queue starts receiving elements after the head at the time of the subscription
     */
    private QueueElem head = new QueueElem(null);

    /**
     * Represents a subscription to the queue
     */
    public class Subscription implements Iterable<T>, AutoCloseable {
        /**
         * The node of the queue currently being read by the subscriber
         */
        private QueueElem iterator;

        /**
         * Creates a new subscription
         */
        private Subscription() {
            lock.lock();
            try {
                iterator = head;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Closes the subscription. This releases the iterator held by the subscription,
         * allowing the garbage collector to catch unused nodes of the queue
         * TODO: signal only this subscription instead of all
         */
        @Override
        public void close() {
            lock.lock();
            try {
                if (iterator != null) {
                    iterator = null;
                    cond.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * Asynchronously retrieves the next element of the queue.
         * If the subscription is cancelled, returns null instead.
         * @return The next element of the queue or null
         */
        private T getNext() {
            lock.lock();

            try {
                while (iterator != null && iterator.next == null)
                    cond.await();

                if (iterator == null)
                    return null;

                iterator = iterator.next;
                return iterator.elem;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

        /**
         * Returns an asynchronous iterator to the queue
         * @return An asynchronous iterator to the queue
         */
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                T current;
                boolean calculated;

                private T get() {
                    if (!calculated) {
                        current = getNext();
                        calculated = true;
                    }
                    return current;
                }

                @Override
                public boolean hasNext() {
                    return get() != null;
                }

                @Override
                public T next() {
                    T ans = get();
                    calculated = false;
                    return ans;
                }
            };
        }
    }

    /**
     * Returns a new subscription to the queue
     * @return A new subscription to the queue
     */
    public Subscription getSubscription() {
        return new Subscription();
    }

    /**
     * Pushes an element to the queue
     * @param elem an element
     */
    public void push(T elem) {
        if (elem == null)
            throw new IllegalArgumentException("The element cannot be null");

        QueueElem e = new QueueElem(elem);

        lock.lock();
        try {
            head.next = e;
            head = e;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Pushes all elements to the queue, ordered.
     * The last element ends up as the head of the queue
     * @param elems the elements
     */
    public void pushAll(Iterable<T> elems) {
        QueueElem first = null;
        QueueElem last = null;

        for (T elem : elems) {
            if (elem == null)
                throw new IllegalArgumentException("The elements cannot be null");

            QueueElem e = new QueueElem(elem);

            if (first == null)
                first = e;

            if (last != null)
                last.next = e;

            last = e;
        }

        lock.lock();
        try {
            head.next = first;
            head = last;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
