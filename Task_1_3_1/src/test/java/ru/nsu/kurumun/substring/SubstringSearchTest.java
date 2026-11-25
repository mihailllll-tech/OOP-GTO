package ru.nsu.kurumun.substring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SubstringSearchTest {
    @TempDir
    Path directory;

    @Test
    void findsOverlappingMatchesAfterPartialMismatch() throws IOException {
        Path file = Files.writeString(directory.resolve("overlapping.txt"), "абаабабаб");

        List<Long> positions = new ArrayList<>();

        SubstringSearch.find(file.toString(), "абаб", positions::add);

        assertEquals(List.of(3L, 5L), positions);
    }

    @Test
    void reportsPositionsInUnicodeCodePoints() throws IOException {
        Path file = Files.writeString(directory.resolve("unicode.txt"), "а😀ба😀б");
        List<Long> positions = new ArrayList<>();

        SubstringSearch.find(file.toString(), "😀б", positions::add);

        assertEquals(List.of(1L, 4L), positions);
    }
}
