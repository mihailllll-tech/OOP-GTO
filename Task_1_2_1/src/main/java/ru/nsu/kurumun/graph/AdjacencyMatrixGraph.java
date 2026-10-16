package ru.nsu.kurumun.graph;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrixGraph<T> implements Graph<T> {
    private final List<T> vertices = new ArrayList<>();
    private final List<List<Integer>> matrix = new ArrayList<>();

    @Override
    public void addVertex(T vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Вершина не может быть нулл");
        }
        if (vertices.contains(vertex)) {
            return;
        }

        vertices.add(vertex);
        for (List<Integer> row : matrix) {
            row.add(0);
        }

        List<Integer> newRow = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            newRow.add(0);
        }
        matrix.add(newRow);
    }

    @Override
    public void removeVertex(T vertex) {
        int index = vertices.indexOf(vertex);
        if (index < 0) {
            return;
        }

        vertices.remove(index);
        matrix.remove(index);
        for (List<Integer> row : matrix) {
            row.remove(index);
        }
    }

    @Override
    public void addEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        matrix.get(fromIndex).set(toIndex, 1);
    }

    @Override
    public void removeEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        matrix.get(fromIndex).set(toIndex, 0);
    }

    @Override
    public boolean containsVertex(T vertex) {
        return vertices.contains(vertex);
    }

    @Override
    public boolean containsEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        return fromIndex >= 0 && toIndex >= 0
                && matrix.get(fromIndex).get(toIndex) == 1;
    }

    @Override
    public List<T> getVertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public List<T> getNeighbors(T vertex) {
        int index = vertices.indexOf(vertex);
        if (index < 0) {
            throw new IllegalArgumentException("Вершина не найдена: " + vertex);
        }

        List<T> neighbors = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (matrix.get(index).get(i) == 1) {
                neighbors.add(vertices.get(i));
            }
        }
        return neighbors;
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
        for (T vertex : vertices) {
            result.append(vertex)
                    .append(" -> ")
                    .append(getNeighbors(vertex))
                    .append(System.lineSeparator());
        }
        return result.toString();
    }
}
