package ru.nsu.kurumun.graph.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import ru.nsu.kurumun.graph.io.GraphReader;

/**
 * Ориентированный граф без повторяющихся рёбер, с поддержкой петель.
 *
 * <p>Вершины сравниваются через {@link Object#equals(Object)}. Их поля,
 * участвующие в равенстве и хешировании, не следует менять после добавления.
 *
 * @param <T> тип вершины
 */
public interface Graph<T> {
    /**
     * Добавляет вершину; повторное добавление не изменяет граф.
     *
     * @param vertex добавляемая вершина
     * @throws IllegalArgumentException если вершина равна {@code null}
     */
    void addVertex(T vertex);

    /**
     * Удаляет вершину и все связанные с ней рёбра.
     * Отсутствие вершины не изменяет граф.
     *
     * @param vertex удаляемая вершина
     */
    void removeVertex(T vertex);

    /**
     * Добавляет направленное ребро, если его ещё нет.
     * Совпадающие начало и конец образуют петлю.
     *
     * @param from начало ребра
     * @param to конец ребра
     * @throws IllegalArgumentException если хотя бы одной вершины нет в графе
     */
    void addEdge(T from, T to);

    /**
     * Удаляет направленное ребро; отсутствие ребра не изменяет граф.
     *
     * @param from начало ребра
     * @param to конец ребра
     * @throws IllegalArgumentException если хотя бы одной вершины нет в графе
     */
    void removeEdge(T from, T to);

    /**
     * Проверяет наличие вершины.
     *
     * @param vertex искомая вершина
     * @return {@code true}, если вершина присутствует
     */
    boolean containsVertex(T vertex);

    /**
     * Проверяет наличие направленного ребра.
     *
     * @param from начало ребра
     * @param to конец ребра
     * @return {@code true}, если ребро существует; иначе {@code false},
     *         в том числе при отсутствии одной из вершин
     */
    boolean containsEdge(T from, T to);

    /**
     * Возвращает отдельный список вершин в порядке их добавления.
     *
     * <p>Список можно изменять независимо от графа; сами вершины не копируются.
     *
     * @return поверхностная копия списка вершин
     */
    List<T> getVertices();

    /**
     * Возвращает вершины, в которые идут исходящие рёбра указанной вершины.
     * Порядок соседей зависит от реализации.
     *
     * @param vertex исходная вершина
     * @return отдельный список соседей; сами вершины не копируются
     * @throws IllegalArgumentException если вершины нет в графе
     */
    List<T> getNeighbors(T vertex);

    /**
     * Заменяет содержимое графа данными из текстового файла.
     *
     * <p>Формат описан в {@link GraphReader}. При ошибке после начала чтения
     * граф может остаться заполненным частично.
     *
     * @param path путь к файлу в кодировке UTF-8
     * @param vertexParser преобразование имени вершины в объект
     * @throws IOException если файл недоступен или произошла ошибка чтения
     * @throws IllegalArgumentException если данные имеют неверный формат
     */
    default void readFromFile(Path path, Function<String, T> vertexParser)
            throws IOException {
        GraphReader.read(path, this, vertexParser);
    }

    /**
     * Сравнивает вершины и направленные рёбра независимо от способа хранения.
     * Порядок добавления вершин и соседей не влияет на результат.
     *
     * @param first первый граф
     * @param second второй граф
     * @return {@code true}, если вершины и рёбра совпадают
     */
    static boolean sameStructure(Graph<?> first, Graph<?> second) {
        return structureOf(first).equals(structureOf(second));
    }

    /**
     * Вычисляет хеш-код по вершинам и множествам их исходящих соседей.
     *
     * @param graph граф
     * @return хеш-код, согласованный с {@link #sameStructure(Graph, Graph)}
     */
    static int structureHashCode(Graph<?> graph) {
        return structureOf(graph).hashCode();
    }

    /**
     * Строит представление графа, не зависящее от порядка обхода.
     *
     * @param graph исходный граф
     * @param <V> тип вершины
     * @return соответствие вершин множествам соседей
     */
    private static <V> Map<V, Set<V>> structureOf(Graph<V> graph) {
        Map<V, Set<V>> structure = new HashMap<>();
        for (V vertex : graph.getVertices()) {
            structure.put(vertex, new HashSet<>(graph.getNeighbors(vertex)));
        }
        return structure;
    }
}
