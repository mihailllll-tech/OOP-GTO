package ru.nsu.kurumun.graph.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.kurumun.graph.model.AdjacencyListGraph;
import ru.nsu.kurumun.graph.model.AdjacencyMatrixGraph;
import ru.nsu.kurumun.graph.model.Graph;
import ru.nsu.kurumun.graph.model.IncidenceMatrixGraph;

class TopologicalSortTest {
    static Stream<Graph<String>> graphs() {
        return Stream.of(new AdjacencyListGraph<>(), new AdjacencyMatrixGraph<>(),
                new IncidenceMatrixGraph<>());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void sortsEmptyGraph(Graph<String> graph) {
        assertTrue(TopologicalSort.sort(graph).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void preservesGraphWhileSortingDisconnectedComponents(Graph<String> graph) {
        for (String vertex : List.of("D", "C", "B", "A", "isolated")) {
            graph.addVertex(vertex);
        }
        graph.addEdge("A", "B");
        graph.addEdge("C", "D");
        List<String> verticesBefore = graph.getVertices();
        Map<String, List<String>> neighborsBefore = new HashMap<>();
        for (String vertex : verticesBefore) {
            neighborsBefore.put(vertex, graph.getNeighbors(vertex));
        }

        List<String> order = TopologicalSort.sort(graph);

        assertEquals(verticesBefore.size(), order.size());
        assertEquals(new HashSet<>(verticesBefore), new HashSet<>(order));
        assertTrue(order.indexOf("A") < order.indexOf("B"));
        assertTrue(order.indexOf("C") < order.indexOf("D"));
        assertEquals(verticesBefore, graph.getVertices());
        for (String vertex : verticesBefore) {
            assertEquals(neighborsBefore.get(vertex), graph.getNeighbors(vertex));
        }
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void rejectsCycleEvenWhenAnotherComponentCanBeSorted(Graph<String> graph) {
        for (String vertex : List.of("A", "B", "C", "isolated")) {
            graph.addVertex(vertex);
        }
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> TopologicalSort.sort(graph));

        assertTrue(exception.getMessage().contains("цикл"));
        assertTrue(graph.containsEdge("C", "A"));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void rejectsSelfLoop(Graph<String> graph) {
        graph.addVertex("A");
        graph.addEdge("A", "A");

        assertThrows(IllegalArgumentException.class, () -> TopologicalSort.sort(graph));
    }
}
