package ru.nsu.kurumun.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph<String> listGraph = new AdjacencyListGraph<>();
        Graph<String> adjacencyMatrixGraph = new AdjacencyMatrixGraph<>();
        Graph<String> incidenceMatrixGraph = new IncidenceMatrixGraph<>();

        List<Graph<String>> graphs = new ArrayList<>();
        graphs.add(listGraph);
        graphs.add(adjacencyMatrixGraph);
        graphs.add(incidenceMatrixGraph);

        try {
            for (Graph<String> graph : graphs) {
                graph.readFromFile(Path.of("graph.txt"), text -> text);
                System.out.println("Реализация: " + graph.getClass().getSimpleName());
                System.out.print(graph);
                System.out.println("Топологический порядок: " + TopologicalSort.sort(graph));
                System.out.println();
            }

            System.out.println("Список и матрица смежности равны: "
                    + listGraph.equals(adjacencyMatrixGraph));
            System.out.println("Список и матрица инцидентности равны: "
                    + listGraph.equals(incidenceMatrixGraph));
            System.out.println("Матрицы смежности и инцидентности равны: "
                    + adjacencyMatrixGraph.equals(incidenceMatrixGraph));
            System.out.println("Хеш-коды всех трёх графов равны: "
                    + (listGraph.hashCode() == adjacencyMatrixGraph.hashCode()
                    && listGraph.hashCode() == incidenceMatrixGraph.hashCode()));
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Ошибка: " + exception.getMessage());
        }
    }
}
