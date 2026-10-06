package ru.nsu.kurumun.expressions.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Математическое выражение.
 */
public abstract class Expression {

    /**
     * Режим split, сохраняющий пустые части в конце строки.
     */
    private static final int SPLIT_KEEP_EMPTY_PARTS = -1;

    /**
     * Инициализирует общую часть выражения.
     */
    protected Expression() {
    }

    /**
     * Вычисляет значение выражения по присваиваниям, разделённым точкой с запятой.
     *
     * @param assignments значения переменных, например {@code "x = 10; y = 13"}
     * @return вычисленное значение
     * @throws IllegalArgumentException если строка равна null, имеет неверный формат,
     *         содержит повторяющиеся имена или некорректные целочисленные значения
     */
    public final int eval(String assignments) {
        if (assignments == null) {
            throw new IllegalArgumentException("Assignments must not be null");
        }

        Map<String, Integer> variables = new HashMap<>();

        if (assignments.isBlank()) {
            return eval(variables);
        }

        List<String> entries = new ArrayList<>(
                Arrays.asList(assignments.split(";", SPLIT_KEEP_EMPTY_PARTS))
        );

        for (String assignment : entries) {
            List<String> parts = new ArrayList<>(
                    Arrays.asList(assignment.split("=", SPLIT_KEEP_EMPTY_PARTS))
            );

            if (parts.size() != 2 || parts.get(0).isBlank()) {
                throw new IllegalArgumentException("Invalid assignment: " + assignment);
            }

            String name = parts.get(0).trim();
            int value = Integer.parseInt(parts.get(1).trim());

            if (variables.putIfAbsent(name, value) != null) {
                throw new IllegalArgumentException("Duplicate variable: " + name);
            }
        }

        return eval(variables);
    }

    /**
     * Вычисляет значение выражения с указанными значениями переменных.
     *
     * @param variables имена переменных и их значения
     * @return вычисленное значение
     */
    public abstract int eval(Map<String, Integer> variables);

    /**
     * Создаёт производную, не изменяя исходное выражение.
     *
     * @param variable переменная, по которой выполняется дифференцирование
     * @return выражение производной
     */
    public abstract Expression derivative(String variable);

    /**
     * Возвращает строковое представление выражения.
     *
     * @return выражение в виде строки
     */
    @Override
    public abstract String toString();

    /**
     * Выводит выражение в консоль.
     */
    public void print() {
        System.out.println(this);
    }
}
