package ru.nsu.kurumun.expressions.model;

import java.util.Map;
import java.util.Objects;

public final class Div extends Expression {

    public Expression left;

    public Expression right;

    public Div(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

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