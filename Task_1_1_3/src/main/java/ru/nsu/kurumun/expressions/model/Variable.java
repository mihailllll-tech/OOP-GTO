package ru.nsu.kurumun.expressions.model;

import java.util.Map;

/**
 * Выражение, представляющее именованную переменную.
 */
public final class Variable extends Expression {

    private final String name;

    /**
     * Создаёт выражение-переменную.
     *
     * @param name имя переменной
     * @throws IllegalArgumentException если имя равно null, пусто или состоит из пробелов
     */
    public Variable(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variable name must not be blank");
        }
        this.name = name;
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        Integer value = variables.get(name);
        if (value == null) {
            throw new IllegalArgumentException("No value for variable: " + name);
        }
        return value;
    }

    @Override
    public Expression derivative(String variable) {
        if (name.equals(variable)) {
            return new Number(1);
        }
        return new Number(0);
    }

    @Override
    public String toString() {
        return name;
    }
}