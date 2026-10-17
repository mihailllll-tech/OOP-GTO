package ru.nsu.kurumun.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface Graph<T> {
    void addVertex(T vertex);
    void removeVertex(T vertex);
    void addEdge(T from, T to);
    void removeEdge(T from, T to);
    boolean containsVertex(T vertex);
    boolean containsEdge(T from, T to);
    List<T> getVertices();
    List<T> getNeighbors(T vertex);

    default void readFromFile(Path path, Function<String, T> vertexParser)
            throws IOException {
        GraphReader.read(path, this, vertexParser);
    }

    static boolean sameStructure(Graph<?> first, Graph<?> second) {
        return structureOf(first).equals(structureOf(second));
    }

    static int structureHashCode(Graph<?> graph) {
        return structureOf(graph).hashCode();
    }

    private static <V> Map<V, Set<V>> structureOf(Graph<V> graph) {
        Map<V, Set<V>> structure = new HashMap<>();
        for (V vertex : graph.getVertices()) {
            structure.put(vertex, new HashSet<>(graph.getNeighbors(vertex)));
        }
        return structure;
    }
}
