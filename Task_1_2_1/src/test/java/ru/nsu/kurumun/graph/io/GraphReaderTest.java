package ru.nsu.kurumun.graph.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.kurumun.graph.model.AdjacencyListGraph;
import ru.nsu.kurumun.graph.model.AdjacencyMatrixGraph;
import ru.nsu.kurumun.graph.model.Graph;
import ru.nsu.kurumun.graph.model.IncidenceMatrixGraph;

class GraphReaderTest {
    @TempDir
    Path directory;

    static Stream<Graph<String>> graphs() {
        return Stream.of(new AdjacencyListGraph<>(), new AdjacencyMatrixGraph<>(),
                new IncidenceMatrixGraph<>());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void readsUtf8FileAndReplacesOldGraph(Graph<String> graph) throws IOException {
        graph.addVertex("old");
        graph.addEdge("old", "old");
        Path path = writeGraph(" 3 \n А \nБ\nВ\n\nА   Б\nБ\tВ\n  \n");

        GraphReader.read(path, graph, text -> text);

        assertEquals(List.of("А", "Б", "В"), graph.getVertices());
        assertEquals(List.of("Б"), graph.getNeighbors("А"));
        assertEquals(List.of("В"), graph.getNeighbors("Б"));
        assertTrue(graph.getNeighbors("В").isEmpty());
        assertFalse(graph.containsVertex("old"));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void acceptsZeroVertices(Graph<String> graph) throws IOException {
        graph.addVertex("old");

        GraphReader.read(writeGraph("0\n\n"), graph, text -> text);

        assertTrue(graph.getVertices().isEmpty());
    }

    @Test
    void readsIntegerVertices() throws IOException {
        for (Graph<Integer> graph : Stream.<Graph<Integer>>of(
                new AdjacencyListGraph<>(), new AdjacencyMatrixGraph<>(),
                new IncidenceMatrixGraph<>()).toList()) {
            GraphReader.read(writeGraph("2\n10\n20\n10 20\n"), graph, Integer::parseInt);
            assertEquals(List.of(10, 20), graph.getVertices());
            assertTrue(graph.containsEdge(10, 20));
        }
    }

    @Test
    void reportsMissingFile() {
        Graph<String> graph = new AdjacencyListGraph<>();
        assertThrows(IOException.class,
                () -> GraphReader.read(directory.resolve("missing.txt"), graph, text -> text));
    }

    private Path writeGraph(String content) throws IOException {
        return Files.writeString(directory.resolve("graph.txt"), content, StandardCharsets.UTF_8);
    }
}
