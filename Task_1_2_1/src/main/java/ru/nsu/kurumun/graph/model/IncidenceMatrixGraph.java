package ru.nsu.kurumun.graph.model;

import java.util.ArrayList;
import java.util.List;

public class IncidenceMatrixGraph<T> implements Graph<T> {
    private static final int OUTGOING = -1;
    private static final int INCOMING = 1;
    private static final int LOOP = 2;

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

        int edgeCount = matrix.isEmpty() ? 0 : matrix.get(0).size();
        List<Integer> row = new ArrayList<>();
        for (int i = 0; i < edgeCount; i++) {
            row.add(0);
        }
        vertices.add(vertex);
        matrix.add(row);
    }

    @Override
    public void removeVertex(T vertex) {
        int index = vertices.indexOf(vertex);
        if (index < 0) {
            return;
        }

        for (int edge = matrix.get(index).size() - 1; edge >= 0; edge--) {
            if (matrix.get(index).get(edge) != 0) {
                for (List<Integer> row : matrix) {
                    row.remove(edge);
                }
            }
        }
        vertices.remove(index);
        matrix.remove(index);
    }

    @Override
    public void addEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        if (findEdgeIndex(fromIndex, toIndex) >= 0) {
            return;
        }

        for (List<Integer> row : matrix) {
            row.add(0);
        }
        int edge = matrix.get(fromIndex).size() - 1;
        if (fromIndex == toIndex) {
            matrix.get(fromIndex).set(edge, LOOP);
        } else {
            matrix.get(fromIndex).set(edge, OUTGOING);
            matrix.get(toIndex).set(edge, INCOMING);
        }
    }

    @Override
    public void removeEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }

        int edge = findEdgeIndex(fromIndex, toIndex);
        if (edge >= 0) {
            for (List<Integer> row : matrix) {
                row.remove(edge);
            }
        }
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
                && findEdgeIndex(fromIndex, toIndex) >= 0;
    }

    @Override
    public List<T> getVertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public List<T> getNeighbors(T vertex) {
        if (!vertices.contains(vertex)) {
            throw new IllegalArgumentException("Вершина не найдена: " + vertex);
        }

        List<T> neighbors = new ArrayList<>();
        for (T candidate : vertices) {
            if (containsEdge(vertex, candidate)) {
                neighbors.add(candidate);
            }
        }
        return neighbors;
    }

    private int findEdgeIndex(int fromIndex, int toIndex) {
        for (int edge = 0; edge < matrix.get(fromIndex).size(); edge++) {
            if (fromIndex == toIndex) {
                if (matrix.get(fromIndex).get(edge) == LOOP) {
                    return edge;
                }
            } else if (matrix.get(fromIndex).get(edge) == OUTGOING
                    && matrix.get(toIndex).get(edge) == INCOMING) {
                return edge;
            }
        }
        return -1;
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
