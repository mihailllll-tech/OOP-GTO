package ru.nsu.kurumun.graph.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Ориентированный граф, хранящий матрицу смежности.
 *
 * <p>Строка задаёт начало ребра, столбец — конец; единица означает наличие ребра.
 *
 * @param <T> тип вершины
 */
public class AdjacencyMatrixGraph<T> implements Graph<T> {
    private final List<T> vertices = new ArrayList<>();
    private final List<List<Integer>> matrix = new ArrayList<>();

    /**
     * Создаёт пустой граф.
     */
    public AdjacencyMatrixGraph() {
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void addEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        matrix.get(fromIndex).set(toIndex, 1);
    }

    /** {@inheritDoc} */
    @Override
    public void removeEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        matrix.get(fromIndex).set(toIndex, 0);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsVertex(T vertex) {
        return vertices.contains(vertex);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsEdge(T from, T to) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        return fromIndex >= 0 && toIndex >= 0
                && matrix.get(fromIndex).get(toIndex) == 1;
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getVertices() {
        return new ArrayList<>(vertices);
    }

    /** {@inheritDoc} */
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

    /**
     * Сравнивает вершины и направленные рёбра с другим графом.
     *
     * @param object сравниваемый объект
     * @return {@code true}, если объект является графом с той же структурой
     */
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

    /**
     * Возвращает хеш-код структуры графа, согласованный с {@code equals}.
     *
     * @return хеш-код вершин и рёбер
     */
    @Override
    public int hashCode() {
        return Graph.structureHashCode(this);
    }

    /**
     * Возвращает строки вида «вершина -&gt; список соседей».
     *
     * @return представление графа; для пустого графа — пустая строка
     */
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
