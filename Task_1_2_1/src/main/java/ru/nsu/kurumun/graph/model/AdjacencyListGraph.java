package ru.nsu.kurumun.graph.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ориентированный граф, хранящий списки исходящих соседей в карте.
 *
 * <p>Карта сохраняет порядок добавления вершин.
 *
 * @param <T> тип вершины
 */
public class AdjacencyListGraph<T> implements Graph<T> {
    private final Map<T, List<T>> adjacency = new LinkedHashMap<>();

    /**
     * Создаёт пустой граф.
     */
    public AdjacencyListGraph() {
    }

    /** {@inheritDoc} */
    @Override
    public void addVertex(T vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Вершина не может быть null");
        }
        if (!adjacency.containsKey(vertex)) {
            adjacency.put(vertex, new ArrayList<>());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeVertex(T vertex) {
        adjacency.remove(vertex);
        for (List<T> neighbors : adjacency.values()) {
            neighbors.remove(vertex);
        }
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void removeEdge(T from, T to) {
        if (!adjacency.containsKey(from) || !adjacency.containsKey(to)) {
            throw new IllegalArgumentException("Обе вершины должны быть добавлены в граф");
        }
        adjacency.get(from).remove(to);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsVertex(T vertex) {
        return adjacency.containsKey(vertex);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsEdge(T from, T to) {
        return adjacency.containsKey(from) && adjacency.get(from).contains(to);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getVertices() {
        return new ArrayList<>(adjacency.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getNeighbors(T vertex) {
        if (!adjacency.containsKey(vertex)) {
            throw new IllegalArgumentException("Вершина не найдена: " + vertex);
        }
        return new ArrayList<>(adjacency.get(vertex));
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
        for (T vertex : adjacency.keySet()) {
            result.append(vertex)
                    .append(" -> ")
                    .append(adjacency.get(vertex))
                    .append(System.lineSeparator());
        }
        return result.toString();
    }
}
