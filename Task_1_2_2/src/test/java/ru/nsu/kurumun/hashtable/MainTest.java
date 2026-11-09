package ru.nsu.kurumun.hashtable;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void demonstratesOperationsAndCollisions() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.out;
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            try {
                Main.demonstrateOperations();
            } finally {
                System.setOut(previous);
            }
        }

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.startsWith("1.0" + System.lineSeparator()));
        assertTrue(text.contains("Коллизия Aa и BB: 10, 20"));
        assertTrue(text.contains("Таблицы равны: true"));
        assertTrue(text.contains("Хеш-коды равны: true"));
        assertTrue(text.contains("Удаление неверной пары: false"));
        assertTrue(text.contains("Удаление пары two=2: true"));
        assertTrue(text.contains("Ошибка обхода: Таблица изменена во время обхода"));
    }
}
