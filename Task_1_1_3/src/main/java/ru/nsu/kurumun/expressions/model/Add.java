package ru.nsu.kurumun.expressions.model;

import java.util.Map;
import java.util.Objects;

public final class Add extends Expression {

    public Expression left;

    public Expression right;

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