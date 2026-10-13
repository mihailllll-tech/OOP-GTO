package ru.nsu.kurumun.expressions.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.model.Number;
import ru.nsu.kurumun.expressions.model.Sub;
import ru.nsu.kurumun.expressions.model.Variable;

class ExpressionIoTest {

    @TempDir
    Path directory;

    @Test
    void readsExpressionFromFile() throws IOException {
        Path input = directory.resolve("input.txt");
        Files.writeString(
                input,
                " (3 + (2 * x))\n",
                StandardCharsets.UTF_8
        );

        Expression expression = ExpressionIo.read(input);

        assertEquals("(3+(2*x))", expression.toString());
        assertEquals(23, expression.eval("x = 10"));
    }

    @Test
    void writesExpressionToFile() throws IOException {
        Path output = directory.resolve("output.txt");
        Expression expression = new Sub(
                new Variable("speed"),
                new Number(2)
        );

        ExpressionIo.write(output, expression);

        String saved = Files.readString(output, StandardCharsets.UTF_8);
        assertEquals("(speed-2)", saved);
    }

    @Test
    void rejectsMissingInputFile() {
        Path missing = directory.resolve("missing.txt");

        assertThrows(IOException.class, () -> ExpressionIo.read(missing));
    }
}