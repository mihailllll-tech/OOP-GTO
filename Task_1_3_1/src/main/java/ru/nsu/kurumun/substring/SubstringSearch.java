package ru.nsu.kurumun.substring;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

/**
 * Ищет подстроки в UTF-8-файлах алгоритмом Кнута — Морриса — Пратта.
 */
public final class SubstringSearch {
    private SubstringSearch() {
    }

    /**
     * Собирает позиции всех вхождений, включая перекрывающиеся, в список.
     *
     * @param fileName путь к текстовому файлу
     * @param substring непустая подстрока для поиска с учётом регистра
     * @return индексы начала вхождений в кодовых точках Unicode, начиная с нуля
     * @throws IllegalArgumentException если подстрока пуста
     * @throws IOException если файл не удалось открыть или прочитать
     */
    public static List<Long> find(String fileName, String substring) throws IOException {
        List<Long> positions = new ArrayList<>();
        find(fileName, substring, positions::add);
        return positions;
    }

    /**
     * Читает файл порциями и передаёт каждую найденную позицию сразу.
     * Учитывает перекрывающиеся вхождения.
     *
     * @param fileName путь к текстовому файлу
     * @param substring непустая подстрока для поиска с учётом регистра
     * @param onMatch действие, получающее индекс начала вхождения в кодовых точках Unicode,
     *     начиная с нуля
     * @throws IllegalArgumentException если подстрока пуста
     * @throws IOException если файл не удалось открыть или прочитать
     */
    public static void find(String fileName, String substring, LongConsumer onMatch)
            throws IOException {
        if (substring.isEmpty()) {
            throw new IllegalArgumentException("Искомая строка должна быть непустой");
        }

        List<Integer> pattern = new ArrayList<>();
        substring.codePoints().forEachOrdered(pattern::add);
        List<Integer> prefix = buildPrefix(pattern);
        int patternLength = pattern.size();
        int matched = 0;
        long position = 0;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName),
                StandardCharsets.UTF_8)) {
            int current;
            while ((current = readCodePoint(reader)) != -1) {
                matched = advance(pattern, prefix, matched, current);
                if (matched == patternLength) {
                    onMatch.accept(position - patternLength + 1);
                    matched = prefix.get(matched - 1);
                }
                position++;
            }
        }
    }

    /**
     * Для каждой начальной части шаблона вычисляет длину её самого длинного
     * собственного префикса, который совпадает с её суффиксом.
     *
     * @param pattern кодовые точки шаблона
     * @return значения префиксной функции в порядке символов шаблона
     */
    private static List<Integer> buildPrefix(List<Integer> pattern) {
        List<Integer> prefix = new ArrayList<>(pattern.size());
        prefix.add(0);
        for (int i = 1; i < pattern.size(); i++) {
            prefix.add(advance(pattern, prefix, prefix.get(i - 1), pattern.get(i)));
        }
        return prefix;
    }

    /**
     * Обновляет число совпавших символов после чтения очередной кодовой точки.
     *
     * @param pattern кодовые точки шаблона
     * @param prefix значения префиксной функции
     * @param matched число символов шаблона, совпавших перед текущим символом
     * @param current текущая кодовая точка
     * @return новое число совпавших символов
     */
    private static int advance(List<Integer> pattern, List<Integer> prefix,
                               int matched, int current) {
        while (matched > 0 && pattern.get(matched) != current) {
            matched = prefix.get(matched - 1);
        }
        if (pattern.get(matched) == current) {
            matched++;
        }
        return matched;
    }

    /**
     * Читает кодовую точку и при необходимости объединяет суррогатную пару.
     *
     * @param reader источник декодированных символов
     * @return следующая кодовая точка или -1 в конце файла
     * @throws IOException если при чтении произошла ошибка
     */
    private static int readCodePoint(BufferedReader reader) throws IOException {
        int first = reader.read();
        if (first != -1 && Character.isHighSurrogate((char) first)) {
            return Character.toCodePoint((char) first, (char) reader.read());
        }
        return first;
    }
}
