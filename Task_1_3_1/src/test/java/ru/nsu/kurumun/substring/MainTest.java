package ru.nsu.kurumun.substring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

class MainTest {
    @TempDir
    Path directory;

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void printsNumberOfMatches() throws IOException {
        Path file = Files.writeString(directory.resolve("sample.txt"), "абракадабра");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            try {
                Main.main(new String[] {file.toString(), "бра"});
            } finally {
                System.setOut(original);
            }
        }

        assertEquals("2" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }
}
