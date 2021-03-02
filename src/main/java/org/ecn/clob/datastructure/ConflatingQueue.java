package org.ecn.clob.datastructure;

public interface ConflatingQueue<K, V> {

    /**
     * Adds a key value item to the queue.
     * <p>
     * If a @{{@link KeyValue} already exists in the queue with the same key, then the old @{{@link KeyValue} in the queue is updated
     * in place with the new keyValue. The order of the new keyValue within the queue should be the same as the old @{{@link KeyValue}.
     * <p>
     * If no @{{@link KeyValue} item exists in the queue with the same key, the keyValue is added to the end of the queue.
     *
     * @param keyValue the key value item to add to the queue
     * @return true if the key value item was successfully added to the queue, false otherwise
     * @throws NullPointerException if keyValue is null
     */
    boolean offer(KeyValue<K, V> keyValue);

    /**
     * Removes the first key value item in the queue, blocking if the queue is empty.
     *
     * @return the first key value item in the queue
     * @throws InterruptedException if the thread was interrupted while waiting for a key value item to be added to the queue
     */
    KeyValue<K, V> take() throws InterruptedException;

    /**
     * Checks whether the queue is currently empty
     *
     * @return true if the queue is currently empty, false otherwise
     */
    boolean isEmpty();

}