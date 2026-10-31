package ru.nsu.kurumun.hashtable.model;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class HashTable<K, V> implements Iterable<Entry<K, V>> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAX_CAPACITY = 1 << 30;

    private List<Entry<K, V>> buckets;
    private int size;
    private int modCount;

    public HashTable() {
        buckets = createBuckets(DEFAULT_CAPACITY);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public V put(K key, V value) {
        Entry<K, V> entry = findEntry(key);
        if (entry != null) {
            V previousValue = entry.getValue();
            entry.setValue(value);
            return previousValue;
        }

        int hash = hash(key);
        int index = indexFor(hash, buckets.size());
        buckets.set(index, new Entry<>(hash, key, value, buckets.get(index)));
        size++;
        modCount++;

        if (size > buckets.size() * 0.75 && buckets.size() < MAX_CAPACITY) {
            int capacity = buckets.size() * 2;
            List<Entry<K, V>> newBuckets = createBuckets(capacity);
            for (Entry<K, V> head : buckets) {
                Entry<K, V> current = head;
                while (current != null) {
                    Entry<K, V> next = current.getNext();
                    int newIndex = indexFor(current.getHash(), capacity);
                    current.setNext(newBuckets.get(newIndex));
                    newBuckets.set(newIndex, current);
                    current = next;
                }
            }
            buckets = newBuckets;
        }
        return null;
    }

    public V get(K key) {
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.getValue();
    }

    public boolean containsKey(K key) {
        return findEntry(key) != null;
    }

    public V update(K key, V value) {
        Entry<K, V> entry = findEntry(key);
        if (entry == null) {
            throw new NoSuchElementException("Ключ не найден: " + key);
        }
        V previousValue = entry.getValue();
        entry.setValue(value);
        return previousValue;
    }

    public boolean remove(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.size());
        Entry<K, V> previous = null;
        Entry<K, V> current = buckets.get(index);

        while (current != null) {
            if (current.getHash() == hash && Objects.equals(current.getKey(), key)
                    && Objects.equals(current.getValue(), value)) {
                unlinkEntry(index, previous, current);
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }

    private void unlinkEntry(int index, Entry<K, V> previous, Entry<K, V> current) {
        if (previous == null) {
            buckets.set(index, current.getNext());
        } else {
            previous.setNext(current.getNext());
        }
        current.setNext(null);
        size--;
        modCount++;

    }

    private Entry<K, V> findEntry(Object key) {
        int hash = hash(key);
        Entry<K, V> current = buckets.get(indexFor(hash, buckets.size()));
        while (current != null) {
            if (current.getHash() == hash && Objects.equals(current.getKey(), key)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    private static int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int hash = key.hashCode();
        return hash ^ (hash >>> 16);
    }

    private static int indexFor(int hash, int capacity) {
        return (capacity - 1) & hash;
    }

    private List<Entry<K, V>> createBuckets(int capacity) {
        List<Entry<K, V>> result = new ArrayList<>(capacity);
        for (int index = 0; index < capacity; index++) {
            result.add(null);
        }
        return result;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new TableIterator();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof HashTable<?, ?> other) || size != other.size) {
            return false;
        }
        for (Entry<K, V> entry : this) {
            Entry<?, ?> otherEntry = other.findEntry(entry.getKey());
            if (otherEntry == null || !Objects.equals(entry.getValue(), otherEntry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Entry<K, V> entry : this) {
            result += entry.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        boolean first = true;
        for (Entry<K, V> entry : this) {
            if (!first) {
                result.append(", ");
            }
            result.append(entry);
            first = false;
        }
        return result.append('}').toString();
    }

    private class TableIterator implements Iterator<Entry<K, V>> {
        private final int expectedModCount = modCount;
        private int bucketIndex;
        private Entry<K, V> nextEntry;

        private TableIterator() {
            moveToNextBucket();
        }

        @Override
        public boolean hasNext() {
            checkForModification();
            return nextEntry != null;
        }

        @Override
        public Entry<K, V> next() {
            checkForModification();
            if (nextEntry == null) {
                throw new NoSuchElementException("В таблице больше нет элементов");
            }
            Entry<K, V> result = nextEntry;
            nextEntry = nextEntry.getNext();
            moveToNextBucket();
            return result;
        }

        private void moveToNextBucket() {
            while (nextEntry == null && bucketIndex < buckets.size()) {
                nextEntry = buckets.get(bucketIndex);
                bucketIndex++;
            }
        }

        private void checkForModification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException("Таблица изменена во время обхода");
            }
        }
    }
}
