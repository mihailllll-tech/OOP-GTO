package ru.nsu.kurumun.hashtable;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import ru.nsu.kurumun.hashtable.model.Entry;
import ru.nsu.kurumun.hashtable.model.HashTable;

public class Main {
    public static void main(String[] args) {
        HashTable<String, Number> table = new HashTable<>();
        table.put("one", 1);
        table.update("one", 1.0);
        System.out.println(table.get("one"));

        table.put("two", 2);
        table.put("Aa", 10);
        table.put("BB", 20);
        table.put(null, 30);
        table.put("empty", null);

        System.out.println("Таблица: " + table);
        System.out.println("Количество записей: " + table.size());
        System.out.println("Ключ one есть: " + table.containsKey("one"));
        System.out.println("Ключ missing есть: " + table.containsKey("missing"));
        System.out.println("Коллизия Aa и BB: " + table.get("Aa") + ", " + table.get("BB"));
        System.out.println("Ключ с пустым значением есть: " + table.containsKey("empty"));

        System.out.println("Обход пар:");
        for (Entry<String, Number> entry : table) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        HashTable<String, Number> copy = new HashTable<>();
        for (Entry<String, Number> entry : table) {
            copy.put(entry.getKey(), entry.getValue());
        }
        System.out.println("Таблицы равны: " + table.equals(copy));
        System.out.println("Хеш-коды равны: " + (table.hashCode() == copy.hashCode()));
        System.out.println("Удаление неверной пары: " + table.remove("two", 100));
        System.out.println("Удаление пары two=2: " + table.remove("two", 2));
        System.out.println("После удаления: " + table);

        Iterator<Entry<String, Number>> iterator = table.iterator();
        table.put("three", 3);
        try {
            iterator.next();
        } catch (ConcurrentModificationException exception) {
            System.out.println("Ошибка обхода: " + exception.getMessage());
        }

        HashTable<Integer, Integer> numbers = new HashTable<>();
        for (int number = 0; number < 100; number++) {
            numbers.put(number, number * number);
        }
        System.out.println("После расширения: " + numbers.size()
                + " записей, значение для 99: " + numbers.get(99));
        for (int number = 0; number < 95; number++) {
            numbers.remove(number, number * number);
        }
        System.out.println("После удаления лишних записей: " + numbers.size()
                + ", значение для 99: " + numbers.get(99));
    }
}
