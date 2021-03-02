package org.ecn.clob.datastructure;

public interface ConflatingQueue<K, V> {

    boolean offer(KeyValue<K, V> keyValue);

    KeyValue<K, V> take() throws InterruptedException;

    boolean isEmpty();

}