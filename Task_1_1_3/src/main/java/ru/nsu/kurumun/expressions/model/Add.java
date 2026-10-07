package ru.nsu.kurumun.expressions.model;

import java.util.Map;
import java.util.Objects;

/**
 * Сумма двух выражений.
 */
public final class Add extends Expression {

    /**
     * Левый операнд.
     */
    private final Expression left;

    /**
     * Правый операнд.
     */
    private final Expression right;

    /**
     * Создаёт выражение сложения.
     *
     * @param left левый операнд
     * @param right правый операнд
     * @throws NullPointerException если хотя бы один операнд равен null
     */
    public Add(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) + right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        return new Add(
                left.derivative(variable),
                right.derivative(variable)
        );
    }

    @Override
    public String toString() {
        return "(" + left + "+" + right + ")";
    }
}