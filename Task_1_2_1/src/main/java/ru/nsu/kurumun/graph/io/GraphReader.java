package ru.nsu.kurumun.graph.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import ru.nsu.kurumun.graph.model.Graph;

public final class GraphReader {
    private GraphReader() {
    }

    public static <T> void read(Path path, Graph<T> graph,
                               Function<String, T> vertexParser) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            readGraph(reader, graph, vertexParser);
        }
    }

    public static <T> void readResource(String resourceName, Graph<T> graph,
                                       Function<String, T> vertexParser) throws IOException {
        InputStream stream = GraphReader.class.getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IOException("Ресурс не найден: " + resourceName);
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            readGraph(reader, graph, vertexParser);
        }
    }

    private static <T> void readGraph(BufferedReader reader, Graph<T> graph,
                                     Function<String, T> vertexParser) throws IOException {
        int vertexCount = readVertexCount(reader);
        clearGraph(graph);
        readVertices(reader, graph, vertexParser, vertexCount);
        readEdges(reader, graph, vertexParser, vertexCount + 1);
    }

    private static int readVertexCount(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("Файл пуст");
        }
        int vertexCount = Integer.parseInt(line.trim());
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Количество вершин не может быть отрицательным");
        }
        return vertexCount;
    }

    private static <T> void clearGraph(Graph<T> graph) {
        for (T vertex : new ArrayList<>(graph.getVertices())) {
            graph.removeVertex(vertex);
        }
    }

    private static <T> void readVertices(BufferedReader reader, Graph<T> graph,
                                        Function<String, T> vertexParser, int count)
            throws IOException {
        for (int i = 0; i < count; i++) {
            String name = readVertexName(reader);
            T vertex = vertexParser.apply(name);
            if (graph.containsVertex(vertex)) {
                throw new IllegalArgumentException("Повторяющаяся вершина: " + name);
            }
            graph.addVertex(vertex);
        }
    }

    private static String readVertexName(BufferedReader reader) throws IOException {
        String name = reader.readLine();
        if (name == null) {
            throw new IllegalArgumentException("В файле не хватает вершин");
        }
        name = name.trim();
        if (!name.matches("\\S+")) {
            throw new IllegalArgumentException("Имя вершины должно быть непустым и без пробелов");
        }
        return name;
    }

    private static <T> void readEdges(BufferedReader reader, Graph<T> graph,
                                     Function<String, T> vertexParser, int lineNumber)
            throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (!line.isBlank()) {
                readEdge(line, lineNumber, graph, vertexParser);
            }
        }
    }

    private static <T> void readEdge(String line, int lineNumber, Graph<T> graph,
                                    Function<String, T> vertexParser) {
        List<String> parts = new ArrayList<>(Arrays.asList(line.trim().split("\\s+")));
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
