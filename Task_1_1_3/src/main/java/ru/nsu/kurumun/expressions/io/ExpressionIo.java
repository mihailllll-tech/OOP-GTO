package ru.nsu.kurumun.expressions.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.parser.ExpressionParser;

/**
 * Читает выражения из текстовых файлов и записывает их в текстовые файлы.
 */
public final class ExpressionIo {

    private ExpressionIo() {
    }

    /**
     * Читает выражение из текстового файла в кодировке UTF-8.
     *
     * @param path путь к входному файлу
     * @return выражение, полученное при разборе строки
     * @throws IOException если не удалось прочитать файл
     * @throws IllegalArgumentException если выражение некорректно
     */
    public static Expression read(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        return ExpressionParser.parse(text);
    }

    /**
     * Записывает выражение в текстовый файл в кодировке UTF-8.
     * Заменяет содержимое файла, если он уже существует.
     *
     * @param path путь к выходному файлу
     * @param expression выражение для записи
     * @throws IOException если не удалось записать файл
     */
    public static void write(Path path, Expression expression) throws IOException {
        Files.writeString(path, expression.toString(), StandardCharsets.UTF_8);
    }
}