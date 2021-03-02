package org.ecn.clob.datastructure;

import java.util.Objects;

public class SimpleKeyValue<K, V> implements KeyValue<K, V> {

    private final K key;
    private final V value;

    private SimpleKeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> SimpleKeyValue<K, V> of(K key, V value) {
        return new SimpleKeyValue<>(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        SimpleKeyValue that = (SimpleKeyValue) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
