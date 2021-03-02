package org.ecn.clob.datastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlockingConflatingQueue<K, V> implements ConflatingQueue<K, V> {

    private final Map<K, KeyValue<K, V>> queue = new LinkedHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Condition isNotEmpty = lock.writeLock().newCondition();

    public boolean offer(KeyValue<K, V> keyValue) {
        if (Objects.isNull(keyValue)) {
            throw new NullPointerException("Null keyValue not allowed");
        }

        try {
            lock.writeLock().lock();
            queue.put(keyValue.getKey(), keyValue);
            isNotEmpty.signalAll();
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public KeyValue<K, V> take() throws InterruptedException {
        try {
            lock.writeLock().lock();
            while (queue.isEmpty()) {
                isNotEmpty.await();
            }
            K keyOfFirst = queue.keySet().iterator().next();
            return queue.remove(keyOfFirst);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isEmpty() {
        try {
            lock.readLock().lock();
            return queue.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }
}