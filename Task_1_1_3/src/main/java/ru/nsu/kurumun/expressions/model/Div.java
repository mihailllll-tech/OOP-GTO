package ru.nsu.kurumun.expressions.model;

import java.util.Map;
import java.util.Objects;

/**
 * Частное двух выражений.
 */
public final class Div extends Expression {

    /**
     * Числитель.
     */
    private final Expression left;

    /**
     * Знаменатель.
     */
    private final Expression right;

    /**
     * Создаёт выражение деления.
     *
     * @param left числитель
     * @param right знаменатель
     * @throws NullPointerException если хотя бы один операнд равен null
     */
    public Div(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    /**
     * Вычисляет значение частного с помощью целочисленного деления.
     *
     * @param variables имена переменных и их значения
     * @return частное с отброшенной дробной частью
     * @throws ArithmeticException если значение знаменателя равно нулю
     */
    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) / right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        return new Div(
                new Sub(
                        new Mul(left.derivative(variable), right),
                        new Mul(left, right.derivative(variable))
                ),
                new Mul(right, right)
        );
    }

    @Override
    public String toString() {
        return "(" + left + "/" + right + ")";
    }
}