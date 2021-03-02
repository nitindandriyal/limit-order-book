package org.ecn.clob.datastructure;

public interface KeyValue<K, V> {

    /**
     * Returns the key
     *
     * @return the key
     */
    K getKey();

    /**
     * Returns the value
     *
     * @return the value
     */
    V getValue();

}
