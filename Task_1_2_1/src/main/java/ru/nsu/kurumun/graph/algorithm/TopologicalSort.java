package ru.nsu.kurumun.graph.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.nsu.kurumun.graph.model.Graph;

/**
 * Топологическая сортировка ориентированного графа алгоритмом Кана.
 */
public final class TopologicalSort {
    private TopologicalSort() {
    }

    /**
     * Строит порядок, в котором начало каждого ребра предшествует его концу.
     *
     * <p>Исходный граф не изменяется. При нескольких допустимых порядках
     * результат зависит от порядка вершин и соседей в реализации графа.
     *
     * @param graph исходный граф
     * @param <T> тип вершины
     * @return список всех вершин в топологическом порядке
     * @throws IllegalArgumentException если граф содержит направленный цикл,
     *         включая петлю
     */
    public static <T> List<T> sort(Graph<T> graph) {
        List<T> vertices = graph.getVertices();
        Map<T, Integer> incoming = new HashMap<>();

        for (T vertex : vertices) {
            incoming.put(vertex, 0);
        }

        for (T vertex : vertices) {
            for (T neighbor : graph.getNeighbors(vertex)) {
                incoming.put(neighbor, incoming.get(neighbor) + 1);
            }
        }

        List<T> ready = new ArrayList<>();
        for (T vertex : vertices) {
            if (incoming.get(vertex) == 0) {
                ready.add(vertex);
            }
        }

        List<T> result = new ArrayList<>();
        while (!ready.isEmpty()) {
            T vertex = ready.remove(0);
            result.add(vertex);

            for (T neighbor : graph.getNeighbors(vertex)) {
                int remaining = incoming.get(neighbor) - 1;
                incoming.put(neighbor, remaining);
                if (remaining == 0) {
                    ready.add(neighbor);
                }
            }
        }

        if (result.size() != vertices.size()) {
            throw new IllegalArgumentException(
                    "Граф содержит цикл: топологическая сортировка невозможна"
            );
        }
        return result;
    }
}
