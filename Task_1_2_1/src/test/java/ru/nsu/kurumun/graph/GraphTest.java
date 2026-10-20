package ru.nsu.kurumun.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.nsu.kurumun.graph.algorithm.TopologicalSort;
import ru.nsu.kurumun.graph.io.GraphReader;
import ru.nsu.kurumun.graph.model.AdjacencyListGraph;
import ru.nsu.kurumun.graph.model.AdjacencyMatrixGraph;
import ru.nsu.kurumun.graph.model.Graph;
import ru.nsu.kurumun.graph.model.IncidenceMatrixGraph;

class GraphTest {
    @Test
    void readsGraphFromResource() throws IOException {
        for (Graph<String> graph : createGraphs()) {
            graph.addVertex("old");
            graph.addEdge("old", "old");

            GraphReader.readResource("/graph.txt", graph, text -> text);

            assertEquals(4, graph.getVertices().size());
            assertEquals(Set.of("A", "B", "C", "D"), new HashSet<>(graph.getVertices()));
            assertEquals(2, graph.getNeighbors("A").size());
            assertEquals(Set.of("B", "C"), new HashSet<>(graph.getNeighbors("A")));
            assertEquals(List.of("D"), graph.getNeighbors("B"));
            assertEquals(List.of("D"), graph.getNeighbors("C"));
            assertTrue(graph.getNeighbors("D").isEmpty());
            assertFalse(graph.containsVertex("old"));
        }
    }

    @Test
    void topologicalSortRespectsEdgeDirections() {
        for (Graph<String> graph : createGraphs()) {
            for (String vertex : List.of("D", "C", "B", "A", "E")) {
                graph.addVertex(vertex);
            }
            graph.addEdge("A", "B");
            graph.addEdge("A", "C");
            graph.addEdge("B", "D");
            graph.addEdge("C", "D");

            List<String> order = TopologicalSort.sort(graph);

            assertEquals(5, order.size());
            assertEquals(Set.of("A", "B", "C", "D", "E"), new HashSet<>(order));
            assertTrue(order.indexOf("A") < order.indexOf("B"));
            assertTrue(order.indexOf("A") < order.indexOf("C"));
            assertTrue(order.indexOf("B") < order.indexOf("D"));
            assertTrue(order.indexOf("C") < order.indexOf("D"));
        }
    }

    private static List<Graph<String>> createGraphs() {
        List<Graph<String>> graphs = new ArrayList<>();
        graphs.add(new AdjacencyListGraph<>());
        graphs.add(new AdjacencyMatrixGraph<>());
        graphs.add(new IncidenceMatrixGraph<>());
        return graphs;
    }
}
