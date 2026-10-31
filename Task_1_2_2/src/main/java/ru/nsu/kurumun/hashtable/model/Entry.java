package ru.nsu.kurumun.hashtable.model;

import java.util.Objects;

public final class Entry<K, V> {
    private final int hash;
    private final K key;
    private V value;
    private Entry<K, V> next;

    Entry(int hash, K key, V value, Entry<K, V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    int getHash() {
        return hash;
    }

    Entry<K, V> getNext() {
        return next;
    }

    void setValue(V value) {
        this.value = value;
    }

    void setNext(Entry<K, V> next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Entry<?, ?> entry)) {
            return false;
        }
        return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
