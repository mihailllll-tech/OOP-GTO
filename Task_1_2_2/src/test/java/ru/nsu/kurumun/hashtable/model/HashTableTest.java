package ru.nsu.kurumun.hashtable.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.nsu.kurumun.hashtable.model.HashTable.Entry;

class HashTableTest {
    @Test
    void startsEmptyAndReportsMissingKeys() {
        HashTable<String, Integer> table = new HashTable<>();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
        assertNull(table.get("missing"));
        assertFalse(table.containsKey("missing"));
    }

    @Test
    void replacesValueWithoutAddingAnotherRecord() {
        HashTable<String, Number> table = new HashTable<>();

        assertNull(table.put("one", 1));
        assertEquals(1, table.put("one", 2));
        assertEquals(2, table.update("one", 1.0));

        assertEquals(1.0, table.get("one"));
        assertEquals(1, table.size());
        assertFalse(table.isEmpty());
    }

    @Test
    void distinguishesNullValueFromMissingKey() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put(null, null);

        assertTrue(table.containsKey(null));
        assertNull(table.get(null));
        assertNull(table.get("missing"));
        assertFalse(table.containsKey("missing"));

        assertNull(table.put(null, 7));
        assertEquals(7, table.get(null));
        assertEquals(1, table.size());
    }

    @Test
    void removesCollidingKeysWithoutLosingOtherRecords() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("AaAa", 1);
        table.put("BBBB", 2);
        table.put("AaBB", 3);

        assertFalse(table.remove("BBBB", 100));
        assertEquals(3, table.size());
        assertTrue(table.remove("BBBB", 2));
        assertEquals(1, table.get("AaAa"));
        assertEquals(3, table.get("AaBB"));
        assertTrue(table.remove("AaBB", 3));
        assertTrue(table.remove("AaAa", 1));

        assertTrue(table.isEmpty());
        assertFalse(table.containsKey("BBBB"));
    }

    @Test
    void preservesAllRecordsAfterGrowingAndRemovingSomeKeys() {
        HashTable<Integer, String> table = new HashTable<>();
        for (int number = -40; number < 40; number++) {
            table.put(number, "value" + number);
        }

        assertEquals(80, table.size());
        for (int number = -40; number < 40; number++) {
            assertEquals("value" + number, table.get(number));
        }
        for (int number = -40; number < 0; number++) {
            assertTrue(table.remove(number, "value" + number));
        }

        assertEquals(40, table.size());
        for (int number = 0; number < 40; number++) {
            assertEquals("value" + number, table.get(number));
        }
    }

    @Test
    void iteratesOverEachPairOnceWithIndependentPositions() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("first", 5);
        table.put("second", 6);
        Iterator<Entry<String, Integer>> first = table.iterator();
        Iterator<Entry<String, Integer>> second = table.iterator();

        assertSame(first.next(), second.next());
        Set<String> keys = new HashSet<>();
        for (Entry<String, Integer> entry : table) {
            assertTrue(keys.add(entry.getKey()));
            assertEquals(entry.getKey().length(), entry.getValue());
        }

        assertEquals(Set.of("first", "second"), keys);
    }

    @Test
    void detectsStructuralModificationDuringIteration() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("first", 1);
        Iterator<Entry<String, Integer>> iterator = table.iterator();

        table.put("second", 2);

        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void keepsIteratorValidWhenOnlyValueChanges() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("key", 1);
        Iterator<Entry<String, Integer>> iterator = table.iterator();

        table.put("key", 2);
        table.update("key", 3);

        assertTrue(iterator.hasNext());
        Entry<String, Integer> entry = iterator.next();
        assertEquals("key", entry.getKey());
        assertEquals(3, entry.getValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    void comparesContentsRegardlessOfInsertionOrder() {
        HashTable<String, Integer> first = new HashTable<>();
        first.put("Aa", 10);
        first.put("BB", 20);
        first.put(null, null);
        HashTable<String, Integer> second = new HashTable<>();
        second.put(null, null);
        second.put("BB", 20);
        second.put("Aa", 10);

        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals("not a table"));
        second.put("extra", 30);
        assertFalse(first.equals(second));
    }
}
