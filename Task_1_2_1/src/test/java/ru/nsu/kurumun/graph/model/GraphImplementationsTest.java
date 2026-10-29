package ru.nsu.kurumun.graph.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class GraphImplementationsTest {
    static Stream<Graph<String>> graphs() {
        return Stream.of(new AdjacencyListGraph<>(), new AdjacencyMatrixGraph<>(),
                new IncidenceMatrixGraph<>());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void startsEmpty(Graph<String> graph) {
        assertTrue(graph.getVertices().isEmpty());
        assertFalse(graph.containsVertex("missing"));
        assertFalse(graph.containsEdge("missing", "missing"));
        assertEquals("", graph.toString());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void ignoresRepeatedVertices(Graph<String> graph) {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("A");

        assertEquals(List.of("A", "B"), graph.getVertices());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void storesDirectedEdgesWithoutDuplicates(Graph<String> graph) {
        addVertices(graph, List.of("A", "B", "C"));
        graph.addEdge("A", "B");
        graph.addEdge("A", "B");

        assertTrue(graph.containsEdge("A", "B"));
        assertFalse(graph.containsEdge("B", "A"));
        assertFalse(graph.containsEdge("A", "missing"));
        assertFalse(graph.containsEdge("missing", "A"));
        assertEquals(List.of("B"), graph.getNeighbors("A"));
        assertTrue(graph.getNeighbors("C").isEmpty());

        graph.addVertex("D");
        assertTrue(graph.containsEdge("A", "B"));
        assertTrue(graph.getNeighbors("D").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void rejectsAddingEdgesWithMissingEndpoints(Graph<String> graph) {
        graph.addVertex("A");
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> graph.addEdge("missing", "A")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> graph.addEdge("A", "missing"))
        );
        assertTrue(graph.getNeighbors("A").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void addsAndRemovesSelfLoop(Graph<String> graph) {
        addVertices(graph, List.of("A", "B"));
        graph.addEdge("A", "B");
        graph.addEdge("A", "A");
        graph.addEdge("A", "A");

        assertTrue(graph.containsEdge("A", "A"));
        assertEquals(Set.of("A", "B"), new HashSet<>(graph.getNeighbors("A")));
        assertEquals(2, graph.getNeighbors("A").size());

        graph.removeEdge("A", "A");
        graph.removeEdge("A", "A");
        assertFalse(graph.containsEdge("A", "A"));
        assertEquals(List.of("B"), graph.getNeighbors("A"));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void removesOnlyRequestedEdge(Graph<String> graph) {
        addVertices(graph, List.of("A", "B", "C"));
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "A");

        graph.removeEdge("A", "B");
        graph.removeEdge("A", "B");

        assertFalse(graph.containsEdge("A", "B"));
        assertTrue(graph.containsEdge("A", "C"));
        assertTrue(graph.containsEdge("B", "A"));
        assertEquals(List.of("C"), graph.getNeighbors("A"));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void removesIncidentEdgesAndPreservesUnrelatedEdges(Graph<String> graph) {
        addVertices(graph, List.of("A", "B", "C", "D"));
        graph.addEdge("A", "B");
        graph.addEdge("B", "A");
        graph.addEdge("B", "C");
        graph.addEdge("C", "B");
        graph.addEdge("B", "B");
        graph.addEdge("A", "D");
        graph.addEdge("D", "C");

        graph.removeVertex("B");
        graph.removeVertex("missing");

        assertEquals(List.of("A", "C", "D"), graph.getVertices());
        assertFalse(graph.containsVertex("B"));
        assertEquals(List.of("D"), graph.getNeighbors("A"));
        assertEquals(List.of("C"), graph.getNeighbors("D"));
        assertTrue(graph.getNeighbors("C").isEmpty());

        graph.removeVertex("A");
        assertTrue(graph.containsEdge("D", "C"));
        graph.removeVertex("D");
        assertEquals(List.of("C"), graph.getVertices());
        assertTrue(graph.getNeighbors("C").isEmpty());
        graph.removeVertex("C");
        assertTrue(graph.getVertices().isEmpty());

        graph.addVertex("new");
        assertTrue(graph.getNeighbors("new").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void returnsIndependentCopiesOfLists(Graph<String> graph) {
        addVertices(graph, List.of("A", "B"));
        graph.addEdge("A", "B");

        graph.getVertices().clear();
        graph.getNeighbors("A").clear();

        assertEquals(List.of("A", "B"), graph.getVertices());
        assertEquals(List.of("B"), graph.getNeighbors("A"));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void comparesStructureAcrossRepresentationsAndInsertionOrders(Graph<String> graph) {
        addVertices(graph, List.of("A", "B", "C"));
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        assertTrue(graph.equals(graph));
        assertFalse(graph.equals(null));
        assertFalse(graph.equals("not a graph"));

        for (Graph<String> other : graphs().toList()) {
            addVertices(other, List.of("C", "B", "A"));
            other.addEdge("B", "C");
            other.addEdge("A", "B");

            assertEquals(graph, other);
            assertEquals(other, graph);
            assertEquals(graph.hashCode(), other.hashCode());

            other.addEdge("C", "C");
            assertNotEquals(graph, other);
            other.removeEdge("C", "C");
            other.addVertex("isolated");
            assertNotEquals(graph, other);
        }
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void equalityDistinguishesEdgeDirection(Graph<String> graph) {
        addVertices(graph, List.of("A", "B"));
        graph.addEdge("A", "B");
        Graph<String> reversed = new AdjacencyListGraph<>();
        addVertices(reversed, List.of("A", "B"));
        reversed.addEdge("B", "A");

        assertNotEquals(graph, reversed);
        assertNotEquals(reversed, graph);
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void printsVerticesAndTheirOutgoingNeighbors(Graph<String> graph) {
        addVertices(graph, List.of("A", "B"));
        graph.addEdge("A", "B");

        assertEquals("A -> [B]" + System.lineSeparator()
                + "B -> []" + System.lineSeparator(), graph.toString());
    }

    @Test
    void supportsIntegerVerticesAsValuesRatherThanListIndices() {
        for (Graph<Integer> graph : Stream.<Graph<Integer>>of(
                new AdjacencyListGraph<>(), new AdjacencyMatrixGraph<>(),
                new IncidenceMatrixGraph<>()).toList()) {
            graph.addVertex(10);
            graph.addVertex(20);
            graph.addVertex(30);
            graph.addEdge(10, 20);
            graph.addEdge(20, 30);
            graph.addEdge(10, 30);

            graph.removeVertex(20);

            assertEquals(List.of(10, 30), graph.getVertices());
            assertTrue(graph.containsEdge(10, 30));
            assertEquals(List.of(30), graph.getNeighbors(10));
        }
    }

    private static void addVertices(Graph<String> graph, List<String> vertices) {
        for (String vertex : vertices) {
            graph.addVertex(vertex);
        }
    }
}
