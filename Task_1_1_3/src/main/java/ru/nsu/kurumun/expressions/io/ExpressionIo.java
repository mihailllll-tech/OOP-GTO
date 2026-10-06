package ru.nsu.kurumun.expressions.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.parser.ExpressionParser;

public final class ExpressionIo {

    private ExpressionIo() {
    }

    public static Expression read(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        return ExpressionParser.parse(text);
    }

    public static void write(Path path, Expression expression) throws IOException {
        Files.writeString(path, expression.toString(), StandardCharsets.UTF_8);
    }
}