package ru.nsu.kurumun.hashtable.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.kurumun.hashtable.model.HashTable.Entry;

class EntryTest {
    @Test
    void comparesBothKeyAndValueAndUsesMatchingHashCodes() {
        Entry<String, Integer> first = entry("key", 1);
        Entry<String, Integer> equal = entry(new String("key"), 1);

        assertEquals(first, equal);
        assertEquals(equal, first);
        assertEquals(first.hashCode(), equal.hashCode());
        assertFalse(first.equals(null));
        assertFalse(first.equals("key=1"));
        assertNotEquals(first, entry("other", 1));
        assertNotEquals(first, entry("key", 2));
        assertEquals("key=1", first.toString());
    }

    private Entry<String, Integer> entry(String key, Integer value) {
        HashTable<String, Integer> table = new HashTable<>();
        table.put(key, value);
        return table.iterator().next();
    }
}
