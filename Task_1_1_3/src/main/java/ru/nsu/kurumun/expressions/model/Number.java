package ru.nsu.kurumun.expressions.model;

import java.util.Map;

/**
 * Выражение, представляющее целочисленную константу.
 */
public final class Number extends Expression {

    private final int value;

    /**
     * Создаёт выражение-константу.
     *
     * @param value значение константы
     */
    public Number(int value) {
        this.value = value;
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return value;
    }

    @Override
    public Expression derivative(String variable) {
        return new Number(0);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}