package ru.nsu.kurumun.hashtable.model;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Хеш-таблица с обработкой коллизий через цепочки записей.
 *
 * <p>Поддерживает {@code null} в качестве ключа и значения. Ключи должны
 * сохранять поля, используемые в {@code equals} и {@code hashCode}.
 * Порядок обхода записей не определён.
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public final class HashTable<K, V> implements Iterable<HashTable.Entry<K, V>> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAX_CAPACITY = 1 << 30;

    private List<Entry<K, V>> buckets;
    private int size;
    private int modCount;

    /**
     * Создаёт пустую таблицу с начальной вместимостью 16 корзин.
     */
    public HashTable() {
        buckets = createBuckets(DEFAULT_CAPACITY);
    }

    /**
     * Возвращает количество записей.
     *
     * @return число пар ключ–значение
     */
    public int size() {
        return size;
    }

    /**
     * Проверяет, отсутствуют ли записи.
     *
     * @return {@code true}, если таблица пуста
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Добавляет новую пару или заменяет значение существующего ключа.
     *
     * @param key ключ, допускается {@code null}
     * @param value значение, допускается {@code null}
     * @return прежнее значение или {@code null}, если ключ отсутствовал
     *         либо его значение было равно {@code null}
     */
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

    /**
     * Находит значение по ключу.
     *
     * @param key искомый ключ
     * @return значение или {@code null}, если ключ отсутствует либо хранит
     *         {@code null}; для различения используется {@link #containsKey(Object)}
     */
    public V get(K key) {
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.getValue();
    }

    /**
     * Проверяет наличие ключа независимо от связанного значения.
     *
     * @param key искомый ключ
     * @return {@code true}, если ключ присутствует
     */
    public boolean containsKey(K key) {
        return findEntry(key) != null;
    }

    /**
     * Заменяет значение существующей записи, сохраняя количество записей.
     *
     * @param key ключ изменяемой записи
     * @param value новое значение
     * @return прежнее значение
     * @throws NoSuchElementException если ключ отсутствует
     */
    public V update(K key, V value) {
        Entry<K, V> entry = findEntry(key);
        if (entry == null) {
            throw new NoSuchElementException("Ключ не найден: " + key);
        }
        V previousValue = entry.getValue();
        entry.setValue(value);
        return previousValue;
    }

    /**
     * Удаляет запись при совпадении ключа и значения через {@code equals}.
     * Вместимость хранилища после удаления не уменьшается.
     *
     * @param key ключ удаляемой записи
     * @param value ожидаемое значение
     * @return {@code true}, если запись удалена
     */
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

    /**
     * Исключает запись из цепочки и учитывает изменение структуры.
     *
     * @param index номер корзины
     * @param previous предыдущая запись или {@code null} для начала цепочки
     * @param current удаляемая запись
     */
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

    /**
     * Ищет запись в корзине, соответствующей хешу ключа.
     *
     * @param key искомый ключ
     * @return запись или {@code null}, если ключ отсутствует
     */
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

    /**
     * Смешивает биты хеш-кода ключа.
     *
     * @param key ключ
     * @return смешанный хеш или ноль для {@code null}
     */
    private static int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int hash = key.hashCode();
        return hash ^ (hash >>> 16);
    }

    /**
     * Вычисляет индекс для вместимости, равной степени двойки.
     *
     * @param hash хеш ключа
     * @param capacity число корзин
     * @return индекс корзины
     */
    private static int indexFor(int hash, int capacity) {
        return (capacity - 1) & hash;
    }

    /**
     * Создаёт список пустых корзин указанного размера.
     *
     * @param capacity число корзин
     * @return список, заполненный {@code null}
     */
    private List<Entry<K, V>> createBuckets(int capacity) {
        List<Entry<K, V>> result = new ArrayList<>(capacity);
        for (int index = 0; index < capacity; index++) {
            result.add(null);
        }
        return result;
    }

    /**
     * Создаёт независимый итератор по парам ключ–значение.
     *
     * <p>Добавление нового ключа или удаление записи делает существующие
     * итераторы недействительными: их методы обнаруживают изменение через
     * {@link ConcurrentModificationException}. Замена значения не меняет структуру.
     * Удаление через сам итератор не поддерживается.
     *
     * @return новый итератор
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new TableIterator();
    }

    /**
     * Сравнивает пары двух таблиц независимо от порядка и вместимости.
     *
     * @param object сравниваемый объект
     * @return {@code true}, если объект является таблицей с такими же парами
     */
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

    /**
     * Вычисляет сумму хеш-кодов записей независимо от порядка обхода.
     *
     * @return хеш-код содержимого таблицы
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (Entry<K, V> entry : this) {
            result += entry.hashCode();
        }
        return result;
    }

    /**
     * Возвращает пары в фигурных скобках, разделённые запятыми.
     *
     * @return строковое представление таблицы
     */
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

    /**
     * Запись таблицы с фиксированной ссылкой на ключ и изменяемым значением.
     *
     * <p>Записи создаются таблицей. Методы доступа возвращают исходные объекты,
     * а не их копии; обновление таблицы может изменить значение записи.
     *
     * @param <K> тип ключа
     * @param <V> тип значения
     */
    public static final class Entry<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Entry<K, V> next;

        private Entry(int hash, K key, V value, Entry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Возвращает ключ записи.
         *
         * @return ключ
         */
        public K getKey() {
            return key;
        }

        /**
         * Возвращает текущее значение записи.
         *
         * @return значение
         */
        public V getValue() {
            return value;
        }

        private int getHash() {
            return hash;
        }

        private Entry<K, V> getNext() {
            return next;
        }

        private void setValue(V value) {
            this.value = value;
        }

        private void setNext(Entry<K, V> next) {
            this.next = next;
        }

        /**
         * Сравнивает ключ и значение с другой записью.
         *
         * @param object сравниваемый объект
         * @return {@code true}, если ключи и значения равны
         */
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

        /**
         * Вычисляет хеш пары ключ–значение.
         *
         * @return результат побитового исключающего ИЛИ хешей ключа и значения
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        /**
         * Возвращает запись в формате {@code ключ=значение}.
         *
         * @return строковое представление записи
         */
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Обходит цепочки корзин и проверяет счётчик изменений таблицы.
     */
    private class TableIterator implements Iterator<Entry<K, V>> {
        private final int expectedModCount = modCount;
        private int bucketIndex;
        private Entry<K, V> nextEntry;

        private TableIterator() {
            moveToNextBucket();
        }

        /**
         * Проверяет наличие следующей записи.
         *
         * @return {@code true}, если остались записи
         * @throws ConcurrentModificationException если структура таблицы изменена
         */
        @Override
        public boolean hasNext() {
            checkForModification();
            return nextEntry != null;
        }

        /**
         * Возвращает следующую запись и продвигает итератор.
         *
         * @return следующая запись
         * @throws NoSuchElementException если записи закончились
         * @throws ConcurrentModificationException если структура таблицы изменена
         */
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
