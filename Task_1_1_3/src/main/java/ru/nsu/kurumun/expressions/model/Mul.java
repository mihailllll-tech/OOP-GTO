package ru.nsu.kurumun.expressions.model;

import java.util.Map;
import java.util.Objects;

/**
 * Произведение двух выражений.
 */
public final class Mul extends Expression {

    /**
     * Левый операнд.
     */
    private final Expression left;

    /**
     * Правый операнд.
     */
    private final Expression right;

    /**
     * Создаёт выражение умножения.
     *
     * @param left левый операнд
     * @param right правый операнд
     * @throws NullPointerException если хотя бы один операнд равен null
     */
    public Mul(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) * right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        return new Add(
                new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable))
        );
    }

    @Override
    public String toString() {
        return "(" + left + "*" + right + ")";
    }
}