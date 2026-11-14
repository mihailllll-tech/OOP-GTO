package ru.nsu.kurumun.substring;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

public final class SubstringSearch {
    private SubstringSearch() {
    }

    public static List<Long> find(String fileName, String substring) throws IOException {
        List<Long> positions = new ArrayList<>();
        find(fileName, substring, positions::add);
        return positions;
    }

    public static void find(String fileName, String substring, LongConsumer onMatch)
            throws IOException {
        if (substring.isEmpty()) {
            throw new IllegalArgumentException("Искомая строка должна быть непустой");
        }

        int[] pattern = substring.codePoints().toArray();
        int[] prefix = buildPrefix(pattern);
        int matched = 0;
        long position = 0;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName),
                StandardCharsets.UTF_8)) {
            int current;
            while ((current = readCodePoint(reader)) != -1) {
                matched = advance(pattern, prefix, matched, current);
                if (matched == pattern.length) {
                    onMatch.accept(position - pattern.length + 1);
                    matched = prefix[matched - 1];
                }
                position++;
            }
        }
    }

    private static int[] buildPrefix(int[] pattern) {
        int[] prefix = new int[pattern.length];
        for (int i = 1; i < pattern.length; i++) {
            prefix[i] = advance(pattern, prefix, prefix[i - 1], pattern[i]);
        }
        return prefix;
    }

    private static int advance(int[] pattern, int[] prefix, int matched, int current) {
        while (matched > 0 && pattern[matched] != current) {
            matched = prefix[matched - 1];
        }
        if (pattern[matched] == current) {
            matched++;
        }
        return matched;
    }

    private static int readCodePoint(BufferedReader reader) throws IOException {
        int first = reader.read();
        if (first != -1 && Character.isHighSurrogate((char) first)) {
            return Character.toCodePoint((char) first, (char) reader.read());
        }
        return first;
    }
}
