package ru.nsu.kurumun.graph.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdjacencyListGraph<T> implements Graph<T> {
    private final Map<T, List<T>> adjacency = new LinkedHashMap<>();

    @Override
    public void addVertex(T vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Вершина не может быть null");
        }
        if (!adjacency.containsKey(vertex)) {
            adjacency.put(vertex, new ArrayList<>());
        }
    }

    @Override
    public void removeVertex(T vertex) {
        adjacency.remove(vertex);
        for (List<T> neighbors : adjacency.values()) {
            neighbors.remove(vertex);
        }
    }

    @Override
    public void addEdge(T from, T to) {
        if (!adjacency.containsKey(from) || !adjacency.containsKey(to)) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        List<T> neighbors = adjacency.get(from);
        if (!neighbors.contains(to)) {
            neighbors.add(to);
        }
    }

    @Override
    public void removeEdge(T from, T to) {
        if (!adjacency.containsKey(from) || !adjacency.containsKey(to)) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        adjacency.get(from).remove(to);
    }

    @Override
    public boolean containsVertex(T vertex) {
        return adjacency.containsKey(vertex);
    }

    @Override
    public boolean containsEdge(T from, T to) {
        return adjacency.containsKey(from) && adjacency.get(from).contains(to);
    }

    @Override
    public List<T> getVertices() {
        return new ArrayList<>(adjacency.keySet());
    }

    @Override
    public List<T> getNeighbors(T vertex) {
        if (!adjacency.containsKey(vertex)) {
            throw new IllegalArgumentException("Вершина не найдена: " + vertex);
        }
        return new ArrayList<>(adjacency.get(vertex));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Graph<?> graph)) {
            return false;
        }
        return Graph.sameStructure(this, graph);
    }

    @Override
    public int hashCode() {
        return Graph.structureHashCode(this);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (T vertex : adjacency.keySet()) {
            result.append(vertex)
                    .append(" -> ")
                    .append(adjacency.get(vertex))
                    .append(System.lineSeparator());
        }
        return result.toString();
    }
}
