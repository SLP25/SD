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

    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private QueueElem head = new QueueElem(null);

    public class Subscription implements Iterable<T>, AutoCloseable {
        private boolean active = true;
        private QueueElem iterator;

        private Subscription() {
            lock.lock();
            try {
                iterator = head;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void close() {
            lock.lock();
            try {
                if (active) {
                    active = false;
                    cond.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        private T getNext() {
            lock.lock();

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
                lock.unlock();
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

        lock.lock();
        try {
            head.next = e;
            head = e;
            cond.signalAll();
        } finally {
            lock.unlock();
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
