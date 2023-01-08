package server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class SubscribableQueue<T> {

    private class QueueElem {
        private final T elem;
        private QueueElem next;

        private QueueElem(T elem) {
            this.elem = elem;
        }
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Condition cond = lock.readLock().newCondition();
    private QueueElem head = new QueueElem(null);

    public class Subscription implements Iterable<T>, AutoCloseable {
        private boolean active = true;
        private QueueElem iterator;

        private Subscription() {
            lock.readLock().lock();
            try {
                iterator = head;
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public void close() {
            lock.readLock().lock();
            try {
                if (active) {
                    active = false;
                    cond.signalAll();
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        private T getNext() {
            lock.readLock().lock();

            try {
                while (active && iterator.next == null)
                    cond.await();

                if (!active)
                    return null;

                iterator = iterator.next;
                return iterator.elem;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.readLock().unlock();
            }
        }

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

    public Subscription getSubscription() {
        return new Subscription();
    }

    public void push(T elem) {
        QueueElem e = new QueueElem(elem);

        lock.writeLock().lock();
        try {
            head.next = e;
            head = e;
            cond.signalAll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void pushAll(Iterable<T> elems) {
        QueueElem first = null;
        QueueElem last = null;

        for (T elem : elems) {
            QueueElem e = new QueueElem(elem);

            if (first == null)
                first = e;

            if (last != null)
                last.next = e;

            last = e;
        }

        lock.writeLock().lock();
        try {
            head.next = first;
            head = last;
            cond.signalAll();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
