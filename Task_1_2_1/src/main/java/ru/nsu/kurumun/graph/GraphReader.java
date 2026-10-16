package ru.nsu.kurumun.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class GraphReader {
    private GraphReader() {
    }

    public static <T> void read(Path path, Graph<T> graph,
                                Function<String, T> vertexParser) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IllegalArgumentException("Файл пуст");
            }

            int vertexCount = Integer.parseInt(firstLine.trim());
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Количество вершин не может быть отрицательным");
            }

            for (T vertex : new ArrayList<>(graph.getVertices())) {
                graph.removeVertex(vertex);
            }

            for (int i = 0; i < vertexCount; i++) {
                String name = reader.readLine();
                if (name == null) {
                    throw new IllegalArgumentException("В файле не хватает вершин");
                }
                name = name.trim();
                if (!name.matches("\\S+")) {
                    throw new IllegalArgumentException("Имя вершины должно быть непустым и без пробелов");
                }

                T vertex = vertexParser.apply(name);
                if (graph.containsVertex(vertex)) {
                    throw new IllegalArgumentException("Повторяющаяся вершина: " + name);
                }
                graph.addVertex(vertex);
            }

            String line;
            int lineNumber = vertexCount + 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                List<String> parts = new ArrayList<>(
                        Arrays.asList(line.trim().split("\\s+"))
                );
                if (parts.size() != 2) {
                    throw new IllegalArgumentException(
                            "Строка " + lineNumber + ": ожидаются две вершины ребра"
                    );
                }

                T from = vertexParser.apply(parts.get(0));
                T to = vertexParser.apply(parts.get(1));
                graph.addEdge(from, to);
            }
        }
    }
}